<script lang="ts">
  import Login from "$common/views/login/Login.svelte";
  import Lobby from "$common/views/lobby/Lobby.svelte";
  import { options } from "$common/services/options";
  import Controls from "$common/components/Controls.svelte";
  import { lobbyStore } from "$common/views/lobby/LobbyStore";
    import DebugPanel from "$common/components/DebugPanel.svelte";

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
    <DebugPanel />
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
