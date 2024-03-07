<script lang="ts">
  import Login from "./lib/views/Login.svelte";
  import PlayerDisplay from "./lib/components/PlayerDisplay.svelte";
</script>

<main>
  <PlayerDisplay playerNames={["Alice", "Bob", "Peter"]} />
  <Login />
</main>

<style>
</style>
