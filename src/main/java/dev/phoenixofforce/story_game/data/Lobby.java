package dev.phoenixofforce.story_game.data;

import dev.phoenixofforce.story_game.connection.messages.PlayerJoinMessage;
import lombok.Data;

import java.util.List;

@Data
public class Lobby {

    private final String lobbyCode;
    private final List<PlayerJoinMessage> playerJoinMessages;

    public PlayerJoinMessage getHost() {
        return playerJoinMessages.get(0);
    }
}
