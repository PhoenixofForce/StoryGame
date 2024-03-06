<script lang="ts">
  import Login from "./lib/components/Login.svelte";
  import Room from "./lib/components/Room.svelte";
  import { viewStore } from "./lib/services/appFunctions";
  import PlayerDisplay from "./lib/components/PlayerDisplay.svelte";
</script>

<main>
  <PlayerDisplay playerNames={["Alice", "Bob", "Peter"]} />
  {#if $viewStore === 'login'}
    <Login />
  {:else if $viewStore === 'room'}
    <Room />
  {/if}
</main>

<style>
</style>
