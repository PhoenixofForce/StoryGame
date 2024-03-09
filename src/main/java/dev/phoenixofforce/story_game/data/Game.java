package dev.phoenixofforce.story_game.data;

import lombok.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Game {

	private final int maxRounds;
	private int currentRound;
	private final Map<Player, Story> stories;
	private final List<Player> playerOrder;

	private int finishedPlayers = 0;

	public Game(int maxRounds, List<Player> players) {
		this.maxRounds = maxRounds;
		this.playerOrder = players;
		Collections.shuffle(players);

		stories = new HashMap<>();

		for (Player player : players) {
			stories.put(player, new Story());
		}
	}

	public boolean isRoundOver() {
		return stories.values().stream().allMatch(s -> s.getLength() > currentRound);
	}

	public void advanceRound() {
		++currentRound;
		finishedPlayers = 0;
		rotateStories();
	}

	public void addStoryPart(Player player, String storyPart) {
		stories.get(player).addStoryPart(player, storyPart);
		finishedPlayers++;
	}

	public String getStorySnippet(Player player) {
		return stories.get(player).getStorySnippet();
	}

	private void rotateStories() {
		Story firstStory = stories.get(playerOrder.get(0));

		for (int i = 0; i < playerOrder.size() - 1; i++) {
			Player player = playerOrder.get(i);
			Story nextStory = stories.get(playerOrder.get((i + 1) % playerOrder.size()));
			stories.put(player, nextStory);
		}
		stories.put(playerOrder.get(playerOrder.size() - 1), firstStory);
	}
}
