package dev.phoenixofforce.story_game.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.socket.WebSocketSession;

@Data
public class Player {

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private WebSocketSession session;
    private final String name;
    private final String connectedRoom;

    @EqualsAndHashCode.Exclude
    private boolean connected;

}
