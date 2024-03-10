export type {
    BaseMessage,
    JoinMessage,
    LobbyStateMessage,
    StartRoundTriggerMessage,
    GameStateUpdateMessage,
    RequestRevealMessage,
    StoryRevealMessage
}

interface BaseMessage {
    type: string,
    error: boolean,
    message?: string;
}

interface JoinMessage extends BaseMessage {
    name: string,
    room: string,
    joinType: string;
}

interface LobbyStateMessage extends BaseMessage {
    roomCode: string,
    players: string[],
    you: string,
    host: string,
}

interface StartRoundTriggerMessage extends BaseMessage {
    currentRound: number,
    maxRounds: number,
    lastStorySnippet: string,
}

interface GameStateUpdateMessage extends BaseMessage {
    finishedPlayers: number;
}

interface RequestRevealMessage extends BaseMessage {}

interface StoryRevealMessage extends BaseMessage {
    writer: string,
    text: string
}