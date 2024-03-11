package dev.phoenixofforce.story_game.data;

import dev.phoenixofforce.story_game.connection.messages.StoryRevealMessage;
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
	private int revealedStoryIndex = 0;
	private int revealedStoryPartIndex = 0;
	
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
	
	public boolean isGameOver() {
		return stories.values().stream().allMatch(s -> s.getLength() >= maxRounds);
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
		// a good algorithm to rotate stories is : +1, -2, +3, -4 ...
		// this works perfectly for even numbers of players and as good as possible for odd numbers of players
		int shift = currentRound % playerOrder.size();
		if (shift % 2 == 0) {
			shift = playerOrder.size() - shift;
		}

		Story firstStory = stories.get(playerOrder.get(0));

		for (int i = 0; i < playerOrder.size() - 1; i++) {
			Player player = playerOrder.get(i);
			Story nextStory = stories.get(playerOrder.get((i + shift) % playerOrder.size()));
			stories.put(player, nextStory);
		}
		stories.put(playerOrder.get((playerOrder.size() - shift) % playerOrder.size()), firstStory);
	}
	
	private Story getStory(int index) {
		return stories.get(playerOrder.get(index));
	}
	public StoryRevealMessage advanceReveal() {
		if (allStoriesRevealed()) return null;

		Story story = getStory(revealedStoryIndex);
		Map.Entry<Player, String> storyPart = story.getStoryPart(revealedStoryPartIndex);
		StoryRevealMessage message = new StoryRevealMessage();
		message.setWriter(storyPart.getKey().getName());
		message.setText(storyPart.getValue());

		message.setStoryEnd(revealedStoryPartIndex == story.getLength() - 1);
		++revealedStoryPartIndex;

		if (revealedStoryPartIndex >= maxRounds) {
			revealedStoryPartIndex = 0;
			++revealedStoryIndex;
		}

		System.out.println(allStoriesRevealed());
		message.setLastStory(allStoriesRevealed());
		return message;
	}

	public String getCurrentStoriesPlayer() {
		return getStory(revealedStoryIndex).getStoryPart(0).getKey().getName();
	}

	public boolean allStoriesRevealed() {
		return revealedStoryIndex >= stories.size();
	}
}
