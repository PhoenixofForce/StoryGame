package dev.phoenixofforce.story_game.connection;

import dev.phoenixofforce.story_game.connection.messages.PlayerJoinMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

public class SocketController extends TextWebSocketHandler {

    private interface CommandHandler {
        void apply(WebSocketSession sender, String data) throws Exception;
    }

    private final Map<String, CommandHandler> commands;

    private final Map<WebSocketSession, String> sessionToRoom = Collections.synchronizedMap(new HashMap<>()); // TODO: to player
    private final Map<String, List<WebSocketSession>> rooms = Collections.synchronizedMap(new HashMap<>());

    public SocketController() {
        commands = Map.of(
            "register", this::register
        );
    }

    @Override
    public void handleTextMessage(WebSocketSession sender, TextMessage message) throws Exception {
        String receivedData = message.getPayload();
        for (String command : commands.keySet()) {
            if (receivedData.startsWith(command.trim() + " ")) {
                commands.get(command).apply(
                    sender, receivedData.substring((command.trim() + " ").length())
                );
            }
        }
    }

    private void register(WebSocketSession sender, String data) throws IOException {
        Optional<PlayerJoinMessage> optionalPlayer = PlayerJoinMessage.fromJson(data);
        if(optionalPlayer.isEmpty()) return;

        PlayerJoinMessage playerJoinMessage = optionalPlayer.get();
        if(!rooms.containsKey(playerJoinMessage.getRoom())) rooms.put(playerJoinMessage.getRoom(), new ArrayList<>());
        rooms.get(playerJoinMessage.getRoom()).add(sender);
        sessionToRoom.put(sender, playerJoinMessage.getRoom());

        for (WebSocketSession webSocketSession : rooms.get(playerJoinMessage.getRoom())) {
            webSocketSession.sendMessage(new TextMessage(playerJoinMessage.getName() + " joined!"));
        }
    }
}
