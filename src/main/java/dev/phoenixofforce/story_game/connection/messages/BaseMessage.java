package dev.phoenixofforce.story_game.connection.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.phoenixofforce.story_game.connection.configurations.ObjectMapperConfig;
import dev.phoenixofforce.story_game.connection.messages.trigger.Ping;
import dev.phoenixofforce.story_game.connection.messages.trigger.StartGameTrigger;
import dev.phoenixofforce.story_game.games.storygame.messages.RequestRevealMessage;
import dev.phoenixofforce.story_game.games.storygame.messages.StoryGameState;
import dev.phoenixofforce.story_game.games.storygame.messages.SubmitStoryMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PlayerJoinMessage.class, name="join"),
    @JsonSubTypes.Type(value = LobbyStateMessage.class, name="lobby-change"),
    @JsonSubTypes.Type(value = SubmitStoryMessage.class, name="submit_story"),
    @JsonSubTypes.Type(value = RequestRevealMessage.class, name="request_reveal"),
    @JsonSubTypes.Type(value = StartGameTrigger.class, name="start_game"),
    @JsonSubTypes.Type(value = StoryGameState.class, name="story_game_update"),
    @JsonSubTypes.Type(value = Ping.class, name="ping"),
})
public class BaseMessage {

    private String type;
    private boolean isError;
    private String message;

    public String toPayload() {
        return ObjectMapperConfig.MAPPER.writeValueAsString(this);
    }

    public boolean sendTo(WebSocketSession session) {
        try {
            session.sendMessage(new TextMessage(toPayload()));
            return true;
        } catch (IOException _) {
            return false;
        }
    }

    public static BaseMessage getMessage(String type, String message) {
        return new BaseMessage(type, false, message);
    }

    public static BaseMessage getError(String type, String message) {
        return new BaseMessage(type, true, message);
    }
}
