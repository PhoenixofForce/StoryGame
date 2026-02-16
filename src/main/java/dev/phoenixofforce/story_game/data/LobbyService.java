package dev.phoenixofforce.story_game.data;

import dev.phoenixofforce.story_game.game.Game;
import dev.phoenixofforce.story_game.connection.messages.BaseMessage;
import dev.phoenixofforce.story_game.connection.messages.PlayerJoinMessage;
import dev.phoenixofforce.story_game.connection.messages.StoryRevealMessage;
import dev.phoenixofforce.story_game.connection.messages.SubmitStoryMessage;
import dev.phoenixofforce.story_game.connection.messages.trigger.EndGameTrigger;
import dev.phoenixofforce.story_game.connection.messages.trigger.StartGameTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class LobbyService {

    private final Map<WebSocketSession, Player> socketToPlayer = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, Lobby> codeToLobby = Collections.synchronizedMap(new HashMap<>());


    public void register(WebSocketSession sender, PlayerJoinMessage playerJoinMessage) {
        String roomCode = getRoomCode(playerJoinMessage);
        Optional<Lobby> optionalLobby = getLobby(roomCode);
        if(optionalLobby.isPresent()) {
            joinRoom(roomCode, sender, playerJoinMessage);
        } else {
            createRoom(roomCode, sender, playerJoinMessage);
        }
    }

    public void startLobby(WebSocketSession sender) {
        Optional<LobbyPlayer> lobbyPlayer = getLobbyAndPlayer(sender);
        if(lobbyPlayer.isEmpty()) return;
        Player player = lobbyPlayer.get().player();
        Lobby lobby = lobbyPlayer.get().lobby();

        lobby.startGame(player);
    }

    public void acceptLobby(WebSocketSession sender, SubmitStoryMessage storyMessage) {
        Optional<LobbyPlayer> lobbyPlayer = getLobbyAndPlayer(sender);
        if(lobbyPlayer.isEmpty()) return;
        Player player = lobbyPlayer.get().player();
        Lobby lobby = lobbyPlayer.get().lobby();
        
        lobby.acceptStory(player, storyMessage.getFullStory().trim(), storyMessage.getTeaser().trim());
    }

    public void revealMessage(WebSocketSession sender) {
        Optional<LobbyPlayer> lobbyPlayer = getLobbyAndPlayer(sender);
        if(lobbyPlayer.isEmpty()) return;
        Player player = lobbyPlayer.get().player();
        Lobby lobby = lobbyPlayer.get().lobby();
        Game game = lobby.getGame();

        if (player != lobby.getHost()) return;
        if (game == null || game.isGameRunning()) return;
        if (game.allStoriesRevealed()) return;

        StoryRevealMessage messageToSend = game.advanceReveal();
        if(game.allStoriesRevealed()) {
            lobby.setState(LobbyState.LOBBY);
        }
        lobby.send(messageToSend);
    }

    public void nextStory(WebSocketSession sender) {
        Optional<LobbyPlayer> lobbyPlayer = getLobbyAndPlayer(sender);
        if(lobbyPlayer.isEmpty()) return;
        Lobby lobby = lobbyPlayer.get().lobby();
        lobby.sendNextStory();
    }

    private void createRoom(String roomCode, WebSocketSession sender, PlayerJoinMessage joinMessage) {
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

    private void joinRoom(String roomCode, WebSocketSession sender, PlayerJoinMessage joinMessage) {
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

        Optional<Player> playerInLobby = lobby.getConnectedPlayer().stream().filter(p -> p.getName().equals(playerName)).findAny();
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

    public void handleDisconnect(WebSocketSession session) throws IOException, InterruptedException {
        Optional<LobbyPlayer> lobbyPlayer = getLobbyAndPlayer(session);
        if(lobbyPlayer.isEmpty()) return;
        Player player = lobbyPlayer.get().player();
        Lobby lobby = lobbyPlayer.get().lobby();

        if (!lobby.isGameStarted()) {
            lobby.removePlayer(player);
        }
        player.setConnected(false);
        player.getSession().close();

        Thread.sleep(120 * 1000L);
        if(player.isConnected()) return;

        removePlayer(session);
        if(lobby.getConnectedPlayer().stream().noneMatch(Player::isConnected)) {
            closeLobby(player.getConnectedRoom());
            log.info("Closed room '{}'", player.getConnectedRoom());
        }
    }

    public Optional<LobbyPlayer> getLobbyAndPlayer(WebSocketSession session) {
        Optional<Player> optionalPlayer = getPlayer(session);
        if(optionalPlayer.isEmpty()) return Optional.empty();
        Player player = optionalPlayer.get();

        Optional<Lobby> optionalLobby = getLobby(player.getConnectedRoom());
        if(optionalLobby.isEmpty()) return Optional.empty();
        Lobby lobby = optionalLobby.get();

        return Optional.of(new LobbyPlayer(player, lobby));
    }

    public Optional<Player> getPlayer(WebSocketSession session) {
        return Optional.ofNullable(socketToPlayer.get(session));
    }

    public Optional<Lobby> getLobby(String lobbyCode) {
        return Optional.ofNullable(codeToLobby.get(lobbyCode));
    }

    public void removePlayer(WebSocketSession session) {
        socketToPlayer.remove(session);
    }

    public void closeLobby(String lobbyCode) {
        codeToLobby.remove(lobbyCode);
    }


    private String getRoomCode(PlayerJoinMessage joinMessage) {
        String roomCode = joinMessage.getRoom();
        if(roomCode != null && !roomCode.trim().isBlank()) {
            return roomCode.trim();
        }

        int generations = 0;
        do {
            roomCode = RoomCodeGeneration.generateRoomCode();
            generations++;
        } while(codeToLobby.containsKey(roomCode) && generations <= 10);

        //Backup generation
        while(codeToLobby.containsKey(roomCode)) {
            roomCode = RoomCodeGeneration.getRandomString();
        }

        roomCode = roomCode.trim();
        return roomCode;
    }

}
