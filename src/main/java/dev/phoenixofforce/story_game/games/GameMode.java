package dev.phoenixofforce.story_game.games;

import dev.phoenixofforce.story_game.connection.messages.BaseMessage;
import dev.phoenixofforce.story_game.data.Player;
import dev.phoenixofforce.story_game.data.PlayerRole;

import java.util.Set;

public interface GameMode<GameState extends BaseMessage> {

    String getName();
    Set<PlayerRole> getSupportedRoles();

    GameState getGameStateFor(Player player);
    void handleMessage(Player sender, BaseMessage message);

}
