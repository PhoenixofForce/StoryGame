package dev.phoenixofforce.story_game.connection.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StartRoundTriggerMessage extends BaseMessage {
	int index;
	String storySnippet;

	public StartRoundTriggerMessage() {
		super("start_round", false, "");
	}
}
