package dev.phoenixofforce.story_game.data;

import dev.phoenixofforce.story_game.connection.messages.PlayerJoinMessage;
import lombok.Data;

import java.util.List;

@Data
public class Story {

    private PlayerJoinMessage owner;
    private List<String> stories;

    private String lastSentence() {
        String[] sentences = stories.getLast().split("\\.");
        return sentences[sentences.length - 1];
    }

}
