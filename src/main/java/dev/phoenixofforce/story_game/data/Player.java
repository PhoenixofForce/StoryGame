package dev.phoenixofforce.story_game.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@Data
public class Player {

    @JsonIgnore
    private final WebSocketSession session;
    private final String name;
    private final String connectedRoom;
    private boolean connected;

}
