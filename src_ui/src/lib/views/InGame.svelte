<script lang="ts">
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

  let storyEnd = "";
  let story = "";

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

        story = "";
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
    sendSubmitStoryMessage(story);
    story = "";
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

<div class="mt-32 md:mt-64">
  {#if !submittedStory}
    <p style="text-align: left">{storyEnd}</p>
    <textarea
      bind:value={story}
      placeholder="Type your message"
      class="mb-4 w-full h-64"
    />
    <button
      class="blue float-right w-full md:w-32"
      disabled={!story}
      on:click={sendStory}
    >
      Send
    </button>
  {:else}
    <div class="tracking-widest text-center">Waiting for other players...</div>
  {/if}
</div>

<style>
</style>
