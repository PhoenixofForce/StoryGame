package dev.phoenixofforce.story_game.connection.messages.trigger;

import dev.phoenixofforce.story_game.connection.messages.BaseMessage;

public class RequestStateTrigger  extends BaseMessage {

    public RequestStateTrigger() {
        super("request_state", false, "");
    }

}