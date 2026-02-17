import type {
  NextStoryMessage,
  PlayerJoinMessage,
  RequestRevealMessage,
  SubmitStoryMessage,
} from "./messageTypes";
import { connect, sendMessage } from "./websocketService";

const url = import.meta.env.VITE_API_URL;
const fullUrl = url + "/game";

export function sendJoinMessage(username: string, roomCode: string) {
  const data: PlayerJoinMessage = {
    type: "join",
    error: false,
    message: "",
    name: username,
    room: roomCode,
  };

  connect(fullUrl)
    .then(() => {
      sendMessage(data);
    })
    .catch(() => {
      console.log("something went wrong");
    });
}

export function sendSubmitStoryMessage(fullStory: string, teaser: string) {
  const data: SubmitStoryMessage = {
    type: "submit_story",
    error: false,
    fullStory: fullStory,
    teaser: teaser,
    message: "",
  };
  sendMessage(data);
}

export function sendRequestRevealMessage() {
  const data: RequestRevealMessage = {
    type: "request_reveal",
    error: false,
    message: "",
  };
  sendMessage(data);
}

export function sendNextStoryRequest() {
  const data: NextStoryMessage = {
    type: "next_story",
    error: false,
    message: "",
    creator: "",
  };
  sendMessage(data);
}
