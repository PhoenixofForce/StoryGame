package dev.phoenixofforce.story_game.game.storygame;

import dev.phoenixofforce.story_game.data.Lobby;
import dev.phoenixofforce.story_game.data.Player;
import dev.phoenixofforce.story_game.games.storygame.Chapter;
import dev.phoenixofforce.story_game.games.storygame.StoryGameMode;
import dev.phoenixofforce.story_game.games.storygame.StoryGamePhase;
import dev.phoenixofforce.story_game.games.storygame.messages.RequestRevealMessage;
import dev.phoenixofforce.story_game.games.storygame.messages.SubmitStoryMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class StoryGameModeTest {

    // Todo: test how disconnects are handled
    // Todo: handle spectator

    private Player player1;
    private Player player2;
    private StoryGameMode mode;

    @BeforeEach
    void setUp() {
        Lobby lobby = new Lobby("mock lobby");
        player1 = new Player("Player 1", "mock lobby");
        player1.setConnected(true);
        player2 = new Player("Player 2", "mock lobby");
        player2.setConnected(true);

        lobby.addPlayer(player1);
        lobby.addPlayer(player2);

        mode = new StoryGameMode(lobby);
    }

    @Test
    void test_fullHappyPath() {
        assertGameState()
            .phase(StoryGamePhase.WRITING)
            .round(1)
            .finishedPlayers(0)
            .snippet("", "")
            .submitted(false, false)
            .verify();

        // Player 1 completes his message
        mode.handleMessage(player1, getSubmitMessage("full 1", "teaser 1"));
        assertGameState()
            .phase(StoryGamePhase.WRITING)
            .round(1)
            .finishedPlayers(1)
            .submitted(true, false)
            .verify();

        // Todo: check double submit

        // Player 2 completes his message - Round 1 is over
        mode.handleMessage(player2, getSubmitMessage("full 2", "teaser 2"));
        assertGameState()
            .phase(StoryGamePhase.WRITING)
            .round(2)
            .finishedPlayers(0)
            .snippet("...teaser 2", "...teaser 1")
            .submitted(false, false)
            .verify();

        // Player 2 completes his message
        mode.handleMessage(player2, getSubmitMessage("full 3", "teaser 3"));
        assertGameState()
            .phase(StoryGamePhase.WRITING)
            .round(2)
            .finishedPlayers(1)
            .snippet("...teaser 2", "...teaser 3")
            .submitted(false, true)
            .verify();

        // Player 1 completes his message - Round 2 is over, revealing now
        mode.handleMessage(player1, getSubmitMessage("full 4", "teaser 4"));
        assertGameState()
            .phase(StoryGamePhase.REVEALING)
            .allChaptersRevealed(false)
            .allStoriesRevealed(false)
            .currentAuthor("Player 2")
            .revealedChapters(List.of())
            .verify();

        // Todo: check submit
        // Todo: check playen2 request reveal

        // Reveal Story 1 message 1
        mode.handleMessage(player1, new RequestRevealMessage());
        assertGameState()
                .allChaptersRevealed(false)
                .allStoriesRevealed(false)
                .currentAuthor("Player 2")
                .revealedChapters(List.of(new Chapter(player2, "full 2")))
                .verify();

        // Reveal Story 1 message 2 - Story fully revealed
        mode.handleMessage(player1, new RequestRevealMessage());
        assertGameState()
                .allChaptersRevealed(true)
                .allStoriesRevealed(false)
                .currentAuthor("Player 2")
                .revealedChapters(List.of(new Chapter(player2, "full 2"), new Chapter(player1, "full 4")))
                .verify();

        // Switch to Story 2
        mode.handleMessage(player1, new RequestRevealMessage());
        assertGameState()
                .allChaptersRevealed(false)
                .allStoriesRevealed(false)
                .currentAuthor("Player 1")
                .revealedChapters(List.of())
                .verify();

        // Reveal Story 2 message 1
        mode.handleMessage(player1, new RequestRevealMessage());
        assertGameState()
                .allChaptersRevealed(false)
                .allStoriesRevealed(false)
                .currentAuthor("Player 1")
                .revealedChapters(List.of(new Chapter(player1, "full 1")))
                .verify();

        // Reveal Story 2 message 2 - all stories revealed now
        mode.handleMessage(player1, new RequestRevealMessage());
        assertGameState()
                .allChaptersRevealed(true)
                .allStoriesRevealed(true)
                .currentAuthor("Player 1")
                .revealedChapters(List.of(new Chapter(player1, "full 1"), new Chapter(player2, "full 3")))
                .verify();

        // Todo: test additional messages
    }

    private StoryGameStateAssert assertGameState() {
        return new StoryGameStateAssert(mode::getGameStateFor, player1, player2);
    }

    private SubmitStoryMessage getSubmitMessage(String fullStory, String teaser) {
        SubmitStoryMessage message = new SubmitStoryMessage();
        message.setFullStory(fullStory);
        message.setTeaser(teaser);
        return message;
    }

}
