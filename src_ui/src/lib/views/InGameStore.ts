import { addEventHandler } from "../services/websocketService";
import { writable } from "svelte/store";
import { displayEvaluation } from "../services/navigationService";
import {
  StoryGamePhase,
  type StoryGameState,
} from "$lib/services/messageTypes";

export const inGameStore = writable({} as StoryGameState);

addEventHandler("story_game_update", {
  onSuccess: (data) => {
    inGameStore.update(() => data);
    if (data.phase === StoryGamePhase.REVEALING) {
      displayEvaluation();
    }
    // TODO: speak(data.writer + " wrote: " + data.text);
  },
});
