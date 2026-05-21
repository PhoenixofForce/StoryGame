import { addEventHandler } from "$common/services/websocketService";
import { writable } from "svelte/store";
import { type StoryGameState } from "../../../../common/services/messageTypes";

export const inGameStore = writable({} as StoryGameState);

addEventHandler("story_game_update", {
  onSuccess: (data) => {
    inGameStore.update(() => data);
    // TODO: speak(data.writer + " wrote: " + data.text);
  },
});
