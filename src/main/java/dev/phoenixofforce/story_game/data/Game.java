package dev.phoenixofforce.story_game.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Game {

	private final int maxRounds;
	private int currentRound;
	private final HashMap<String, Story> stories;
	private final List<String> playerOrder;

	public Game(int maxRounds, List<String> players) {
		this.maxRounds = maxRounds;
		this.playerOrder = players;
		Collections.shuffle(players);

		stories = new HashMap<>();

		for (String player : players) {
			stories.put(player, new Story());
		}
	}

	public boolean isRoundOver() {
		return stories.values().stream().allMatch(s -> s.getLength() >= currentRound);
	}

	public void advanceRound() {
		if (currentRound < maxRounds) {
			++currentRound;
			rotateStories();
		} else {
			// end game?
		}
	}

	public int getCurrentRound() {
		return currentRound;
	}

	public void addStoryPart(String player, String storyPart) {
		stories.get(player).addStoryPart(player, storyPart);
	}

	public String getStorySnippet(String player) {
		return stories.get(player).getStorySnippet();
	}

	private void rotateStories() {
		Story firstStory = stories.get(playerOrder.get(0));

		for (int i = 0; i < playerOrder.size() - 1; i++) {
			String player = playerOrder.get(i);
			Story nextStory = stories.get(playerOrder.get((i + 1) % playerOrder.size()));
			stories.put(player, nextStory);
		}
		stories.put(playerOrder.get(playerOrder.size() - 1), firstStory);
	}

	public int getMaxRounds() {
		return maxRounds;
	}
}
