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

  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  if (urlParams.has("c")) {
    roomCode = urlParams.get("c") as string;
  }

  let errorMessage = "";
  $: canCreateGame = username.length > 0;

  function connectToSocket() {
    errorMessage = "";
    sendJoinMessage(username, roomCode);
  }

  let joinHandler = addEventHandler("join", {
    onError: handleJoinError,
  });

  let lobbyHandler = addEventHandler("lobby-change", {
    onSuccess: (_) => {
      window.history.pushState("page2", "Title", location.pathname);

      displayLobby();
    },
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
  <div class="mb-4 text-center text-5xl font-bold tracking-wide drop-shadow-sm">
    The Story Game
  </div>
  <p class="mb-32 text-center italic text-slate-400 xl:mb-52">
    ~ This site is currently under construction ~
  </p>

  <form on:submit|preventDefault={() => {}}>
    <input bind:value={username} placeholder="Username" class="w-96" />
    <input bind:value={roomCode} placeholder="Room Code" class="w-96" />
    <button
      class="blue mt-8 w-96"
      on:click={() => connectToSocket()}
      disabled={!canCreateGame}>Play</button
    >
  </form>
  <div class="text-center text-sm italic text-red-600">{errorMessage}</div>
</div>

<style>
  form {
    display: flex;
    align-items: center;
    flex-direction: column;
    gap: 8px;
  }
</style>
