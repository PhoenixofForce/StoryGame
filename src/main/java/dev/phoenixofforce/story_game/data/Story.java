package dev.phoenixofforce.story_game.data;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Story {

	private final List<Map.Entry<Player, String>> storyParts;
	private String lastTeaser = "";

	public Story() {
		storyParts = new ArrayList<>();
	}

	public int getLength() {
		return storyParts.size();
	}

	public void addStoryPart(Player player, String storyPart, String teaser) {
		storyParts.add(new AbstractMap.SimpleEntry<>(player, storyPart));
		lastTeaser = "..." + teaser;
	}
	
	public Map.Entry<Player, String> getStoryPart(int index) {
		return storyParts.get(index);
	}
	
	public String getStorySnippet() {
		return lastTeaser;
	}
}
