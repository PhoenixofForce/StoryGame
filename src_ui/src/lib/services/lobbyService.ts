import type { BaseMessage, LobbyStateMessage } from "./messageTypes";
import { addEventHandler } from "./websocketService";
import { writable } from "svelte/store";


export const lobbyStore = writable({
    roomCode: "",
    players: <string[]>[],
    you: "",
    host: "",
});

addEventHandler("lobby-change", {
    onSuccess: (dataIn: BaseMessage) => {
        if (!("players" in dataIn)) return;
        let data: LobbyStateMessage = dataIn as LobbyStateMessage;

        let players = data.players;
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
        })
    }
});