import { writable } from "svelte/store";
import { addEventHandler } from "./websocketService"
import { get } from 'svelte/store';

export const viewStore = writable('login');
export function displayLogin() {
  viewStore.set('login');
}

export function displayLobby() {
  viewStore.set('lobby');
}

export function displayInGame() {
  viewStore.set('ingame');
}

export function displayEvaluation() {
  viewStore.set('evaluation');
}

addEventHandler("start_game", {
  onSuccess: (_) => {
    if (get(viewStore) !== 'ingame') {
      displayInGame();
    }
  }
});

addEventHandler("end_game", {
  onSuccess: (_) => {
    if (get(viewStore) !== 'evaluation') {
      displayEvaluation();
    }
  }
})