import { writable, get } from "svelte/store";
import { options } from "../services/options";

export const canSpeak = writable(true);

let currentUtterance: SpeechSynthesisUtterance;
export function speak(text: string) {
  cancel();

  canSpeak.set(false);
  currentUtterance = new SpeechSynthesisUtterance(text);
  const voices = speechSynthesis.getVoices();
  currentUtterance.voice = voices[0]; // Choose a specific voice
  console.log(get(options));
  if (get(options).allowSounds) {
    speechSynthesis.speak(currentUtterance);
  }

  setTimeout(() => {
    canSpeak.set(true);
  }, 1000);
  currentUtterance.onend = () => {
    canSpeak.set(true);
  };
}

export function cancel() {
  window.speechSynthesis.cancel();
}
