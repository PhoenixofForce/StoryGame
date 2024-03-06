package dev.phoenixofforce.story_game.connection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class LobbyEntity {

    @JsonIgnore
    private final List<PlayerEntity> connectedPlayer = Collections.synchronizedList(new ArrayList<>());
    private final String roomCode;

    public void addPlayer(PlayerEntity player) {
        this.connectedPlayer.add(player);
    }

    public PlayerEntity getHost() {
        return connectedPlayer.get(0);
    }

}
