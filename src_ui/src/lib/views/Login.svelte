<script lang="ts">
  import { DoorOpen } from "lucide-svelte";
  import { sendJoinMessage } from "$lib/services/gameService";
  import type { BaseMessage } from "$lib/services/messageTypes";
  import { displayLobby } from "$lib/services/navigationService";
  import { onDestroy } from "svelte";
  import {
    addEventHandler,
    removeEventHandler,
  } from "$lib/services/websocketService";
  import PageLayout from "$lib/components/PageLayout.svelte";
  import Button from "$lib/components/Button.svelte";
  import { m } from "$paraglide/messages.js";

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
          class="text-neutral-content mb-4 text-center text-5xl font-bold tracking-wide drop-shadow"
        >
          {m.common_title()}
        </div>
        <hr class="my-4" />
      </div>

      <div>
        <form
          on:submit|preventDefault={() => {}}
          class="flex flex-col items-center gap-2"
        >
          <input
            bind:value={username}
            placeholder={m.common_username()}
            class="input w-full xl:w-96"
          />
          <input
            bind:value={roomCode}
            placeholder={m.common_room_code()}
            class="input w-full xl:w-96"
          />

          <Button
            type="primary"
            classes="mt-6 w-full xl:w-96"
            icon={DoorOpen}
            onClick={() => connectToSocket()}
            disabled={!canCreateGame}
          >
            {m.login_enter_room()}
          </Button>
        </form>
        <div class="text-error text-center text-sm italic">
          {errorMessage}
        </div>
      </div>
    </div>
  </svelte:fragment>
</PageLayout>
