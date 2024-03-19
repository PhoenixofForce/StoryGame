package dev.phoenixofforce.story_game.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.phoenixofforce.story_game.connection.messages.*;
import dev.phoenixofforce.story_game.connection.messages.trigger.EndGameTrigger;
import dev.phoenixofforce.story_game.connection.messages.trigger.NextStoryTrigger;
import dev.phoenixofforce.story_game.connection.messages.trigger.Ping;
import dev.phoenixofforce.story_game.connection.messages.trigger.StartGameTrigger;
import dev.phoenixofforce.story_game.data.Game;
import dev.phoenixofforce.story_game.data.Lobby;
import dev.phoenixofforce.story_game.data.LobbyState;
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
            "next_story_trigger", this::nextStory,
            "ping", this::ping
        );
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if(!socketToPlayer.containsKey(session)) return;

        Player player = socketToPlayer.get(session);
        Lobby lobby = codeToLobby.get(player.getConnectedRoom());

        if (!lobby.isGameStarted()) {
            lobby.removePlayer(player);
        }
        player.setConnected(false);
        player.getSession().close();

        Thread.sleep(120 * 1000);
        if(player.isConnected()) return;

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

        String roomCode = getRoomCode(playerJoinMessage);
        if(codeToLobby.containsKey(roomCode)) {
            joinRoom(sender, playerJoinMessage);
        } else {
            createRoom(sender, playerJoinMessage);
        }

    }

    private String getRoomCode(PlayerJoinMessage joinMessage) {
        String roomCode = joinMessage.getRoom();
        if(roomCode == null || roomCode.isBlank()) {
            roomCode = "Foo Bar"; // TODO: generate room code
        }
        roomCode = roomCode.trim();
        return roomCode;
    }

    private void createRoom(WebSocketSession sender, PlayerJoinMessage joinMessage) {
        String roomCode = getRoomCode(joinMessage);
        if(codeToLobby.containsKey(roomCode)) {
            BaseMessage.getError("join", "Room code already exists").sendTo(sender);
            return;
        }

        if(joinMessage.getName() == null ||joinMessage.getName().isBlank()) {
            BaseMessage.getError("join", "Name is invalid").sendTo(sender);
            return;
        }

        Player host = new Player(joinMessage.getName().trim(), roomCode);
        host.setSession(sender);
        host.setConnected(true);

        Lobby lobby = new Lobby(roomCode);
        lobby.addPlayer(host);

        socketToPlayer.put(sender, host);
        codeToLobby.put(roomCode, lobby);
    }

    private void joinRoom(WebSocketSession sender, PlayerJoinMessage joinMessage) {
        String roomCode = getRoomCode(joinMessage);
        if(!codeToLobby.containsKey(roomCode)) {
            BaseMessage.getError("join", "Room does not exist").sendTo(sender);
            return;
        }

        if(joinMessage.getName() == null ||joinMessage.getName().isBlank()) {
            BaseMessage.getError("join", "Name is invalid").sendTo(sender);
            return;
        }
        String playerName = joinMessage.getName().trim();

        Lobby lobby = codeToLobby.get(roomCode);
        Optional<Player> playerInLobby = lobby.getConnectedPlayer().stream().filter((p) -> p.getName().equals(playerName)).findAny();
        if (playerInLobby.isPresent() && playerInLobby.get().isConnected()) {
            BaseMessage.getError("join", "Player name already exists").sendTo(sender);
            return;
        }

        if(playerInLobby.isEmpty() && lobby.getGame() != null && lobby.isGameStarted()) {
            BaseMessage.getError("join", "Game is currently running. Please wait to the end").sendTo(sender);
            return;
        }

        Player player = playerInLobby.orElse(new Player(playerName, roomCode));
        player.setSession(sender);
        player.setConnected(true);
        socketToPlayer.put(sender, player);

        //tell player current game state
        if(playerInLobby.isPresent() && lobby.getGame() != null && lobby.isGameStarted()) {
            new StartGameTrigger().sendTo(sender);
            lobby.getNextStoryMessage(player).sendTo(sender);
            lobby.sendGameStateUpdate();
            lobby.sendLobbyChangeUpdate();
            if(!lobby.getGame().hasPlayerSubmitted(player)) {
                lobby.getNextStoryMessage(player).sendTo(sender);
            }
            return;
        }

        lobby.addPlayer(player);
        if(lobby.getState() == LobbyState.EVALUATION) {
            new EndGameTrigger().sendTo(sender);
            lobby.sendNextStory(player);
            //TODO: send already revealed messages
        }
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
        lobby.acceptStory(player, storyMessage.getStory().trim());
    }
    
    private void revealStory(WebSocketSession sender, BaseMessage message) {
        if(!(message instanceof RequestRevealMessage)) return;
    
        Player player = socketToPlayer.get(sender);
        Lobby lobby = codeToLobby.get(player.getConnectedRoom());
        Game game = lobby.getGame();
        
        if (player != lobby.getHost()) return;
        if (game == null || game.isGameRunning()) return;
        if (game.allStoriesRevealed()) return;
        
        lobby.send(game.advanceReveal());   //todo: set lobby state to lobby
    }

    private void nextStory(WebSocketSession sender, BaseMessage message) {
        if(!(message instanceof NextStoryTrigger)) return;
        Player player = socketToPlayer.get(sender);
        Lobby lobby = codeToLobby.get(player.getConnectedRoom());
        lobby.sendNextStory();
    }

    private void ping(WebSocketSession sender, BaseMessage message) {
        if(!(message instanceof Ping)) return;
        new Ping().sendTo(sender);
    }
}
