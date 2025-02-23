package dev.phoenixofforce.story_game.data;

import dev.phoenixofforce.story_game.connection.messages.*;
import dev.phoenixofforce.story_game.connection.messages.trigger.EndGameTrigger;
import dev.phoenixofforce.story_game.connection.messages.trigger.StartGameTrigger;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Data
@Slf4j
public class Lobby {

    private final List<Player> connectedPlayer = Collections.synchronizedList(new ArrayList<>());
    private final String roomCode;
    private Game game;
    private LobbyState state = LobbyState.LOBBY;

    public void addPlayer(Player player) {
        this.connectedPlayer.add(player);
        sendLobbyChangeUpdate();
    }

    public void removePlayer(Player player) {
        this.connectedPlayer.remove(player);
        sendLobbyChangeUpdate();
    }

    public Player getHost() {
        return connectedPlayer.getFirst();
    }

    public void send(BaseMessage message) {
        connectedPlayer.forEach(p -> message.sendTo(p.getSession()));
    }

    public void sendPersonalized(Function<Player, BaseMessage> messageGenerator) {
        connectedPlayer.forEach(p -> messageGenerator.apply(p).sendTo(p.getSession()));
    }

    public void sendLobbyChangeUpdate() {
        sendPersonalized(player -> {
            LobbyStateMessage stateMessage = new LobbyStateMessage();
            stateMessage.setRoomCode(roomCode);
            stateMessage.setPlayers(connectedPlayer.stream().map(Player::getName).toList());
            stateMessage.setYou(player.getName());
            stateMessage.setHost(connectedPlayer.getFirst().getName());
            return stateMessage;
        });
    }

    public void startGame(Player starter) {
        if(!starter.getSession().equals(getHost().getSession())) return;
        state = LobbyState.GAME;

        send(new StartGameTrigger());
        game = new Game(connectedPlayer.size(), new ArrayList<>(connectedPlayer.stream().toList()));

        sendPersonalized(this::getNextStoryMessage);
    }

    public void acceptStory(Player writer, String story, String teaser) {
        if(game.hasPlayerSubmitted(writer)) return;
        game.addStoryPart(writer, story, teaser);

        if (!game.isRoundOver()) {
            sendGameStateUpdate();
            return;
        }

        if (game.getCurrentRound() >= game.getMaxRounds() - 1) {
            state = LobbyState.EVALUATION;

            send(new EndGameTrigger());
            try {
                Thread.sleep(20);
            } catch (InterruptedException ignored) {}
            sendNextStory();
            return;
        }

        game.advanceRound();
        sendPersonalized(this::getNextStoryMessage);
    }

    public void sendGameStateUpdate() {
        send(new GameStateUpdateMessage(game.getFinishedPlayers()));
    }

    public StartRoundMessage getNextStoryMessage(Player player) {
        StartRoundMessage message = new StartRoundMessage();
        message.setCurrentRound(game.getCurrentRound() + 1);
        message.setMaxRounds(game.getMaxRounds());
        message.setLastStorySnippet(game.getStorySnippet(player));
        return message;
    }

    public void sendNextStory() {
        send(new NextStoryMessage(game.getCurrentStoriesAuthor()));
    }

    public void sendNextStory(Player player) {
        new NextStoryMessage(game.getCurrentStoriesAuthor()).sendTo(player.getSession());
    }

    public boolean isGameStarted() {
        return game != null && state == LobbyState.GAME;
    }
}
