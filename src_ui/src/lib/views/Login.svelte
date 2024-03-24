<script lang="ts">
  import { sendJoinMessage } from "../services/gameService";
  import type { BaseMessage } from "../services/messageTypes";
  import { displayLobby } from "../services/navigationService";
  import { onDestroy } from "svelte";
  import {
    addEventHandler,
    removeEventHandler,
  } from "../services/websocketService";

  let username = "";
  let roomCode = "";

  let errorMessage = "";

  $: canJoinGame = username.length > 0 && roomCode.length > 0;
  $: canCreateGame = username.length > 0;

  function connectToSocket(type: "join" | "create") {
    errorMessage = "";
    sendJoinMessage(username, roomCode, type);
  }

  let joinHandler = addEventHandler("join", {
    onError: handleJoinError,
  });

  let lobbyHandler = addEventHandler("lobby-change", {
    onSuccess: displayLobby,
  });

  function handleJoinError(error: BaseMessage) {
    errorMessage = error.message!;
  }

  onDestroy(() => {
    removeEventHandler(joinHandler);
    removeEventHandler(lobbyHandler);
  });
</script>

<div class="mt-24 xl:mt-64">
  <div class="text-5xl mb-4 font-bold tracking-wide drop-shadow text-center">
    The Story Game
  </div>
  <p class="mb-32 xl:mb-52 italic text-slate-400 text-center">
    ~ This site is currently under construction ~
  </p>

  <form on:submit|preventDefault={() => {}}>
    <input bind:value={username} placeholder="Username" class="w-96"/>
    <input bind:value={roomCode} placeholder="Room Code" class="w-96"/>
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
    @apply flex w-full flex-col justify-center md:flex-row md:gap-6;
  }

  .buttons button {
    margin-top: 16px;
  }
</style>
