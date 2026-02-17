/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2026-02-17 18:25:35.

export interface BaseMessage {
    type: "BaseMessage" | "join" | "lobby-change" | "start_round" | "submit_story" | "game_update" | "request_reveal" | "reveal_story" | "start_game" | "end_game" | "next_story_trigger" | "next_story" | "ping";
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
    roomCode: string;
    players: string[];
    you: string;
    host: string;
}

export interface StartRoundMessage extends BaseMessage {
    type: "start_round";
    currentRound: number;
    maxRounds: number;
    lastStorySnippet: string;
}

export interface SubmitStoryMessage extends BaseMessage {
    type: "submit_story";
    fullStory: string;
    teaser: string;
}

export interface GameStateUpdateMessage extends BaseMessage {
    type: "game_update";
    finishedPlayers: number;
}

export interface RequestRevealMessage extends BaseMessage {
    type: "request_reveal";
}

export interface StoryRevealMessage extends BaseMessage {
    type: "reveal_story";
    writer: string;
    text: string;
    storyEnd: boolean;
    lastStory: boolean;
}

export interface StartGameTrigger extends BaseMessage {
    type: "start_game";
}

export interface EndGameTrigger extends BaseMessage {
    type: "end_game";
}

export interface NextStoryTrigger extends BaseMessage {
    type: "next_story_trigger";
}

export interface NextStoryMessage extends BaseMessage {
    type: "next_story";
    creator: string;
}

export interface Ping extends BaseMessage {
    type: "ping";
}

export type BaseMessageUnion = PlayerJoinMessage | LobbyStateMessage | StartRoundMessage | SubmitStoryMessage | GameStateUpdateMessage | RequestRevealMessage | StoryRevealMessage | StartGameTrigger | EndGameTrigger | NextStoryTrigger | NextStoryMessage | Ping;
