package dev.phoenixofforce.story_game.connection.messages.trigger;

import dev.phoenixofforce.story_game.connection.messages.BaseMessage;

public class NextStoryTrigger extends BaseMessage {

    public NextStoryTrigger() {
        super("next_story_trigger", false, "");
    }
}
