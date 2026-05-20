<script lang="ts">
  import { PenTool, Clipboard } from "lucide-svelte";
  import PlayerDisplay from "$lib/components/PlayerDisplay.svelte";
  import { lobbyStore } from "./LobbyStore";
  import {
    sendMessage,
  } from "$lib/services/websocketService";
  import PageLayout from "$lib/components/PageLayout.svelte";
  import Button from "$lib/components/Button.svelte";
  import { m } from "$paraglide/messages.js";

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

  function getPlayerTags(player: string): string[] {
    const out = [];
    if (player === $lobbyStore.you) out.push("You");
    if (player === $lobbyStore.host) out.push("Host");
    return out;
  }

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

<PageLayout>
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
    <h2 class="text-base-content font-bold tracking-wide">
      {m.lobby_settings()}
    </h2>
  </svelte:fragment>

  <svelte:fragment slot="content">
    <div class="text-neutral-content">
      <input
        disabled={true}
        type="number"
        class="input w-full"
        placeholder="Number of Rounds"
      />
      <p class="mt-2">
        {m.lobby_settings_unsupported()}
      </p>
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
      {m.lobby_start_game()}
    </Button>
  </svelte:fragment>
</PageLayout>
