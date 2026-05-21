import type { LobbyStateMessage } from "$common/services/messageTypes";
import { addEventHandler } from "$common/services/websocketService";
import { writable } from "svelte/store";

export const lobbyStore = writable({} as LobbyStateMessage);

addEventHandler("lobby-change", {
  onSuccess: (data) => {
    if (!data.players) return;

    const players = data.players;
    const index = players.indexOf(data.you);
    if (index > -1) {
      players.splice(index, 1);
      players.unshift(data.you);
    }

    lobbyStore.update((s) => ({ ...s, ...data, players }));
  },
});

addEventHandler("start_game", {
  onSuccess: (e) => {
    console.log("start: ", e);
  },
});
