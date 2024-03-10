<script lang="ts">
  import PlayerPlaque from "../components/PlayerPlaque.svelte";
  import { lobbyStore } from "../services/lobbyService";

  let revealedParts = [
    {
      text: "Peter eats some Hot dog",
      writer: "Bob",
    },
    {
      text: "It was very hot",
      writer: "Alics",
    },
  ];
</script>

<div>
  <h1 class="text-lg font-bold">Story from Peter</h1>

  {#each revealedParts as part}
    <div class="w-full">
      <div class="font-bold" class:text-end={$lobbyStore.you === part.writer}>
        {part.writer}
      </div>
      <div class="bg-slate-100 px-3 py-1 rounded-full mb-2 shadow w-fit">
        {part.text}
      </div>
    </div>
  {/each}

  <div
    class="bg-blue-100 tracking-wide font-bold px-6 py-3 rounded-full mt-6 shadow w-fit"
  >
    Click to Reveal next message
  </div>
</div>

<style>
</style>
