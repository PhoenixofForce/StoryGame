<script lang="ts">
  import PlayerPlague from "./PlayerPlaque.svelte";

  export let playerNames: { name: string; color?: string }[];
</script>

<div>
  {#each playerNames as player (player.name)}
    <PlayerPlague
      name={player.name}
      color={player.color ? player.color : "slate"}
    />
  {/each}
</div>
