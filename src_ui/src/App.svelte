<script lang="ts">
  import Login from "$common/views/login/Login.svelte";
  import Lobby from "$common/views/lobby/Lobby.svelte";
  import { options } from "$common/services/options";
  import Controls from "$common/components/Controls.svelte";
  import { lobbyStore } from "$common/views/lobby/LobbyStore";
  import { inGameStore } from "$games/storygame/views/ingame/InGameStore";

  const loadGameRouter = (gameId: string) =>
    import(`./games/${gameId}/Router.svelte`);
  window.onbeforeunload = function () {
    if (!$lobbyStore.you) {
      return undefined;
    }
    return "Are you sure you want to leave?";
  };
</script>

{#key $options.language}
  <main class="h-full w-full">
    {#if import.meta.env.DEV}
      <span class="fixed top-0 left-0 z-10">
        <div>
          LobbyStore: {JSON.stringify($lobbyStore)}
        </div>
        <div>
          InGameStore: {JSON.stringify($inGameStore)}
        </div>
      </span>
    {/if}
    <Controls />

    {#if $lobbyStore.you && !$lobbyStore.gameName}
      <Lobby />
    {:else if $lobbyStore.you && $lobbyStore.gameName}
      {#await loadGameRouter($lobbyStore.gameName) then module}
        <svelte:component this={module.default} />
      {/await}
    {:else}
      <Login />
    {/if}
  </main>
{/key}

<style>
</style>
