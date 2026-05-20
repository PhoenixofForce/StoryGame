package dev.phoenixofforce.story_game.games.storygame.messages;

import dev.phoenixofforce.story_game.connection.messages.BaseMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RequestRevealMessage extends BaseMessage {

    public RequestRevealMessage() {
        super("request_reveal", false, "");
    }
}
