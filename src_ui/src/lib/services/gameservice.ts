const url = "ws://localhost:8080/hello";
let socket: WebSocket;

export function connect(username: string, roomCode: string) {
    const data = {
        name: username,
        room: roomCode,
    }

    socket = new WebSocket(url);
    socket.addEventListener("open", () => {
        console.log("Opened");
        socket.send("register " + JSON.stringify(data));
    });
    
    socket.onmessage = (e) => {
        console.log(e.data);
    };
}