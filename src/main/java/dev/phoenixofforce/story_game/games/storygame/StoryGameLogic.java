package dev.phoenixofforce.story_game.games.storygame;

import dev.phoenixofforce.story_game.data.Player;
import lombok.Data;

import java.util.*;

@Data
public class StoryGameLogic {

	private int currentRound;
	private final int maxRounds;

	private final Map<Player, Story> stories;
	private final List<Player> playerOrder;

	private int finishedPlayers = 0;
	private int revealedChapterIndex = 0;
	private int revealedStoryIndex = 0;

	public StoryGameLogic(int maxRounds, List<Player> players) {
		this.maxRounds = maxRounds;
		this.playerOrder = new ArrayList<>(players);
		Collections.shuffle(playerOrder);

		stories = new HashMap<>();
		for (Player player : players) {
			stories.put(player, new Story());
		}
	}
	
	public boolean isCurrentWritingPhaseOver() {
		return stories.keySet().stream()
			.filter(Player::isConnected)
			.map(stories::get)
			.allMatch(s -> s.getLength() > currentRound);
	}

	public boolean hasPlayerSubmitted(Player player) {
		return stories.containsKey(player) && stories.get(player).getLength() > currentRound;
	}
	
	public boolean isGameRunning() {
		return !stories.values().stream().allMatch(s -> s.getLength() >= maxRounds);
	}
	
	public void advanceRound() {
		++currentRound;
		finishedPlayers = 0;
		rotateStories();
	}

	public void addChapter(Player player, String chapter, String teaser) {
		stories.get(player).addChapter(player, chapter, teaser);
		finishedPlayers++;
	}

	public String getStorySnippet(Player player) {
		return stories.containsKey(player)? stories.get(player).getStorySnippet(): "";
	}

	private void rotateStories() {
		// a good algorithm to rotate stories is : +1, -2, +3, -4 ...
		// this works perfectly for even numbers of players and as good as possible for odd numbers of players
		int shift = currentRound % playerOrder.size();
		if (shift % 2 == 0) {
			shift = playerOrder.size() - shift;
		}

		Map<Player, Story> newStories = new HashMap<>();
		for (int i = 0; i < playerOrder.size(); i++) {
			Player player = playerOrder.get(i);
			Story nextStory = stories.get(playerOrder.get((i + shift) % playerOrder.size()));
			newStories.put(player, nextStory);
		}

        for (Player player : playerOrder) {
            stories.put(player, newStories.get(player));
        }
	}

	public void advanceReveal() {
		if (allStoriesRevealed()) return;

		revealedChapterIndex += 1;
		if (revealedChapterIndex > maxRounds) {
			revealedChapterIndex = 0;
			revealedStoryIndex += 1;
		}
	}

	public List<Chapter> getRevealedChapters() {
		return getStory(revealedStoryIndex).getChapters(revealedChapterIndex);
	}

	public boolean allChaptersRevealed() {
		return revealedChapterIndex >= getStory(revealedStoryIndex).getLength();
	}

	public boolean allStoriesRevealed() {
		return revealedStoryIndex >= stories.size() - 1 && allChaptersRevealed();
	}

	public String getCurrentStoriesAuthor() {
		return getStory(revealedStoryIndex).getAuthor();
	}

	private Story getStory(int index) {
		return stories.get(playerOrder.get(index));
	}


}
