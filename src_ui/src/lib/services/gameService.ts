import type { JoinMessage } from "./messageTypes";
import { connect, sendMessage } from "./websocketService";

const url = import.meta.env.VITE_API_URL;
const fullUrl = url + "/game";

export function sendJoinMessage(username: string, roomCode: string) {
    const data: JoinMessage = {
        type: "join",
        error: false,
        message: "",
        name: username,
        room: roomCode
    }

    connect(fullUrl).then(() => {
        sendMessage(data);
    }).catch(() => {
        console.log("something whent wrong");
    });
}

export function sendSubmitStoryMessage(fullStory: string, teaser: string) {
    const data = {
        type: "submit_story",
        error: false,
        fullStory: fullStory,
        teaser: teaser
    }
    sendMessage(data);
}

export function sendRequestRevealMessage() {
    const data = {
        type: "request_reveal",
        error: false,
        message: ""
    }
    sendMessage(data);
}

export function sendNextStoryRequest() {
    const data = {
        type: "next_story_trigger",
        error: false,
        message: "",
    }
    sendMessage(data);
}