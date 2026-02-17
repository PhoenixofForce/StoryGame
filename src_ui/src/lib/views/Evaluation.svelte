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
  import PageLayout from "../components/PageLayout.svelte";
  import Button from "../components/Button.svelte";

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

  $: buttons = [
    {
      text: "To Lobby",
      icon: Undo,
      onClick: displayLobby,
      visible: wasStoryEnd && wasLastStory,
      disabled: false,
    },
    {
      text: "Download",
      icon: ArrowBigDownDash,
      onClick: downloadStory,
      visible: wasStoryEnd,
      disabled: false,
    },
    {
      text: "Next " + (wasStoryEnd ? "Story" : "Message"),
      icon: wasStoryEnd ? ChevronLast : ChevronRight,
      onClick: next,
      visible: $lobbyStore.you === $lobbyStore.host && !wasLastStory,
      disabled: !$canSpeak,
    },
  ];
  $: visibleButtons = buttons.filter((b) => b.visible);
  $: lastVisibleButtonIndex = visibleButtons.length - 1;
</script>

<PageLayout>
  <svelte:fragment slot="title">
    <h2 class="font-bold tracking-wide text-slate-700 dark:text-slate-100">
      Story from {currentCreator}
    </h2>
  </svelte:fragment>

  <svelte:fragment slot="content">
    <div class="flex flex-col">
      {#each revealedParts as part, index (index)}
        <div class:self-end={$lobbyStore.you === part.writer}>
          <div
            class="font-bold"
            class:text-end={$lobbyStore.you === part.writer}
          >
            {part.writer}
          </div>
          <div
            class="w-fit rounded-3xl px-5 py-2 shadow-sm"
            class:mb-2={index !== revealedParts.length - 1}
            class:bg-slate-100={$lobbyStore.you !== part.writer}
            class:bg-green-100={$lobbyStore.you === part.writer}
            class:dark:bg-slate-600={$lobbyStore.you !== part.writer}
            class:dark:bg-green-700={$lobbyStore.you === part.writer}
          >
            {part.text}
          </div>
        </div>
      {/each}
    </div>
  </svelte:fragment>

  <svelte:fragment slot="actions">
    {#each visibleButtons as button, i (button.text)}
      {#if button.visible}
        <Button
          type={i == lastVisibleButtonIndex ? "primary" : "default"}
          icon={button.icon}
          onClick={button.onClick}
          classes="w-full sm:w-auto"
          disabled={button.disabled}
        >
          {button.text}
        </Button>
      {/if}
    {/each}
  </svelte:fragment>
</PageLayout>
