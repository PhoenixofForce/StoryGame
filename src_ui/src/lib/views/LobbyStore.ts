import { displayInGame } from "$lib/services/navigationService";
import { addEventHandler } from "../services/websocketService";
import { writable } from "svelte/store";

export const lobbyStore = writable({
  roomCode: "",
  players: <string[]>[],
  you: "",
  host: "",
});

addEventHandler("lobby-change", {
  onSuccess: (data) => {
    if (!data.players) return;

    const players = data.players;
    const index = players.indexOf(data.you);
    if (index > -1) {
      players.splice(index, 1);
      players.unshift(data.you);
    }

    lobbyStore.set({
      roomCode: data.roomCode,
      players: players,
      you: data.you,
      host: data.host,
    });
  },
});

addEventHandler("start_game", {
  onSuccess: (e) => {
    console.log("start: ", e);
    displayInGame();
  },
});
