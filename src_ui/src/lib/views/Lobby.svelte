<script lang="ts">
  import { PenTool, Clipboard } from "lucide-svelte";
  import PlayerDisplay from "../components/PlayerDisplay.svelte";
  import { lobbyStore } from "../services/lobbyService";
  import {
    sendMessage,
    addEventHandler,
    removeEventHandler,
  } from "../services/websocketService";
  import { displayInGame } from "../services/navigationService";
  import { onDestroy } from "svelte";
  import PageLayout from "../components/PageLayout.svelte";
  import Button from "../components/Button.svelte";

  let handler = addEventHandler("start_game", {
    onSuccess: (e) => {
      console.log("start: ", e);
      displayInGame();
    },
  });

  function startGame() {
    sendMessage({
      type: "start_game",
      error: false,
      message: "",
    });
  }

  function getPlayerDisplayName(player: string): string {
    return player;
  }

  function getPlayerTags(player: string): string {
    const out = [];
    if (player === $lobbyStore.you) out.push("You");
    if (player === $lobbyStore.host) out.push("Host");
    console.log(out);
    return out;
  }

  onDestroy(() => {
    removeEventHandler(handler);
  });

  function shareRoomCodeLink() {
    const roomCodeLink =
      window.location.protocol +
      "//" +
      window.location.host +
      window.location.pathname +
      "?c=" +
      encodeURIComponent($lobbyStore.roomCode);
    navigator.clipboard.writeText(roomCodeLink);
  }
</script>

<PageLayout showSidebar={true}>
  <svelte:fragment slot="sidebar">
    <div class="mb-4">
      <Button icon={Clipboard} onClick={shareRoomCodeLink}>
        {$lobbyStore.roomCode}
      </Button>
    </div>
    <PlayerDisplay
      playerNames={$lobbyStore.players.map((player) => ({
        name: getPlayerDisplayName(player),
        tags: getPlayerTags(player),
      }))}
    />
  </svelte:fragment>

  <svelte:fragment slot="title">
    <h2 class="text-base-content font-bold tracking-wide">Settings</h2>
  </svelte:fragment>

  <svelte:fragment slot="content">
    <div class="text-neutral-content">
      <input
        disabled={true}
        type="number"
        class="input w-full"
        placeholder="Number of Rounds"
      />
      <p class="mt-2">Settings are currently not supported...</p>
    </div>
  </svelte:fragment>

  <svelte:fragment slot="actions">
    <Button
      icon={PenTool}
      onClick={startGame}
      disabled={$lobbyStore.you !== $lobbyStore.host}
      type="primary"
      classes="w-full md:w-48"
    >
      Start Game
    </Button>
  </svelte:fragment>
</PageLayout>
