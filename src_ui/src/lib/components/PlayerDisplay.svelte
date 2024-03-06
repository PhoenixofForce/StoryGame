<script lang="ts">
  import PlayerPlague from "./PlayerPlaque.svelte";

  export let playerNames!: string[];
</script>

<div class="fixed left-8 top-6">
  {#each playerNames as player}
    <PlayerPlague name={player} />
  {/each}
</div>
