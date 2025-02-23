package dev.phoenixofforce.story_game.data;

import java.util.ArrayList;
import java.util.List;


public class Story {

	private final List<Chapter> storyParts = new ArrayList<>();
	private String lastTeaser = "";

	public int getLength() {
		return storyParts.size();
	}

	public void addStoryPart(Player player, String storyPart, String teaser) {
		storyParts.add(new Chapter(player, storyPart));
		lastTeaser = "..." + teaser;
	}
	
	public Chapter getStoryPart(int index) {
		return storyParts.get(index);
	}
	
	public String getStorySnippet() {
		return lastTeaser;
	}
}
