import { writable } from "svelte/store";

export const canSpeak = writable(true);

let currentUtterance: SpeechSynthesisUtterance;
export function speak(text: string) {
    cancel();

    canSpeak.set(false);
    currentUtterance = new SpeechSynthesisUtterance(text);
    const voices = speechSynthesis.getVoices();
    currentUtterance.voice = voices[0]; // Choose a specific voice
    speechSynthesis.speak(currentUtterance);

    setTimeout(() => {
        canSpeak.set(true);
    }, 1000);
    currentUtterance.onend = (_) => {
        canSpeak.set(true);
    }
}

export function cancel() {
    window.speechSynthesis.cancel();
}