const url = "ws://localhost:8080/hello";
let socket: WebSocket;

let eventHandler: { eventType: string, handler: SocketEventHandler }[] = [];

export interface SocketEventHandler {
    onSuccess?: (data: any) => void;
    onError?: (data: any) => void;
};

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

function sendMessage(data: BaseMessage) {
    socket.send(JSON.stringify(data));
}

export function addEventHandler(eventType: string, handler: SocketEventHandler) {
    eventHandler.push({
        eventType: eventType,
        handler: handler
    });
}

function fireEvent(type: string, isError: boolean, data: any) {
    eventHandler.filter(e => e.eventType)
        .forEach(e => {
            if (isError && e.handler.onError) e.handler.onError(data);
            if (!isError && e.handler.onSuccess) e.handler.onSuccess(data);
        });
}

export function connect(username: string, roomCode: string, type: "join" | "create") {
    const data: JoinMessage = {
        type: "join",
        error: false,
        message: "",
        name: username,
        room: roomCode,
        joinType: type,
    }

    socket = new WebSocket(url);
    socket.addEventListener("open", () => {
        console.log("Opened");
        sendMessage(data);
    });

    socket.onmessage = (e) => {
        //console.log("received", e.data)
        const object = JSON.parse(e.data);
        const isError: boolean = object.error;

        if (isError) {
            fireEvent(object.type, isError, object);
        }
    };
}