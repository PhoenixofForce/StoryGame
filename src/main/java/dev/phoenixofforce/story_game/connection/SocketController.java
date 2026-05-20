package dev.phoenixofforce.story_game.connection;

import dev.phoenixofforce.story_game.connection.configurations.ObjectMapperConfig;
import dev.phoenixofforce.story_game.connection.messages.*;
import dev.phoenixofforce.story_game.connection.messages.trigger.Ping;
import dev.phoenixofforce.story_game.data.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.core.JacksonException;

import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class SocketController extends TextWebSocketHandler {

    private final LobbyService lobbyService;

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        lobbyService.handleDisconnect(session);
    }

    @Override
    public void handleTextMessage(@NonNull WebSocketSession sender, TextMessage message) throws Exception {
        String receivedData = message.getPayload();
        try {
            BaseMessage baseMessage = ObjectMapperConfig.MAPPER.readValue(receivedData, BaseMessage.class);

            switch (baseMessage.getType()) {
                case "join" -> this.register(sender, baseMessage);
                case "start_game" -> this.handleStart(sender, baseMessage);
                case "ping" -> this.ping(sender, baseMessage);
                case null, default -> this.handleGameMessage(sender, baseMessage);
            }

        } catch (JacksonException _) {}
    }

    private void register(WebSocketSession sender, BaseMessage message) {
        if(!(message instanceof PlayerJoinMessage playerJoinMessage)) return;
        lobbyService.register(sender, playerJoinMessage);
    }

    private void handleStart(WebSocketSession sender, BaseMessage message) {
           lobbyService.startLobby(sender);
    }

    public void handleGameMessage(WebSocketSession sender, BaseMessage message) {
        this.lobbyService.handleGameMessage(sender, message);
    }

    private void ping(WebSocketSession sender, BaseMessage message) {
        if(!(message instanceof Ping)) return;
        new Ping().sendTo(sender);
    }
}
