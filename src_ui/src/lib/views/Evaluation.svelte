<script lang="ts">
  import {
    ArrowBigDownDash,
    Undo,
    ChevronLast,
    ChevronRight,
  } from "lucide-svelte";
  import { lobbyStore } from "../services/lobbyService";
  import {
    addEventHandler,
    removeEventHandler,
  } from "../services/websocketService";
  import {
    sendRequestRevealMessage,
    sendNextStoryRequest,
  } from "../services/gameService";
  import { canSpeak, speak } from "../services/speakService";
  import type {
    StoryRevealMessage,
    NextStoryMessage,
  } from "../services/messageTypes";
  import { displayLobby } from "../services/navigationService";
  import { download } from "../services/downloadService";
  import { onDestroy } from "svelte";
  import Card from "../components/Card.svelte";

  let wasStoryEnd = false;
  let wasLastStory = false;
  let revealedParts: Array<{ text: string; writer: string }> = [];
  let currentCreator = "";

  let handler = addEventHandler("next_story", {
    onSuccess: (e) => {
      const data = e as NextStoryMessage;
      currentCreator = data.creator;
      revealedParts = [];
      wasStoryEnd = false;
    },
  });

  let revealHandler = addEventHandler("reveal_story", {
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
      speak(data.writer + " wrote: " + data.text);
      wasStoryEnd = data.storyEnd;
      wasLastStory = data.lastStory;
    },
  });

  function next() {
    if (wasStoryEnd) {
      sendNextStoryRequest();
      return;
    }

    sendRequestRevealMessage();
  }

  function downloadStory() {
    const filename = "story_from_" + currentCreator;
    let text = "";
    for (let revealedPart of revealedParts) {
      text += revealedPart.writer + ":\r\n" + revealedPart.text + "\r\n\r\n";
    }

    download(filename, text);
  }

  onDestroy(() => {
    removeEventHandler(handler);
    removeEventHandler(revealHandler);
  });
</script>

<div class="flex flex-col items-center">
  <!-- Move this down as header of the Card -->
  <h1 class="text-lg font-bold">
    Story from {currentCreator}
  </h1>
  <hr class="my-4 h-px w-full bg-gray-200 dark:bg-gray-800" />

  <Card classes="px-8 py-16 sm:px-36 flex flex-col min-w-3/4 ">
    {#each revealedParts as part}
      <div class:self-end={$lobbyStore.you === part.writer}>
        <div class="font-bold" class:text-end={$lobbyStore.you === part.writer}>
          {part.writer}
        </div>
        <div
          class=" mb-2 w-fit rounded-3xl px-5 py-2 shadow-sm"
          class:bg-slate-100={$lobbyStore.you !== part.writer}
          class:bg-green-100={$lobbyStore.you === part.writer}
        >
          {part.text}
        </div>
      </div>
    {/each}
    <div
      class="mt-6 flex w-full flex-col-reverse flex-nowrap gap-3 place-self-center sm:flex-row sm:justify-center"
    >
      {#if wasStoryEnd}
        {#if wasLastStory}
          <button
            class="sm:max-w-50 w-full whitespace-nowrap sm:w-auto md:flex-1"
            on:click={displayLobby}
          >
            <Undo class="mr-1" /> Back to Lobby
          </button>
        {/if}
        <button
          class="sm:max-w-50 w-full flex-1 whitespace-nowrap last:bg-gradient-to-tr last:from-lime-100 last:to-orange-100 sm:w-auto"
          on:click={downloadStory}
        >
          <ArrowBigDownDash class="mr-1" /> Download
        </button>
      {/if}
      {#if $lobbyStore.you === $lobbyStore.host && !wasLastStory}
        {#if wasStoryEnd}
          <button
            class="sm:max-w-50 w-full flex-1 whitespace-nowrap last:bg-gradient-to-tr last:from-lime-100 last:to-orange-100 sm:w-auto"
            on:click={next}
            disabled={!$canSpeak}
          >
            <ChevronLast class="mr-1" /> Next Story
          </button>
        {:else}
          <button
            class="sm:max-w-50 w-full flex-1 whitespace-nowrap last:bg-gradient-to-tr last:from-lime-100 last:to-orange-100 sm:w-auto"
            on:click={next}
            disabled={!$canSpeak}
          >
            <ChevronRight class="mr-1" /> Next Message
          </button>
        {/if}
      {/if}
    </div>
  </Card>
</div>

<style>
</style>
