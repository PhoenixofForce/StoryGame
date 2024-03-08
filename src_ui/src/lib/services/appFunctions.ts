import { writable } from "svelte/store";

export const viewStore = writable('login');

export function displayLogin() {
  viewStore.set('login');
}

export function displayLobby() {
  viewStore.set('room');
}