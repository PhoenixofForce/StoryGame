package dev.phoenixofforce.story_game.connection.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NextStoryMessage extends BaseMessage {

    private final String creator;
    public NextStoryMessage(String creator) {
        super("next_story", false, "");
        this.creator = creator;
    }

}
