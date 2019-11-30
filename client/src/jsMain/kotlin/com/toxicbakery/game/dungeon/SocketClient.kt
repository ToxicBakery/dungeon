package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.client.ClientMessage.ServerMessage
import com.toxicbakery.game.dungeon.client.ClientMessage.UserMessage
import com.toxicbakery.logging.Arbor
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.parse
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import org.w3c.dom.events.Event

@ImplicitReflectionSerializer
class SocketClient(
    private val host: String,
    private val terminal: Terminal
) {

    private lateinit var socket: WebSocket
    private var connected: Boolean = false

    private val json = Json(
        configuration = JsonConfiguration(
            encodeDefaults = true,
            strictMode = true
        )
    )

    val isConnected: Boolean
        get() = connected

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

    fun sendMessage(message: UserMessage) {
        if (!connected) return
        terminal.displayMessage(ServerMessage(">${message.message}\n\n"))
        socket.send(json.stringify(UserMessage.serializer(), message))
    }

    private fun onOpen() {
        Arbor.d("Socket opened")
        connected = true
    }

    private fun onError(event: Event) {
        Arbor.d("Socket Error: %s", event)
    }

    private fun onMessage(event: MessageEvent) {
        Arbor.d("onMessage(${event.data})")
        when (val message = event.data) {
            is String -> handleMessage(json.parse(message))
            else -> Arbor.d("Unhandled message %s", event)
        }
    }

    private fun onClose() {
        Arbor.d("Socket closed")
        connected = false
    }

    private fun handleMessage(message: ServerMessage) = terminal.displayMessage(message)

}
