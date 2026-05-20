package dev.phoenixofforce.story_game.game.storygame;

import dev.phoenixofforce.story_game.data.Player;
import dev.phoenixofforce.story_game.games.storygame.Chapter;
import dev.phoenixofforce.story_game.games.storygame.StoryGamePhase;
import dev.phoenixofforce.story_game.games.storygame.messages.StoryGameState;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class StoryGameStateAssert {

    private final Function<Player, StoryGameState> stateProvider;
    private final Player player1;
    private final Player player2;

    private StoryGamePhase phase;
    private Integer currentRound;
    private Integer finishedPlayers;
    private String snippetPlayer1;
    private String snippetPlayer2;
    private Boolean submittedPlayer1;
    private Boolean submittedPlayer2;
    private Boolean allChaptersRevealed;
    private Boolean allStoriesRevealed;
    private String currentAuthor;
    private List<Chapter> revealedChapters;

    public StoryGameStateAssert(Function<Player, StoryGameState> stateProvider, Player player1, Player player2) {
        this.stateProvider = stateProvider;
        this.player1 = player1;
        this.player2 = player2;
    }

    public StoryGameStateAssert phase(StoryGamePhase phase) { this.phase = phase; return this; }
    public StoryGameStateAssert round(int currentRound) { this.currentRound = currentRound; return this; }
    public StoryGameStateAssert finishedPlayers(int finishedPlayers) { this.finishedPlayers = finishedPlayers; return this; }
    public StoryGameStateAssert snippet(String forPlayer1, String forPlayer2) { this.snippetPlayer1 = forPlayer1; this.snippetPlayer2 = forPlayer2; return this; }
    public StoryGameStateAssert submitted(boolean forPlayer1, boolean forPlayer2) { this.submittedPlayer1 = forPlayer1; this.submittedPlayer2 = forPlayer2; return this; }
    public StoryGameStateAssert allChaptersRevealed(boolean revealed) { this.allChaptersRevealed = revealed; return this; }
    public StoryGameStateAssert allStoriesRevealed(boolean revealed) { this.allStoriesRevealed = revealed; return this; }
    public StoryGameStateAssert currentAuthor(String author) { this.currentAuthor = author; return this; }
    public StoryGameStateAssert revealedChapters(List<Chapter> chapters) { this.revealedChapters = chapters; return this; }

    public void verify() {
        StoryGameState state1 = stateProvider.apply(player1);
        StoryGameState state2 = stateProvider.apply(player2);

        if (phase != null) {
            assertThat(state1.getPhase()).isEqualTo(phase);
            assertThat(state2.getPhase()).isEqualTo(phase);
        }
        if (currentRound != null) {
            assertThat(state1.getCurrentRound()).isEqualTo(currentRound);
            assertThat(state2.getCurrentRound()).isEqualTo(currentRound);
        }
        if (finishedPlayers != null) {
            assertThat(state1.getFinishedPlayers()).isEqualTo(finishedPlayers);
            assertThat(state2.getFinishedPlayers()).isEqualTo(finishedPlayers);
        }
        if (snippetPlayer1 != null) assertThat(state1.getLastStorySnippet()).isEqualTo(snippetPlayer1);
        if (snippetPlayer2 != null) assertThat(state2.getLastStorySnippet()).isEqualTo(snippetPlayer2);
        if (submittedPlayer1 != null) assertThat(state1.isHasPlayerSubmitted()).isEqualTo(submittedPlayer1);
        if (submittedPlayer2 != null) assertThat(state2.isHasPlayerSubmitted()).isEqualTo(submittedPlayer2);
        if (allChaptersRevealed != null) {
            assertThat(state1.isAllChaptersRevealed()).isEqualTo(allChaptersRevealed);
            assertThat(state2.isAllChaptersRevealed()).isEqualTo(allChaptersRevealed);
        }
        if (allStoriesRevealed != null) {
            assertThat(state1.isAllStoriesRevealed()).isEqualTo(allStoriesRevealed);
            assertThat(state2.isAllStoriesRevealed()).isEqualTo(allStoriesRevealed);
        }
        if (currentAuthor != null) {
            assertThat(state1.getCurrentAuthor()).isEqualTo(currentAuthor);
            assertThat(state2.getCurrentAuthor()).isEqualTo(currentAuthor);
        }
        if (revealedChapters != null) {
            assertThat(state1.getRevealedChapters()).isEqualTo(revealedChapters);
            assertThat(state2.getRevealedChapters()).isEqualTo(revealedChapters);
        }
    }
}