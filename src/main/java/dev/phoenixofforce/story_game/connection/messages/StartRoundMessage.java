package dev.phoenixofforce.story_game.connection.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StartRoundMessage extends BaseMessage {

	private int currentRound;
	private int maxRounds;
	private String lastStorySnippet;

	public StartRoundMessage() {
		super("start_round", false, "");
	}
}
