<script lang="ts">
  import PlayerPlague from "./PlayerPlaque.svelte";

  export let playerNames: { name: string; tags?: string[] }[];
</script>

<div>
  {#each playerNames as player (player.name)}
    <PlayerPlague name={player.name} tags={player.tags} />
  {/each}
</div>
