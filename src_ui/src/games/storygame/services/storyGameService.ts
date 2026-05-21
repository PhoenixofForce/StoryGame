import type {
  RequestRevealMessage,
  SubmitStoryMessage,
} from "$common/services/messageTypes";
import { sendMessage } from "$common/services/websocketService";

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
