package dev.phoenixofforce.story_game.connection.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StoryRevealMessage extends BaseMessage {
	
	private String writer;
	private String text;
	private boolean storyEnd;
	private boolean lastStory;
	public StoryRevealMessage() {
		super("reveal_story", false, "");
	}
}
