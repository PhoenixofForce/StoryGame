package dev.phoenixofforce.story_game.connection.messages.trigger;

import dev.phoenixofforce.story_game.connection.messages.BaseMessage;

public class EndGameTrigger extends BaseMessage {

    public EndGameTrigger() {
        super("end_game", false, "");
    }
}
