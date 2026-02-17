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
    let out = player;
    if (player === $lobbyStore.you) out += " (you)";
    if (player === $lobbyStore.host) out += " (Host)";
    return out;
  }

  function getPlayerColor(player: string): string {
    let color = "slate";
    if (player === $lobbyStore.you) color = "blue";
    if (player === $lobbyStore.host) color = "yellow";
    return color;
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
        color: getPlayerColor(player),
      }))}
    />
  </svelte:fragment>

  <svelte:fragment slot="title">
    <h2 class="font-bold tracking-wide text-slate-700 dark:text-slate-100">
      Settings
    </h2>
  </svelte:fragment>

  <svelte:fragment slot="content">
    <input
      disabled={true}
      type="number"
      class="w-full"
      placeholder="Number of Rounds"
    />
    <p class="mt-2">Settings are currently not supported...</p>
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