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
  import PageLayout from "../components/PageLayout.svelte";
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
    onSuccess: () => {
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

<PageLayout>
  <svelte:fragment slot="content">
    <div class="flex h-full flex-col justify-between">
      <div class="mt-14">
        <div
          class="mb-4 text-center text-5xl font-bold tracking-wide drop-shadow"
        >
          The Story Game
        </div>
        <hr class="my-4" />
        <p class="text-center text-slate-400 italic">
          ~ This site is currently under construction ~
        </p>
      </div>

      <div>
        <form
          on:submit|preventDefault={() => {}}
          class="flex flex-col items-center gap-2"
        >
          <input
            bind:value={username}
            placeholder="Username"
            class="w-full xl:w-96"
          />
          <input
            bind:value={roomCode}
            placeholder="Room Code"
            class="w-full xl:w-96"
          />

          <Button
            type="primary"
            classes="mt-6 w-full xl:w-96"
            icon={DoorOpen}
            onClick={() => connectToSocket()}
            disabled={!canCreateGame}
          >
            Enter Room
          </Button>
        </form>
        <div class="text-center text-sm text-red-600 italic">
          {errorMessage}
        </div>
      </div>
    </div>
  </svelte:fragment>
</PageLayout>
