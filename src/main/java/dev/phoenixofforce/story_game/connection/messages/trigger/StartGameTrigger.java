package dev.phoenixofforce.story_game.connection.messages.trigger;

import dev.phoenixofforce.story_game.connection.messages.BaseMessage;

public class StartGameTrigger extends BaseMessage {

    public StartGameTrigger() {
        super("start_game", false, "");
    }

}
