package dev.phoenixofforce.story_game.connection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@Data
public class PlayerEntity {

    @JsonIgnore
    private final WebSocketSession session;
    private final String name;

}
