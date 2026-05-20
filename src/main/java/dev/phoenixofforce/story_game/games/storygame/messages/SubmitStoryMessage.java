package dev.phoenixofforce.story_game.games.storygame.messages;

import dev.phoenixofforce.story_game.connection.messages.BaseMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SubmitStoryMessage extends BaseMessage {
	private String fullStory;
	private String teaser;
}
