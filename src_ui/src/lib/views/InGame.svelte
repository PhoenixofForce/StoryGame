<script lang="ts">
  import InputField from "../components/InputField.svelte";
  import {
    addEventHandler,
    removeEventHandler,
  } from "../services/websocketService";
  import { sendSubmitStoryMessage } from "../services/gameService";
  import { lobbyStore } from "../services/lobbyService";
  import type {
    GameStateUpdateMessage,
    StartRoundTriggerMessage,
  } from "../services/messageTypes";
  import { displayEvaluation } from "../services/navigationService";
  import { onDestroy } from "svelte";
  import Spinner from "../components/Spinner.svelte";

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
        const data = e as StartRoundTriggerMessage;
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

<div class="absolute top-4 left-8">
  <div class="text-5xl mb-4 font-bold tracking-wide drop-shadow">
    The Story Game
  </div>
  <p class="italic text-slate-400">
    Round {currentRound} / {maxRounds}<br />
    {#if playersReady > 0}
      <span
        >{playersReady} / {$lobbyStore.players.length} Players are ready</span
      >
    {/if}
  </p>
</div>

{#if !submittedStory}
  <div class="mt-32 md:mt-64">
    <p style="text-align: left">{storyEnd}</p>
    <InputField bind:this={storyInputField} bind:fullStory />
    <div class="h-fit">
      <span class="inline-block italic text-slate-400"
        ><b>Hint</b> Use ~ to control what the next player can see.</span
      >
      <button
        class="blue float-right w-full md:w-32 mt-4"
        disabled={!fullStory ||
          fullStory.length < storyInputField.MIN_SENTENCE_LENGTH}
        on:click={sendStory}
      >
        Send
      </button>
    </div>
  </div>
{:else}
  <div class="mt-72 w-full flex flex-col items-center justify-center">
    <Spinner />
    <div class="tracking-widest mt-8">Waiting for other players...</div>
  </div>
{/if}

<style>
</style>
