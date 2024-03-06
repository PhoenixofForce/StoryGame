package dev.phoenixofforce.story_game.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.phoenixofforce.story_game.connection.messages.BaseMessage;
import dev.phoenixofforce.story_game.connection.messages.PlayerJoinMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

public class SocketController extends TextWebSocketHandler {

    private interface CommandHandler {
        void apply(WebSocketSession sender, BaseMessage data) throws Exception;
    }

    private final Map<String, CommandHandler> commands;

    private final Map<WebSocketSession, PlayerEntity> socketToPlayer = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, LobbyEntity> codeToLobby = Collections.synchronizedMap(new HashMap<>());

    public SocketController() {
        commands = Map.of(
            "join", this::register
        );
    }

    @Override
    public void handleTextMessage(WebSocketSession sender, TextMessage message) throws Exception {
        String receivedData = message.getPayload();
        BaseMessage baseMessage = new ObjectMapper().readValue(receivedData, BaseMessage.class);

        for (String command : commands.keySet()) {
            if (baseMessage.getType().equals(command)) {
                commands.get(command).apply(sender, baseMessage);
            }
        }
    }

    private void register(WebSocketSession sender, BaseMessage message) throws IOException {
        if(!(message instanceof PlayerJoinMessage playerJoinMessage)) return;

        if(playerJoinMessage.getJoinType().equals("create")) {
            createRoom(sender, playerJoinMessage);
        } else if(playerJoinMessage.getJoinType().equals("join")) {
            joinRoom(sender, playerJoinMessage);
        }
    }

    private void createRoom(WebSocketSession sender, PlayerJoinMessage joinMessage) {
        String roomCode = joinMessage.getRoom();
        if(roomCode == null || roomCode.isBlank()) {
            roomCode = "Foo Bar"; // TODO: generate room code
        }

        if(codeToLobby.containsKey(roomCode)) {
            sendMessage(sender, BaseMessage.getError("join", "Room code is invalid"));
            return;
        }

        PlayerEntity host = new PlayerEntity(sender, joinMessage.getName());
        LobbyEntity lobby = new LobbyEntity(roomCode);
        lobby.addPlayer(host);

        socketToPlayer.put(sender, host);
        codeToLobby.put(roomCode, lobby);

        sendMessage(sender, BaseMessage.getMessage("join", "Created Lobby " + roomCode));
    }

    private void joinRoom(WebSocketSession sender, PlayerJoinMessage joinMessage) {
        String roomCode = joinMessage.getRoom();
        if(roomCode == null || roomCode.isBlank()) {
            sendMessage(sender, BaseMessage.getError("join", "Room code is invalid"));
            return;
        }

        if(!codeToLobby.containsKey(roomCode)) {
            sendMessage(sender, BaseMessage.getError("join", "Lobby does not exist"));
            return;
        }

        PlayerEntity player = new PlayerEntity(sender, joinMessage.getName());
        LobbyEntity lobby = codeToLobby.get(roomCode);

        socketToPlayer.put(sender, player);
        lobby.addPlayer(player);

        sendMessage(sender, BaseMessage.getMessage("join", "Joined Lobby " + roomCode));
    }

    private boolean sendMessage(WebSocketSession recipient, BaseMessage message) {
        try {
            recipient.sendMessage(new TextMessage(message.toPayload()));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
