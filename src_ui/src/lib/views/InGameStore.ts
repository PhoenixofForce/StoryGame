import { addEventHandler } from "../services/websocketService";
import { writable } from "svelte/store";
import { displayEvaluation } from "../services/navigationService";

export const inGameStore = writable({
  storyEnd: "",
  currentRound: 0,
  maxRounds: 0,
  submittedStory: false,
  playersReady: 0,
});

addEventHandler("start_round", {
  onSuccess: (data) => {
    inGameStore.update((s) => ({
      ...s,
      storyEnd: data.lastStorySnippet,
      currentRound: data.currentRound,
      maxRounds: data.maxRounds,
      submittedStory: false,
      playersReady: 0,
    }));
  },
});

addEventHandler("game_update", {
  onSuccess: (data) => {
    inGameStore.update((s) => ({
      ...s,
      playersReady: data.finishedPlayers,
    }));
  },
});

addEventHandler("end_game", {
  onSuccess: displayEvaluation,
});
