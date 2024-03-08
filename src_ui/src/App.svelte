<script lang="ts">
  import Login from "./lib/views/Login.svelte";
  import Lobby from "./lib/views/Lobby.svelte";
  import InGame from "./lib/views/InGame.svelte";

  import { viewStore } from "./lib/services/navigationService";
</script>

<main>
  {$viewStore}
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
