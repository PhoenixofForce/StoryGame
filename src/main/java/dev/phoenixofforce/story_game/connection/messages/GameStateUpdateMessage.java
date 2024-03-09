package dev.phoenixofforce.story_game.connection.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GameStateUpdateMessage extends BaseMessage {

    private final int finishedPlayers;
    public GameStateUpdateMessage(int finishedPlayers) {
        super("game-update", false, "");
        this.finishedPlayers = finishedPlayers;
    }
}
