package dev.phoenixofforce.story_game.data;

import dev.phoenixofforce.story_game.connection.messages.BaseMessage;
import dev.phoenixofforce.story_game.connection.messages.LobbyStateMessage;
import dev.phoenixofforce.story_game.connection.messages.StartGameTriggerMessage;
import dev.phoenixofforce.story_game.connection.messages.StartRoundTriggerMessage;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Data
public class Lobby {

    private final List<Player> connectedPlayer = Collections.synchronizedList(new ArrayList<>());
    private final String roomCode;
    private Game game;

    public void addPlayer(Player player) {
        this.connectedPlayer.add(player);
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
        send(new StartGameTriggerMessage());
        game = new Game(connectedPlayer.size(), connectedPlayer.stream().map(Player::getName).toList());

        sendPersonalized(player -> {
            StartRoundTriggerMessage message = new StartRoundTriggerMessage();
            message.setIndex(game.getCurrentRound());
            message.setStorySnippet(game.getStorySnippet(player.getName()));
            return message;
        });
    }

    public void acceptStory(Player writer, String story) {
        game.addStoryPart(writer.getName(), story);

        if (!game.isRoundOver()) return;
        game.advanceRound();
        if (game.getCurrentRound() > game.getMaxRounds()) return; //TODO sum up stories and read them out

        sendPersonalized(player -> {
            StartRoundTriggerMessage message = new StartRoundTriggerMessage();
            message.setIndex(game.getCurrentRound());
            message.setStorySnippet(game.getStorySnippet(player.getName()));
            return message;
        });
    }
}
