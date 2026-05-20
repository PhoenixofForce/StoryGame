import { addEventHandler } from "../services/websocketService";
import { writable } from "svelte/store";
import { speak } from "../services/speakService";

export const evaluationStore = writable({
  wasStoryEnd: false,
  wasLastStory: false,
  revealedParts: <Array<{ text: string; writer: string }>>[],
  currentCreator: "",
});

addEventHandler("next_story", {
  onSuccess: (data) => {
    evaluationStore.update((s) => ({
      ...s,
      currentCreator: data.creator,
      revealedParts: [],
      wasStoryEnd: false,
    }));
  },
});

addEventHandler("reveal_story", {
  onSuccess: (data) => {
    evaluationStore.update((s) => ({
      ...s,
      revealedParts: [
        ...s.revealedParts,
        { text: data.text, writer: data.writer },
      ],
      wasStoryEnd: data.storyEnd,
      wasLastStory: data.lastStory,
    }));
    speak(data.writer + " wrote: " + data.text);
  },
});
