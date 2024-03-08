import type { JoinMessage } from "./messageTypes";
import { connect, sendMessage, addEventHandler } from "./websocketService";

export { addEventHandler };

const url = "ws://localhost:8080/hello";

export function sendJoinMessage(username: string, roomCode: string, type: "join" | "create") {
    const data: JoinMessage = {
        type: "join",
        error: false,
        message: "",
        name: username,
        room: roomCode,
        joinType: type,
    }

    connect(url).then(() => {
        sendMessage(data);
    }).catch(() => {
        console.log("something whent wrong");
    });
}