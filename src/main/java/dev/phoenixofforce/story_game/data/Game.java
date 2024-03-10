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
		Story firstStory = stories.get(playerOrder.get(0));

		for (int i = 0; i < playerOrder.size() - 1; i++) {
			Player player = playerOrder.get(i);
			Story nextStory = stories.get(playerOrder.get((i + 1) % playerOrder.size()));
			stories.put(player, nextStory);
		}
		stories.put(playerOrder.get(playerOrder.size() - 1), firstStory);
	}
	
	private Story getStory(int index) {
		return stories.get(playerOrder.get(index));
	}
	public StoryRevealMessage advanceReveal() {
		if (allStoriesRevealed()) return null;
		
		Map.Entry<Player, String> storyPart =  getStory(revealedStoryIndex).getStoryPart(revealedStoryPartIndex);
		StoryRevealMessage message = new StoryRevealMessage();
		message.setWriter(storyPart.getKey().getName());
		message.setText(storyPart.getValue());
		
		//TODO adapt to whatever happens to stories if players leave midgame
		++revealedStoryPartIndex;
		if (revealedStoryPartIndex >= maxRounds) {
			revealedStoryPartIndex = 0;
			++revealedStoryIndex;
		}
		return message;
	}
	
	public boolean allStoriesRevealed() {
		return revealedStoryIndex >= stories.size();
	}
}
