package dev.phoenixofforce.story_game.data;

import dev.phoenixofforce.story_game.connection.messages.StoryRevealMessage;
import lombok.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Game {

	private int currentRound;
	private final int maxRounds;

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

	public boolean allStoriesRevealed() {
		return revealedStoryIndex >= stories.size();
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
		for (int i = 0; i < playerOrder.size(); i++) {
			Player player = playerOrder.get(i);
			stories.put(player, newStories.get(player));
		}
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

		message.setLastStory(allStoriesRevealed());
		return message;
	}

	public String getCurrentStoriesAuthor() {
		return getStory(revealedStoryIndex).getStoryPart(0).getKey().getName();
	}

}
