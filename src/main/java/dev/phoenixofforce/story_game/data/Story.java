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

		return "... "  + prepareStoryText(storyParts.getLast().getValue());
	}

	//THIS FUNCTION HAS TO BE KEPT SYNCHRONIZED TO Ingame.highlightNextStoryText in the frontend
	public static String prepareStoryText(String inputText) {
		final int MIN_SENTENCE_LENGTH = 10;

		String[] sentences = inputText.split("[.?!]");
		int sentence_to_show = sentences.length - 1;
		while (sentence_to_show >= 1 && sentences[sentence_to_show].trim().length() < MIN_SENTENCE_LENGTH) {
			sentence_to_show -= 1;
		}
		int snippetStart = 0;
		for (int j = 0; j < sentence_to_show; j++) {
			snippetStart += sentences[j].length() + 1;
		}

		return inputText.substring(snippetStart);
	}
}
