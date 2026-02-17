package dev.phoenixofforce.story_game.game;

import dev.phoenixofforce.story_game.data.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Chapter {
    private Player author;
    private String text;
}
