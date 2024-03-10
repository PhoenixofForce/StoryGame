<script lang="ts">
  import PlayerPlaque from "../components/PlayerPlaque.svelte";
  import { lobbyStore } from "../services/lobbyService";
  import { addEventHandler } from "../services/websocketService";
  import { sendRequestRevealMessage } from "../services/gameservice";
  import type {
    StoryRevealMessage
  } from "../services/messageTypes";

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

  addEventHandler("reveal_story", {
    onSuccess: (e) => {
      const data = e as StoryRevealMessage;
      // reassign to trigger reactivity
      revealedParts = [
        ...revealedParts,
        {
          text: data.text,
          writer: data.writer,
        },
      ];
    }
  });
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

  <button
    class="bg-blue-100 tracking-wide font-bold px-6 py-3 rounded-full mt-6 shadow w-fit"
    on:click={sendRequestRevealMessage}
  >
    Click to Reveal next message
  </button>
</div>

<style>
</style>
