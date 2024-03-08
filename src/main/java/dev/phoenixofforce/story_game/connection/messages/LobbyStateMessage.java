package dev.phoenixofforce.story_game.connection.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class LobbyStateMessage extends BaseMessage {

    private String roomCode;
    private List<String> players;
    private String you;
    private String host;

    public LobbyStateMessage() {
        super("lobby-change", false, "");
    }
}
