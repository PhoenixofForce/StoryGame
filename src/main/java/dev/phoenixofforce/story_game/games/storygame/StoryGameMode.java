package dev.phoenixofforce.story_game.games.storygame;

import dev.phoenixofforce.story_game.connection.messages.*;
import dev.phoenixofforce.story_game.data.Lobby;
import dev.phoenixofforce.story_game.data.LobbyState;
import dev.phoenixofforce.story_game.data.Player;
import dev.phoenixofforce.story_game.data.PlayerRole;
import dev.phoenixofforce.story_game.games.GameMode;
import dev.phoenixofforce.story_game.games.storygame.messages.RequestRevealMessage;
import dev.phoenixofforce.story_game.games.storygame.messages.StoryGameState;
import dev.phoenixofforce.story_game.games.storygame.messages.SubmitStoryMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class StoryGameMode implements GameMode<StoryGameState> {

    private StoryGamePhase currentPhase;

    private final Lobby lobby;
    private final StoryGameLogic game;

    public StoryGameMode(Lobby lobby) {
        this.lobby = lobby;
        this.game = new StoryGameLogic(lobby.getConnectedPlayer().size(), lobby.getConnectedPlayer());

        this.currentPhase = StoryGamePhase.WRITING;
    }

    @Override
    public String getName() {
        return "Story Game";
    }

    @Override
    public Set<PlayerRole> getSupportedRoles() {
        return Set.of(PlayerRole.PLAYER, PlayerRole.SPECTATOR);
    }

    @Override
    public StoryGameState getGameStateFor(Player player) {
        StoryGameState gameState = new StoryGameState();
        gameState.setPhase(currentPhase);
        gameState.setCurrentRound(game.getCurrentRound() + 1);
        gameState.setMaxRounds(game.getMaxRounds());
        gameState.setFinishedPlayers(game.getFinishedPlayers());
        gameState.setLastStorySnippet(game.getStorySnippet(player));
        gameState.setHasPlayerSubmitted(game.hasPlayerSubmitted(player));

        gameState.setAllChaptersRevealed(game.allChaptersRevealed());
        gameState.setAllStoriesRevealed(game.allStoriesRevealed());
        gameState.setRevealedChapters(game.getRevealedChapters());
        gameState.setCurrentAuthor(game.getCurrentStoriesAuthor());

        return gameState;
    }

    @Override
    public void handleMessage(Player player, BaseMessage message) {
        switch (message) {
            case SubmitStoryMessage m -> this.acceptStory(player, m.getFullStory(), m.getTeaser());
            case RequestRevealMessage _ -> this.revealStory(player);
            default -> log.error("Unhandled message: {}", message.getType());
        }
    }

    private void acceptStory(Player writer, String story, String teaser) {
        if(game.hasPlayerSubmitted(writer)) return;
        game.addChapter(writer, story, teaser);

        if (!game.isCurrentWritingPhaseOver()) {
            lobby.sendGameState();
            return;
        }

        if (game.getCurrentRound() >= game.getMaxRounds() - 1) {
            this.currentPhase = StoryGamePhase.REVEALING;
            lobby.sendGameState();

            return;
        }

        game.advanceRound();
        lobby.sendGameState();
    }

    private void revealStory(Player player) {
        if (player != lobby.getHost()) return;
        if (game == null || game.isGameRunning()) return;
        if (game.allStoriesRevealed()) return;

        game.advanceReveal();
        if(game.allStoriesRevealed()) {
            lobby.setState(LobbyState.LOBBY);
        }
        lobby.sendGameState();
    }
}
