package dev.phoenixofforce.story_game.data;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Story {

	private final List<Map.Entry<Player, String>> storyParts;

	public Story() {
		storyParts = new ArrayList<>();
	}

	public int getLength() {
		return storyParts.size();
	}

	public void addStoryPart(Player player, String storyPart) {
		storyParts.add(new AbstractMap.SimpleEntry<>(player, storyPart));
	}
	
	public Map.Entry<Player, String> getStoryPart(int index) {
		return storyParts.get(index);
	}
	
	public String getStorySnippet() {
		if (storyParts.isEmpty()) {
			return "";
		}

		String lastPart = storyParts.get(storyParts.size() - 1).getValue();
		int snippetStart = lastPart.length() - Math.min(50, lastPart.length());
		return "... "  + lastPart.substring(snippetStart);
	}
}
