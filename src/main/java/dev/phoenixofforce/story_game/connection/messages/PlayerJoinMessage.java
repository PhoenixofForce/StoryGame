package dev.phoenixofforce.story_game.connection.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
public class PlayerJoinMessage {

    private String name;
    private String room;
    private String type;

    public static Optional<PlayerJoinMessage> fromJson(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return Optional.of(mapper.readValue(jsonString, PlayerJoinMessage.class));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "";
        }
    }
}
