package dev.phoenixofforce.story_game.connection.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    @JsonSubTypes.Type(value = LobbyStateMessage.class, name="lobby-state")
})
public class BaseMessage {

    private String type;
    private boolean isError;
    private String message;

    public String toPayload() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error("Could not parse this object to json: {}", this);
            return "";
        }
    }

    public boolean sendTo(WebSocketSession session) {
        try {
            session.sendMessage(new TextMessage(toPayload()));
            return true;
        } catch (IOException e) {
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
