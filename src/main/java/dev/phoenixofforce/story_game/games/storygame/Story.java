package dev.phoenixofforce.story_game.games.storygame;

import dev.phoenixofforce.story_game.data.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


public class Story {

	private final List<Chapter> chapters = new ArrayList<>();
	private String lastTeaser = "";

	public int getLength() {
		return chapters.size();
	}

	public void addChapter(Player player, String storyPart, String teaser) {
		chapters.add(new Chapter(player, storyPart));
		lastTeaser = "..." + teaser;
	}
	
	public List<Chapter> getChapters(int exclusiveIndex) {
		return IntStream.range(0, chapters.size())
				.filter(i -> i < exclusiveIndex)
				.mapToObj(chapters::get)
				.toList();
	}
	
	public String getStorySnippet() {
		return lastTeaser;
	}

	public String getAuthor() {
		if(chapters.isEmpty()) return "";
		return chapters.getFirst().getAuthor().getName();
	}
}
