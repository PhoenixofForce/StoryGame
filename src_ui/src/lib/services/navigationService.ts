import { writable, get } from "svelte/store";
import { addEventHandler } from "./websocketService";

export const viewStore = writable("login");
export function displayLogin() {
  viewStore.set("login");
}

export function displayLobby() {
  viewStore.set("lobby");
}

export function displayInGame() {
  viewStore.set("ingame");
}

export function displayEvaluation() {
  viewStore.set("evaluation");
}

addEventHandler("start_game", {
  onSuccess: () => {
    if (get(viewStore) !== "ingame") {
      displayInGame();
    }
  },
});

addEventHandler("end_game", {
  onSuccess: () => {
    if (get(viewStore) !== "evaluation") {
      displayEvaluation();
    }
  },
});
