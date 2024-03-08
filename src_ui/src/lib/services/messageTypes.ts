export type { BaseMessage, JoinMessage, LobbyStateMessage }

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