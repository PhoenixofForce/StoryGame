<script lang="ts">
  import { DoorOpen } from "lucide-svelte";
  import { sendJoinMessage } from "../services/gameService";
  import type { BaseMessage } from "../services/messageTypes";
  import { displayLobby } from "../services/navigationService";
  import { onDestroy } from "svelte";
  import {
    addEventHandler,
    removeEventHandler,
  } from "../services/websocketService";
  import Card from "../components/Card.svelte";
  import Button from "../components/Button.svelte";

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

<div class="mt-24 w-full xl:mt-32">
  <Card classes="px-8 py-16 md:px-48">
    <div class="mb-4 text-center text-5xl font-bold tracking-wide drop-shadow">
      The Story Game
    </div>
    <hr />
    <p class="mb-16 text-center italic text-slate-400 xl:mb-52">
      ~ This site is currently under construction ~
    </p>

    <form on:submit|preventDefault={() => {}}>
      <input
        bind:value={username}
        placeholder="Username"
        class="w-full xl:w-96"
      />
      <input bind:value={roomCode} placeholder="Room Code" class="w-full" />

      <Button
        type="primary"
        classes="mt-6 w-full p-20"
        icon={DoorOpen}
        onClick={() => connectToSocket()}
        disabled={!canCreateGame}
      >
        Enter Room
      </Button>
    </form>
    <div class="text-center text-sm italic text-red-600">{errorMessage}</div>
  </Card>
</div>

<style>
  form {
    display: flex;
    align-items: center;
    flex-direction: column;
    gap: 8px;
  }
</style>
