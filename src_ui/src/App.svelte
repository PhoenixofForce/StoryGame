<script lang="ts">
  import Login from "./lib/views/Login.svelte";
  import Lobby from "./lib/views/Lobby.svelte";
  import InGame from "./lib/views/InGame.svelte";
  import Evaluation from "./lib/views/Evaluation.svelte";

  import { viewStore } from "./lib/services/navigationService";
  import Controls from "./lib/components/Controls.svelte";

  window.onbeforeunload = function () {
    if ($viewStore === "login") {
      return undefined;
    }
    return "Are you sure you want to leave?";
  };
</script>

<main class="w-full">
  {#if import.meta.env.DEV}
    <span class="fixed top-0 left-0">State: {$viewStore}</span>
  {/if}
  <Controls />

  {#if $viewStore === "login"}
    <Login />
  {:else if $viewStore === "lobby"}
    <Lobby />
  {:else if $viewStore === "ingame"}
    <InGame />
  {:else if $viewStore === "evaluation"}
    <Evaluation />
  {/if}
</main>

<style>
</style>
