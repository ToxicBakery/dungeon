// Global variable to hold the websocket.
var socket = null;

/**
 * This function is in charge of connecting the client.
 */
function connect() {
    console.log("Begin connect");
    socket = new WebSocket("ws://" + window.location.host + "/ws");

    socket.onerror = function() {
        console.log("socket error");
    };

    socket.onopen = function() {
        write("Connected");
    };

    socket.onclose = function(evt) {
        var explanation = "";
        if (evt.reason && evt.reason.length > 0) {
            explanation = "reason: " + evt.reason;
        } else {
            explanation = "without a reason specified";
        }

        write("Disconnected with close code " + evt.code + " and " + explanation);
    };

    // If we receive a message from the server, we want to handle it.
    socket.onmessage = function(event) {
        received(event.data.toString());
    };
}

/**
 * Handle messages received from the sever.
 *
 * @param message The textual message
 */
function received(message) {
    write(message);
}

/**
 * Writes a message in the HTML 'messages' container that the user can see.
 *
 * @param message The message to write in the container
 */
function write(message) {
    var messagesDiv = document.getElementById("messages");
    var messageP;
    if (messagesDiv.childNodes.length > 500) {
        messageP = messagesDiv.childNodes[0];
        messagesDiv.removeChild(messageP);
    } else {
        messageP = document.createElement("p");
        messageP.className = "message";
    }
    messageP.innerHTML = message.replace(/(?:\r\n|\r|\n)/g, '<br>');
    messagesDiv.appendChild(messageP);
    messagesDiv.scrollTop = messageP.offsetTop;
}

/**
 * Function in charge of sending the 'commandInput' text to the server via the socket.
 */
function onSend() {
    var input = document.getElementById("commandInput");
    if (input) {
        var text = input.value;
        if (text && socket) {
            socket.send(text);
            input.value = "";
        }
    }
}

/**
 * The initial code to be executed once the page has been loaded and is ready.
 */
function start() {
    connect();
    document.getElementById("sendButton").onclick = onSend;
    document.getElementById("commandInput").onkeydown = function(e) {
        if (e.keyCode == 13) {
            onSend();
        }
    };
}

/**
 * The entry point of the client.
 */
function initLoop() {
    if (document.getElementById("sendButton")) {
        start();
    } else {
        setTimeout(initLoop, 300);
    }
}

initLoop();
