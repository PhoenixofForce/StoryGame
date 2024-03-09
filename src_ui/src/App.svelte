<script lang="ts">
  import Login from "./lib/views/Login.svelte";
  import Lobby from "./lib/views/Lobby.svelte";
  import InGame from "./lib/views/InGame.svelte";

  import { viewStore } from "./lib/services/navigationService";
</script>

<main>
  <span class="fixed top-0 left-0">State: {$viewStore}</span>
  {#if $viewStore === "login"}
    <Login />
  {:else if $viewStore === "lobby"}
    <Lobby />
  {:else if $viewStore === "ingame"}
    <InGame />
  {/if}
</main>

<style>
</style>
