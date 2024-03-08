package dev.phoenixofforce.story_game.data;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Story {

	private final List<Map.Entry<String, String>> storyParts;

	public Story() {
		storyParts = new ArrayList<>();
	}

	public int getLength() {
		return storyParts.size();
	}

	public void addStoryPart(String player, String storyPart) {
		storyParts.add(new AbstractMap.SimpleEntry<>(player, storyPart));
	}

	public String getStorySnippet() {
		if (storyParts.size() == 0) {
			return "";
		}
		Map.Entry<String, String> lastPart = storyParts.get(storyParts.size() - 1);
		int snippetLength = Math.min(50, lastPart.getValue().length());
		return "... "  + lastPart.getValue().substring(0, snippetLength);
	}
}
