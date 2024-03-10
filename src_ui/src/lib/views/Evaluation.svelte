<script lang="ts">
  import PlayerPlaque from "../components/PlayerPlaque.svelte";
  import { lobbyStore } from "../services/lobbyService";
  import { addEventHandler } from "../services/websocketService";
  import { sendRequestRevealMessage } from "../services/gameservice";
  import type {
    StoryRevealMessage
  } from "../services/messageTypes";


  let wasStoryEnd: boolean = false;
  let revealedParts: Array<{ text: string; writer: string }> = [];

  addEventHandler("reveal_story", {
    onSuccess: (e) => {
      const data = e as StoryRevealMessage;

      if (wasStoryEnd) {
        console.log("clearing prev stories");
        revealedParts = [];
      }
      // reassign to trigger reactivity
      revealedParts = [
        ...revealedParts,
        {
          text: data.text,
          writer: data.writer,
        },
      ];
      wasStoryEnd = data.storyEnd;
      console.log("should it end here?", data);
    }
  });
</script>

<div>
  <h1 class="text-lg font-bold">Story</h1>

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
    Click to Reveal next {wasStoryEnd ? "Story" : "Message"}
  </button>
</div>

<style>
</style>
