<script lang="ts">
  import { sendJoinMessage, addEventHandler } from "../services/gameservice";
  import type { BaseMessage } from "../services/messageTypes";
  import { displayLobby } from "../services/appFunctions";

  let username = "";
  let roomCode = "";

  let errorMessage = "";

  $: canJoinGame = username.length > 0 && roomCode.length > 0;
  $: canCreateGame = username.length > 0;

  function connectToSocket(type: "join" | "create") {
    errorMessage = "";
    sendJoinMessage(username, roomCode, type);
  }

  addEventHandler("join", {
    onSuccess: goToLobby,
    onError: handleJoinError,
  });

  function goToLobby(data: BaseMessage) {
    console.log(data);
    displayLobby();
  }

  function handleJoinError(error: BaseMessage) {
    errorMessage = error.message!;
  }
</script>

<div class="mt-64">
  <div class="text-5xl mb-4 font-bold tracking-wide drop-shadow text-center">
    The Story Game
  </div>
  <p class="mb-64 italic text-slate-400 text-center">
    ~ This site is currently under construction ~
  </p>

  <form on:submit|preventDefault={() => {}}>
    <input bind:value={username} placeholder="Username" />
    <input bind:value={roomCode} placeholder="Room Code" />
    <div class="buttons">
      <button on:click={() => connectToSocket("join")} disabled={!canJoinGame}
        >Join Game</button
      >
      <button
        on:click={() => connectToSocket("create")}
        disabled={!canCreateGame}>Create Game</button
      >
    </div>
  </form>
  <div class="text-red-600 italic text-sm text-center">{errorMessage}</div>
</div>

<style>
  form {
    display: flex;
    align-items: center;
    flex-direction: column;
    gap: 8px;
  }

  .buttons {
    display: flex;
    flex-direction: row;
    gap: 24px;
  }
</style>
