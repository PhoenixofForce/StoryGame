package dev.phoenixofforce.story_game.connection.messages.trigger;

import dev.phoenixofforce.story_game.connection.messages.BaseMessage;

public class Ping extends BaseMessage {

    public Ping() {
        super("Ping", false, "");
    }

}
