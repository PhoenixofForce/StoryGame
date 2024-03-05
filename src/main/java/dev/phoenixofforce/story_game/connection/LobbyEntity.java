package dev.phoenixofforce.story_game.connection;

import dev.phoenixofforce.story_game.connection.messages.PlayerJoinMessage;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Data
public class LobbyEntity {

    private final String room;
    private final Map<PlayerJoinMessage, WebSocketSession> connectedPlayer;

}
