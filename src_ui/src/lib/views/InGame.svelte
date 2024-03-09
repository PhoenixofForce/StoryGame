<script lang="ts">
  import { addEventHandler } from "../services/websocketService";
  import { sendSubmitStoryMessage } from "../services/gameservice";
  import { lobbyStore } from "../services/lobbyService";
  import type {
    GameStateUpdateMessage,
    StartRoundTriggerMessage,
  } from "../services/messageTypes";
  import { displayLobby } from "../services/navigationService";

  let storyEnd = "";
  let story = "";

  let currentRound = 0;
  let maxRounds = 0;
  let submittedStory = false;

  let playersReady = 0;

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
  });

  addEventHandler("game-update", {
    onSuccess: (e) => {
      const data = e as GameStateUpdateMessage;
      playersReady = data.finishedPlayers;
    },
  });

  addEventHandler("end_game", {
    onSuccess: displayLobby,
  });

  function sendStory() {
    sendSubmitStoryMessage(story);
    story = "";
    submittedStory = true;
  }
</script>

<div class="text-5xl mb-4 font-bold tracking-wide drop-shadow">
  The Story Game
</div>
<p class="mb-60 italic text-slate-400">
  Round {currentRound + 1} / {maxRounds + 1}<br />
  {#if playersReady > 0}
    <span>{playersReady} / {$lobbyStore.players.length} Players are ready</span>
  {/if}
</p>

<div class="">
  {#if !submittedStory}
    <p style="text-align: left">{storyEnd}</p>
    <textarea
      bind:value={story}
      placeholder="Type your message"
      class="mb-8 w-full h-34"
    />
    <button class="blue float-right w-32" disabled={!story} on:click={sendStory}
      >Send</button
    >
  {:else}
    <div class="tracking-widest text-center">Waiting for other players...</div>
  {/if}
</div>

<style>
</style>
