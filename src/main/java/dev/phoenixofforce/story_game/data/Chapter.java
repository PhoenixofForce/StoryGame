package dev.phoenixofforce.story_game.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Chapter {
    private Player author;
    private String text;
}
