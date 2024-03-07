export type { BaseMessage, JoinMessage }

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
