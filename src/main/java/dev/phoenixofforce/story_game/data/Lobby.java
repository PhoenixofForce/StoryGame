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
    private boolean gameStarted = false;

    public void addPlayer(Player player) {
        this.connectedPlayer.add(player);
        sendLobbyChangeUpdate();
    }

    public void removePlayer(Player player) {
        this.connectedPlayer.remove(player);
        sendLobbyChangeUpdate();
    }

    public Player getHost() {
        return connectedPlayer.get(0);
    }

    public void send(BaseMessage message) {
        connectedPlayer.forEach(p -> message.sendTo(p.getSession()));
    }

    public void sendPersonalized(Function<Player, BaseMessage> messageGenerator) {
        connectedPlayer.forEach(p -> messageGenerator.apply(p).sendTo(p.getSession()));
    }

    private void sendLobbyChangeUpdate() {
        sendPersonalized(player -> {
            LobbyStateMessage stateMessage = new LobbyStateMessage();
            stateMessage.setRoomCode(roomCode);
            stateMessage.setPlayers(connectedPlayer.stream().map(Player::getName).toList());
            stateMessage.setYou(player.getName());
            stateMessage.setHost(connectedPlayer.get(0).getName());
            return stateMessage;
        });
    }

    public void startGame(Player starter) {
        if(!starter.getSession().equals(getHost().getSession())) return;
        send(new StartGameTrigger());
        game = new Game(connectedPlayer.size(), new ArrayList<>(connectedPlayer.stream().toList()));
        gameStarted = true;

        sendPersonalized(player -> {
            StartRoundMessage message = new StartRoundMessage();
            message.setCurrentRound(game.getCurrentRound() + 1);
            message.setMaxRounds(game.getMaxRounds());
            message.setLastStorySnippet(game.getStorySnippet(player));
            return message;
        });
    }

    public void acceptStory(Player writer, String story) {
        game.addStoryPart(writer, story);

        if (!game.isRoundOver()) {
            send(new GameStateUpdateMessage(game.getFinishedPlayers()));
            return;
        }
        if (game.getCurrentRound() >= game.getMaxRounds() - 1) {
            send(new EndGameTrigger());
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            sendNextStory();
            return;
        }
        game.advanceRound();

        sendPersonalized(player -> {
            StartRoundMessage message = new StartRoundMessage();
            message.setCurrentRound(game.getCurrentRound() + 1);
            message.setMaxRounds(game.getMaxRounds());
            message.setLastStorySnippet(game.getStorySnippet(player));
            return message;
        });
    }

    public void sendNextStory() {
        send(new NextStoryMessage(game.getCurrentStoriesPlayer()));
    }
}
