package dev.phoenixofforce.story_game.games.storygame.messages;

import dev.phoenixofforce.story_game.connection.messages.BaseMessage;
import dev.phoenixofforce.story_game.games.storygame.Chapter;
import dev.phoenixofforce.story_game.games.storygame.StoryGamePhase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class StoryGameState extends BaseMessage {

    private StoryGamePhase phase; // writing or revealing

    // Writing
    private int currentRound;
    private int maxRounds;
    private int finishedPlayers;
    private String lastStorySnippet;
    private boolean hasPlayerSubmitted;

    // Revealing
    private boolean allChaptersRevealed;
    private boolean allStoriesRevealed;

    private String currentAuthor;
    private List<Chapter> revealedChapters;

    public StoryGameState() {
        super("story_game_update", false, "");
    }

}
