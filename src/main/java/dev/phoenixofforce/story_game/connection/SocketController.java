package dev.phoenixofforce.story_game.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.phoenixofforce.story_game.connection.messages.*;
import dev.phoenixofforce.story_game.connection.messages.trigger.NextStoryTrigger;
import dev.phoenixofforce.story_game.data.Game;
import dev.phoenixofforce.story_game.data.Lobby;
import dev.phoenixofforce.story_game.data.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;


@Slf4j
public class SocketController extends TextWebSocketHandler {

    private interface CommandHandler {
        void apply(WebSocketSession sender, BaseMessage data) throws Exception;
    }

    private final Map<String, CommandHandler> commands;

    private final Map<WebSocketSession, Player> socketToPlayer = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, Lobby> codeToLobby = Collections.synchronizedMap(new HashMap<>());

    public SocketController() {
        commands = Map.of(
            "join", this::register,
            "start_game", this::handleStart,
            "submit_story", this::acceptStory,
            "request_reveal", this::revealStory,
            "next_story_trigger", this::nextStory
        );
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Player player = socketToPlayer.get(session);
        Lobby lobby = codeToLobby.get(player.getConnectedRoom());

        player.setConnected(false);
        player.getSession().close();
        socketToPlayer.remove(session);

        if(lobby.getConnectedPlayer().stream().noneMatch(Player::isConnected)) {
            codeToLobby.remove(player.getConnectedRoom());
            log.info("Closed room '{}'", player.getConnectedRoom());
        }
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

        Player host = new Player(sender, joinMessage.getName(), roomCode);
        host.setConnected(true);

        Lobby lobby = new Lobby(roomCode);
        lobby.addPlayer(host);

        socketToPlayer.put(sender, host);
        codeToLobby.put(roomCode, lobby);
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

        Player player = new Player(sender, joinMessage.getName(), joinMessage.getRoom());
        player.setConnected(true);
        Lobby lobby = codeToLobby.get(roomCode);

        socketToPlayer.put(sender, player);
        lobby.addPlayer(player);
 }

    private void handleStart(WebSocketSession sender, BaseMessage message) {
           Player player = socketToPlayer.get(sender);
           Lobby lobby = codeToLobby.get(player.getConnectedRoom());
           lobby.startGame(player);
    }

    private void acceptStory(WebSocketSession sender, BaseMessage message) {
        if(!(message instanceof SubmitStoryMessage storyMessage)) return;

        Player player = socketToPlayer.get(sender);
        Lobby lobby = codeToLobby.get(player.getConnectedRoom());
        lobby.acceptStory(player, storyMessage.getStory());
    }
    
    private void revealStory(WebSocketSession sender, BaseMessage message) {
        if(!(message instanceof RequestRevealMessage)) return;
    
        Player player = socketToPlayer.get(sender);
        Lobby lobby = codeToLobby.get(player.getConnectedRoom());
        Game game = lobby.getGame();
        
        if (player != lobby.getHost()) return;
        if (game == null || !game.isGameOver()) return;
        if (game.allStoriesRevealed()) return;
        
        lobby.send(game.advanceReveal());
    }

    private void nextStory(WebSocketSession sender, BaseMessage message) {
        if(!(message instanceof NextStoryTrigger)) return;
        Player player = socketToPlayer.get(sender);
        Lobby lobby = codeToLobby.get(player.getConnectedRoom());
        lobby.sendNextStory();
    }
}
