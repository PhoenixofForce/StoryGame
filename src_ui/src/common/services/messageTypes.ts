/* tslint:disable */

// Generated using typescript-generator version 3.2.1263 on 2026-05-21 19:13:52.

export interface BaseMessage {
  type:
    | "BaseMessage"
    | "join"
    | "lobby-change"
    | "submit_story"
    | "request_reveal"
    | "start_game"
    | "request_state"
    | "story_game_update"
    | "ping";
  message: string;
  error: boolean;
}

export interface PlayerJoinMessage extends BaseMessage {
  type: "join";
  name: string;
  room: string;
}

export interface LobbyStateMessage extends BaseMessage {
  type: "lobby-change";
  gameName: string;
  roomCode: string;
  players: string[];
  you: string;
  host: string;
}

export interface SubmitStoryMessage extends BaseMessage {
  type: "submit_story";
  fullStory: string;
  teaser: string;
}

export interface RequestRevealMessage extends BaseMessage {
  type: "request_reveal";
}

export interface StartGameTrigger extends BaseMessage {
  type: "start_game";
}

export interface RequestStateTrigger extends BaseMessage {
  type: "request_state";
}

export interface StoryGameState extends BaseMessage {
  type: "story_game_update";
  phase: StoryGamePhase;
  currentRound: number;
  maxRounds: number;
  finishedPlayers: number;
  lastStorySnippet: string;
  hasPlayerSubmitted: boolean;
  allChaptersRevealed: boolean;
  allStoriesRevealed: boolean;
  currentAuthor: string;
  revealedChapters: Chapter[];
}

export interface Ping extends BaseMessage {
  type: "ping";
}

export interface Chapter {
  text: string;
  author: string;
}

export type BaseMessageUnion =
  | PlayerJoinMessage
  | LobbyStateMessage
  | SubmitStoryMessage
  | RequestRevealMessage
  | StartGameTrigger
  | RequestStateTrigger
  | StoryGameState
  | Ping;

export const enum StoryGamePhase {
  WRITING = "WRITING",
  REVEALING = "REVEALING",
}
