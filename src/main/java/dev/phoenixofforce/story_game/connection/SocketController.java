package dev.phoenixofforce.story_game.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.phoenixofforce.story_game.connection.messages.BaseMessage;
import dev.phoenixofforce.story_game.connection.messages.PlayerJoinMessage;
import dev.phoenixofforce.story_game.data.Lobby;
import dev.phoenixofforce.story_game.data.Player;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SocketController extends TextWebSocketHandler {

    private interface CommandHandler {
        void apply(WebSocketSession sender, BaseMessage data) throws Exception;
    }

    private final Map<String, CommandHandler> commands;

    private final Map<WebSocketSession, Player> socketToPlayer = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, Lobby> codeToLobby = Collections.synchronizedMap(new HashMap<>());

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
            BaseMessage.getError("join", "Room code is invalid").sendTo(sender);
            return;
        }

        Player host = new Player(sender, joinMessage.getName());
        Lobby lobby = new Lobby(roomCode);
        lobby.addPlayer(host);

        socketToPlayer.put(sender, host);
        codeToLobby.put(roomCode, lobby);

        BaseMessage.getMessage("join", "Created Lobby " + roomCode).sendTo(sender);
    }

    private void joinRoom(WebSocketSession sender, PlayerJoinMessage joinMessage) {
        String roomCode = joinMessage.getRoom();
        if(roomCode == null || roomCode.isBlank()) {
            BaseMessage.getError("join", "Room code is invalid").sendTo(sender);
            return;
        }

        if(!codeToLobby.containsKey(roomCode)) {
           BaseMessage.getError("join", "Lobby does not exist").sendTo(sender);
            return;
        }

        Player player = new Player(sender, joinMessage.getName());
        Lobby lobby = codeToLobby.get(roomCode);

        socketToPlayer.put(sender, player);
        lobby.addPlayer(player);

        BaseMessage.getMessage("join", "Joined Lobby " + roomCode).sendTo(sender);
        lobby.send(BaseMessage.getMessage("join", player.getName() + " joined the lobby, all: " + lobby.getConnectedPlayer().stream().map(Player::getName).collect(Collectors.joining(", "))));
    }
}
