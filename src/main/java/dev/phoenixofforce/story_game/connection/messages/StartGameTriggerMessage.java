package dev.phoenixofforce.story_game.connection.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StartGameTriggerMessage extends BaseMessage {

    public StartGameTriggerMessage() {
        super("start_game", false, "");
    }
}
