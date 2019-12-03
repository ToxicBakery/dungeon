package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.model.client.ClientMessage
import com.toxicbakery.game.dungeon.model.client.ClientMessage.ServerMessage
import com.toxicbakery.game.dungeon.model.client.ClientMessage.UserMessage
import com.toxicbakery.logging.Arbor
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.dumps
import kotlinx.serialization.loads
import kotlinx.serialization.protobuf.ProtoBuf
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import org.w3c.dom.events.Event

@ImplicitReflectionSerializer
class SocketClient(
    private val host: String,
    private val terminal: Terminal
) {

    private lateinit var socket: WebSocket
    private var _connected: Boolean = false
    private val protoBuf: ProtoBuf = ProtoBuf()

    var isConnected: Boolean
        get() = _connected
        private set(value) {
            _connected = value
        }

    fun start() {
        socket = WebSocket("ws://$host/ws")
        socket.onopen = { onOpen() }
        socket.onclose = { onClose() }
        socket.onerror = { event -> onError(event) }
        socket.onmessage = { event: MessageEvent -> onMessage(event) }
    }

    fun stop() {
        socket.close()
    }

    @UnstableDefault
    fun sendMessage(message: String) {
        if (!isConnected) return
        val userMessage = UserMessage(message)
        terminal.displayMessage(userMessage)
        val output = protoBuf.dumps(ClientMessage.serializer(), userMessage)
        Arbor.d("Sending message: %s\n%s\noutput: %s", message, userMessage, output)
        socket.send(output)
    }

    private fun onOpen() {
        Arbor.d("Socket opened")
        isConnected = true
    }

    private fun onError(event: Event) {
        Arbor.d("Socket Error: %s", event)
    }

    private fun onMessage(event: MessageEvent) {
        when (val message = event.data) {
            is String -> handleText(message)
            else -> Arbor.d("Unhandled message %s", event)
        }
    }

    private fun handleText(text: String) {
        val clientMessage = protoBuf.loads(ClientMessage.serializer(), text)
        Arbor.d("Received message: %s", clientMessage.toString())
        handleMessage(clientMessage)
    }

    private fun onClose() {
        Arbor.d("Socket closed")
        isConnected = false
        handleMessage(ServerMessage("Disconnected"))
    }

    private fun handleMessage(message: ClientMessage) = terminal.displayMessage(message)

}
