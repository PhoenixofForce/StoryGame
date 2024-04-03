package dev.phoenixofforce.story_game.connection.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SubmitStoryMessage extends BaseMessage {
	private String fullStory;
	private String teaser;
}
