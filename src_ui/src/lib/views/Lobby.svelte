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
  import Card from "../components/Card.svelte";
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

<div class="md:fixed md:left-12 md:top-5">
  <div class="mb-4">
    <span class="font-semibold"> </span>
    <Button icon={Clipboard} onClick={shareRoomCodeLink}>
      {$lobbyStore.roomCode}
    </Button>
  </div>

  <div>
    <PlayerDisplay
      playerNames={$lobbyStore.players.map((player) => {
        return {
          name: getPlayerDisplayName(player),
          color: getPlayerColor(player),
        };
      })}
    />
  </div>
</div>

<div class=" flex items-center justify-center">
  <div class="flex flex-col">
    <h2 class="mb-2 text-center font-bold tracking-wide text-slate-700">
      Settings
    </h2>
    <Card
      classes="w-128 h-128 xl:h-200 xl:w-222 xl:p-38 px-8 py-8 rounded-lg  py-5 xl:py-16"
    >
      <div class="align-center flex h-full w-full flex-col justify-between">
        <div>
          <input
            disabled={true}
            type="number"
            class="w-full"
            placeholder="Number of Rounds"
          />

          <p class="mt-2">Settings are currently not supported...</p>
        </div>
        <div class="flex w-full flex-row flex-nowrap justify-end">
          <Button
            icon={PenTool}
            onClick={startGame}
            disabled={$lobbyStore.you !== $lobbyStore.host}
            type="primary"
            classes="w-full md:w-48"
          >
            Start Game
          </Button>
        </div>
      </div>
    </Card>
  </div>
</div>

<style>
</style>
