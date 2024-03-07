package dev.phoenixofforce.story_game.connection.messages;

import dev.phoenixofforce.story_game.data.Lobby;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LobbyStateMessage extends BaseMessage {

    private Lobby lobby;

    public LobbyStateMessage(Lobby lobby) {
        super("lobby-data", false, "");
        this.lobby = lobby;
    }
}
