import { writable } from "svelte/store";

export const viewStore = writable('login');

export function switchToLogin() {
  viewStore.set('login');
}

export function switchToRoom() {
  viewStore.set('room');
}