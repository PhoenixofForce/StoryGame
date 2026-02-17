<script lang="ts">
  import { Send } from "lucide-svelte";
  import InputField from "../components/InputField.svelte";
  import {
    addEventHandler,
    removeEventHandler,
  } from "../services/websocketService";
  import { sendSubmitStoryMessage } from "../services/gameService";
  import { lobbyStore } from "../services/lobbyService";
  import type {
    GameStateUpdateMessage,
    StartRoundMessage,
  } from "../services/messageTypes";
  import { displayEvaluation } from "../services/navigationService";
  import { onDestroy } from "svelte";
  import Spinner from "../components/Spinner.svelte";
  import Button from "../components/Button.svelte";
  import PageLayout from "../components/PageLayout.svelte";

  let fullStory = "";
  let storyEnd = "";
  let storyInputField: InputField;

  let currentRound = 0;
  let maxRounds = 0;
  let submittedStory = false;

  let playersReady = 0;

  let handlers = [
    addEventHandler("start_round", {
      onSuccess: (e) => {
        const data = e as StartRoundMessage;
        storyEnd = data.lastStorySnippet;
        currentRound = data.currentRound;
        maxRounds = data.maxRounds;

        if (storyInputField) storyInputField.resetStory();
        submittedStory = false;
        playersReady = 0;
      },
    }),

    addEventHandler("game-update", {
      onSuccess: (e) => {
        const data = e as GameStateUpdateMessage;
        playersReady = data.finishedPlayers;
      },
    }),

    addEventHandler("end_game", {
      onSuccess: displayEvaluation,
    }),
  ];

  function sendStory() {
    const teaser = storyInputField.splitStoryIntoStartAndEnd(fullStory)[1];
    sendSubmitStoryMessage(fullStory.replace("~", ""), teaser.replace("~", ""));
    storyInputField.resetStory();
    submittedStory = true;
  }

  onDestroy(() => {
    for (let handler of handlers) {
      removeEventHandler(handler);
    }
  });
</script>

<PageLayout noCard={true}>
  <svelte:fragment slot="content">
    {#if !submittedStory}
      <div class="flex h-full flex-col">
        <div class="mb-2">
          <div class="text-left text-2xl font-bold tracking-wide drop-shadow-sm sm:text-3xl">
            Round {currentRound} / {maxRounds}
          </div>
          <p class="min-h-5 text-left text-sm text-slate-400 italic">
            {#if playersReady > 0}
              {playersReady} / {$lobbyStore.players.length} Players are ready
            {/if}
          </p>
        </div>
        <p class="mb-2 text-left">{storyEnd}</p>
        <div class="min-h-0 flex-1">
          <InputField bind:this={storyInputField} bind:fullStory />
        </div>
      </div>
    {:else}
      <div class="flex h-full w-full flex-col items-center justify-center">
        <Spinner />
        <div class="mt-8 tracking-widest text-slate-500">
          Waiting for other players...
        </div>
      </div>
    {/if}
  </svelte:fragment>

  <svelte:fragment slot="actions">
    {#if !submittedStory}
      <span class="mr-auto text-sm text-slate-400 italic md:text-base">
        <b>Hint</b> Use ~ to control what the next player can see.
      </span>
      <Button
        type="primary"
        classes="w-full md:w-48"
        icon={Send}
        onClick={sendStory}
        disabled={!fullStory ||
          fullStory.length < storyInputField.MIN_SENTENCE_LENGTH}
      >
        Send
      </Button>
    {/if}
  </svelte:fragment>
</PageLayout>