<script lang="ts">
  import {
    ArrowBigDownDash,
    Undo,
    ChevronLast,
    ChevronRight,
  } from "lucide-svelte";
  import { lobbyStore } from "./LobbyStore";
  import { inGameStore } from "./InGameStore";
  import {
    sendRequestRevealMessage  } from "$lib/services/gameService";
  import { canSpeak } from "$lib/services/speakService";
  import { displayLobby } from "$lib/services/navigationService";
  import { download } from "$lib/services/downloadService";
  import PageLayout from "$lib/components/PageLayout.svelte";
  import Button from "$lib/components/Button.svelte";
  import { m } from "$paraglide/messages.js";

  function next() {
    if ($inGameStore.allStoriesRevealed) {
      return;
    }

    sendRequestRevealMessage();
  }

  function downloadStory() {
    const filename = "story_from_" + $inGameStore.currentAuthor;
    let text = "";
    for (let revealedPart of $inGameStore.revealedChapters) {
      text += revealedPart.author + ":\r\n" + revealedPart.text + "\r\n\r\n";
    }

    download(filename, text);
  }

  const buttons = $derived([
    {
      text: m.common_to_lobby(),
      icon: Undo,
      onClick: displayLobby,
      visible: $inGameStore.allChaptersRevealed && $inGameStore.allStoriesRevealed,
      disabled: false,
    },
    {
      text: m.common_download(),
      icon: ArrowBigDownDash,
      onClick: downloadStory,
      visible: $inGameStore.allChaptersRevealed,
      disabled: false,
    },
    {
      text: $inGameStore.allChaptersRevealed
        ? m.storygame_eval_next_story()
        : m.storygame_eval_next_message(),
      icon: $inGameStore.allChaptersRevealed ? ChevronLast : ChevronRight,
      onClick: next,
      visible:
        $lobbyStore.you === $lobbyStore.host && !$inGameStore.allStoriesRevealed,
      disabled: !$canSpeak,
    },
  ]);
  const visibleButtons = $derived(buttons.filter((b) => b.visible));
  const lastVisibleButtonIndex = $derived(visibleButtons.length - 1);
</script>

<PageLayout>
  <svelte:fragment slot="title">
    <h2 class="text-base-content font-bold tracking-wide">
      {m.storygame_eval_story_from({
        creator: $inGameStore.currentAuthor,
      })}
    </h2>
  </svelte:fragment>

  <svelte:fragment slot="content">
    <div class="flex flex-col">
      {#each $inGameStore.revealedChapters as part, index (index)}
        <div
          class="chat {$lobbyStore.you === part.author
            ? 'chat-end'
            : 'chat-start'}"
        >
          <div class="chat-header text-neutral-content">
            {part.author}
          </div>
          <div
            class="chat-bubble {$lobbyStore.you === part.author
              ? 'chat-bubble-primary'
              : 'chat-bubble-accent'}"
          >
            {part.text}!
          </div>
        </div>
      {/each}
    </div>
  </svelte:fragment>

  <svelte:fragment slot="actions">
    {#each visibleButtons as button, i (button.text)}
      {#if button.visible}
        <Button
          type={i === lastVisibleButtonIndex ? "primary" : "default"}
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
