package dev.phoenixofforce.story_game.games.storygame;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.phoenixofforce.story_game.data.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Chapter {
    @JsonIgnore
    private Player author;
    private String text;

    @JsonProperty("author")
    public String getAuthorName() {
        return author.getName();
    }
}
