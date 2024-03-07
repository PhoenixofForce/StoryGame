package dev.phoenixofforce.story_game.data;

import dev.phoenixofforce.story_game.connection.messages.BaseMessage;
import dev.phoenixofforce.story_game.connection.messages.LobbyStateMessage;
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
        send(new LobbyStateMessage(this));
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

}
