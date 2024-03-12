import type { BaseMessage } from "./messageTypes";
export { connect, sendMessage, addEventHandler };

let socket: WebSocket;

function connect(url: string): Promise<void> {
    return new Promise((resolve, reject) => {
        socket = new WebSocket(url);
        socket.addEventListener("open", () => {
            console.log("Opened");
            startHeartbeat();
            resolve();
        });

        socket.addEventListener("error", (error) => {
            console.log(error);
            reject();
        });

        socket.onmessage = (e) => {
            const object = JSON.parse(e.data);
            if (!("error" in object && "type" in object)) return;

            const isError: boolean = object.error;
            fireEvent(object.type, isError, object);
        };
    });
}

function sendMessage(data: BaseMessage) {
    socket.send(JSON.stringify(data));
}

let eventHandler: { eventType: string, handler: SocketEventHandler }[] = [];

interface SocketEventHandler {
    onSuccess?: (data: BaseMessage) => void;
    onError?: (data: BaseMessage) => void;
};

function addEventHandler(eventType: string, handler: SocketEventHandler) {
    eventHandler.push({
        eventType: eventType,
        handler: handler
    });
}

function fireEvent(type: string, isError: boolean, data: any) {
    console.log("fire", type)
    eventHandler.filter(e => e.eventType === type)
        .forEach(e => {
            if (isError && e.handler.onError) e.handler.onError(data);
            if (!isError && e.handler.onSuccess) e.handler.onSuccess(data);
        });
}

function startHeartbeat() {
    setInterval(
        () => {
            fetch(import.meta.env.VITE_REST_URL).then((response) => {

            });
        }, 10 * 1000
    );
}