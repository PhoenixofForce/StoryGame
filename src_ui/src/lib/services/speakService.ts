import { writable } from "svelte/store";

export const canSpeak = writable(true);

export function speak(text: string) {
    canSpeak.set(false);
    const currentUtterance = new SpeechSynthesisUtterance(text);
    const voices = speechSynthesis.getVoices();
    currentUtterance.voice = voices[0]; // Choose a specific voice
    speechSynthesis.speak(currentUtterance);

    currentUtterance.onend = (_) => {
        canSpeak.set(true);
    }
}