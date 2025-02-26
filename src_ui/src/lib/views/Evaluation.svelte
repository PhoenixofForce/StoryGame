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
  <h1 class="text-lg font-bold">
    Story from {currentCreator}
  </h1>
  <hr class="my-4 h-px w-full bg-gray-200 dark:bg-gray-800" />

  <div class="messages flex flex-col rounded-2xl bg-white p-2 shadow">
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
    <div class="mt-6 w-fit place-self-center">
      {#if wasStoryEnd}
        <button
          class="whitespace-break-nowrap mb-4 w-full md:w-fit"
          on:click={downloadStory}
        >
          <ArrowBigDownDash class="mr-1" /> Download
        </button>
        {#if wasLastStory}
          <button class="w-full md:w-fit" on:click={displayLobby}>
            <Undo class="mr-1" /> Back to Lobby
          </button>
        {/if}
      {/if}
      {#if $lobbyStore.you === $lobbyStore.host && !wasLastStory}
        {#if wasStoryEnd}
          <button
            class="green w-full md:w-fit"
            on:click={next}
            disabled={!$canSpeak}
          >
            <ChevronLast class="mr-1" /> Next Story
          </button>
        {:else}
          <button
            class=" blue w-full rounded-full px-6 py-3 font-bold tracking-wide shadow-sm md:w-fit"
            on:click={next}
            disabled={!$canSpeak}
          >
            <ChevronRight class="mr-1" /> Next Message
          </button>
        {/if}
      {/if}
    </div>
  </div>
</div>

<style>
  .messages {
    width: 60%;
    max-width: 800px;
    min-width: 300px;
  }
</style>
