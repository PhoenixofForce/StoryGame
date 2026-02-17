import type { BaseMessage } from "./messageTypes";
export { connect, sendMessage, addEventHandler, removeEventHandler };

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

let eventHandler: {
  eventType: string;
  handler: SocketEventHandler;
  id: number;
}[] = [];

interface SocketEventHandler {
  onSuccess?: (data: BaseMessage) => void;
  onError?: (data: BaseMessage) => void;
}

function addEventHandler(
  eventType: string,
  handler: SocketEventHandler,
): number {
  const id = Math.random();
  eventHandler.push({
    eventType: eventType,
    handler: handler,
    id: id,
  });
  return id;
}

function removeEventHandler(id: number) {
  eventHandler = eventHandler.filter((handler) => handler.id != id);
}

function fireEvent(type: string, isError: boolean, data: BaseMessage) {
  console.log(type, data);
  eventHandler
    .filter((e) => e.eventType === type)
    .forEach((e) => {
      if (isError && e.handler.onError) e.handler.onError(data);
      if (!isError && e.handler.onSuccess) e.handler.onSuccess(data);
    });
}

function startHeartbeat() {
  const baseUrl = import.meta.env.VITE_REST_URL;
  const fullUrl = baseUrl + "/ping";
  setInterval(() => {
    fetch(fullUrl).then(() => {});
    sendMessage({
      type: "ping",
      error: false,
      message: "",
    });
  }, 10 * 1000);
}
