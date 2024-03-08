package dev.phoenixofforce.story_game.data;

import dev.phoenixofforce.story_game.connection.messages.BaseMessage;
import dev.phoenixofforce.story_game.connection.messages.LobbyStateMessage;
import dev.phoenixofforce.story_game.connection.messages.StartGameTriggerMessage;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Data
public class Lobby {

    private final List<Player> connectedPlayer = Collections.synchronizedList(new ArrayList<>());
    private final String roomCode;

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
    }

}
