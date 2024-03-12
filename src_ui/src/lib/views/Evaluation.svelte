<script lang="ts">
  import { lobbyStore } from "../services/lobbyService";
  import { addEventHandler } from "../services/websocketService";
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

  let wasStoryEnd = false;
  let wasLastStory = false;
  let revealedParts: Array<{ text: string; writer: string }> = [];
  let currentCreator = "";

  addEventHandler("next_story", {
    onSuccess: (e) => {
      const data = e as NextStoryMessage;
      console.log(data);
      currentCreator = data.creator;
      revealedParts = [];
      wasStoryEnd = false;
    },
  });

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
</script>

<div class="flex flex-col items-center">
  <h1 class="text-lg font-bold">
    Story from {currentCreator}
  </h1>
  <hr class="h-px bg-gray-200 w-full dark:bg-gray-800 my-4" />

  <div class="messages flex flex-col">
    {#each revealedParts as part}
      <div class:self-end={$lobbyStore.you === part.writer}>
        <div class="font-bold" class:text-end={$lobbyStore.you === part.writer}>
          {part.writer}
        </div>
        <div
          class=" px-3 py-1 rounded-full mb-2 shadow w-fit"
          class:bg-slate-100={$lobbyStore.you !== part.writer}
          class:bg-green-100={$lobbyStore.you === part.writer}
        >
          {part.text}
        </div>
      </div>
    {/each}
  </div>

  <div class="mt-6">
    {#if wasStoryEnd}
      <button class="w-full md:w-32 mb-4" on:click={downloadStory}>
        Download
      </button>
      {#if wasLastStory}
        <button class="w-full md:w-fit" on:click={displayLobby}>
          Back to Lobby
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
          Click to reveal next Story
        </button>
      {:else}
        <button
          class=" w-full md:w-fit blue tracking-wide font-bold px-6 py-3 rounded-full shadow"
          on:click={next}
          disabled={!$canSpeak}
        >
          Click to reveal next Message
        </button>
      {/if}
    {/if}
  </div>
</div>

<style>
  .messages {
    width: 60%;
    max-width: 800px;
    min-width: 300px;
  }
</style>
