import type { BaseMessage, BaseMessageUnion } from "./messageTypes";
export { connect, sendMessage, addEventHandler, removeEventHandler };

type MessageOfType = {
  [M in BaseMessageUnion as M["type"]]: M;
};
type EventType = keyof MessageOfType;

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

function sendMessage<Type extends EventType>(data: MessageOfType[Type]) {
  socket.send(JSON.stringify(data));
}

let eventHandler: {
  eventType: EventType;
  handler: SocketEventHandler<BaseMessage>;
  id: number;
}[] = [];

interface SocketEventHandler<TypedMessage extends BaseMessage> {
  onSuccess?: (data: TypedMessage) => void;
  onError?: (data: TypedMessage) => void;
}

let nextId = 0;
function addEventHandler<Type extends EventType>(
  eventType: Type,
  handler: SocketEventHandler<MessageOfType[Type]>,
): number {
  const id = nextId++;
  eventHandler.push({
    eventType: eventType,
    handler: handler as SocketEventHandler<BaseMessage>,
    id: id,
  });
  return id;
}

function removeEventHandler(id: number) {
  eventHandler = eventHandler.filter((handler) => handler.id !== id);
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
