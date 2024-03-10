package dev.phoenixofforce.story_game.connection.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StoryRevealMessage extends BaseMessage {
	
	private String writer;
	private String text;
	//even if I name this "isStoryEnd" lombok generates getter named "getStoryEnd" and "storyEnd" lands in the json ._.
	private boolean storyEnd;
	public StoryRevealMessage() {
		super("reveal_story", false, "");
	}
}
