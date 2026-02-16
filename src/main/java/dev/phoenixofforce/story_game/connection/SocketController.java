package dev.phoenixofforce.story_game.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.phoenixofforce.story_game.connection.messages.*;
import dev.phoenixofforce.story_game.connection.messages.trigger.NextStoryTrigger;
import dev.phoenixofforce.story_game.connection.messages.trigger.Ping;
import dev.phoenixofforce.story_game.data.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class SocketController extends TextWebSocketHandler {

    private interface CommandHandler {
        void apply(WebSocketSession sender, BaseMessage data) throws Exception;
    }

    private final Map<String, CommandHandler> commands = Map.of(
        "join", this::register,
        "start_game", this::handleStart,
        "submit_story", this::acceptStory,
        "request_reveal", this::revealStory,
        "next_story_trigger", this::nextStory,
        "ping", this::ping
        );

    private final LobbyService lobbyService;

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        lobbyService.handleDisconnect(session);
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
        lobbyService.register(sender, playerJoinMessage);
    }

    private void handleStart(WebSocketSession sender, BaseMessage message) {
           lobbyService.startLobby(sender);
    }

    private void acceptStory(WebSocketSession sender, BaseMessage message) {
        if(!(message instanceof SubmitStoryMessage storyMessage)) return;
        lobbyService.acceptLobby(sender, storyMessage);
    }

    private void revealStory(WebSocketSession sender, BaseMessage message) {
        if(!(message instanceof RequestRevealMessage)) return;
        lobbyService.revealMessage(sender);
    }

    private void nextStory(WebSocketSession sender, BaseMessage message) {
        if(!(message instanceof NextStoryTrigger)) return;
        lobbyService.nextStory(sender);
    }

    private void ping(WebSocketSession sender, BaseMessage message) {
        if(!(message instanceof Ping)) return;
        new Ping().sendTo(sender);
    }
}
