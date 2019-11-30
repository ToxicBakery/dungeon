package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.client.ClientMessage.ServerMessage
import com.toxicbakery.game.dungeon.client.ClientMessage.UserMessage
import com.toxicbakery.logging.Arbor
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.dump
import kotlinx.serialization.load
import kotlinx.serialization.protobuf.ProtoBuf
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.Int8Array
import org.khronos.webgl.Uint8Array
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import org.w3c.dom.events.Event
import org.w3c.files.Blob
import kotlin.js.Promise

@ImplicitReflectionSerializer
class SocketClient(
    private val host: String,
    private val terminal: Terminal
) {

    private lateinit var socket: WebSocket
    private var connected: Boolean = false
    private val protoBuf: ProtoBuf = ProtoBuf()

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

    fun sendMessage(
        message: UserMessage,
        hide:Boolean = false
    ) {
        if (!connected) return
        if (hide) terminal.displayMessage(ServerMessage("> ****"))
        else terminal.displayMessage(ServerMessage("> ${message.message}\n\n"))
        socket.send(protoBuf.dump(message).asUInt8Array())
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
            is Blob -> handleBlob(message)
            else -> Arbor.d("Unhandled message %s", event)
        }
    }

    private fun handleBlob(blob: Blob) = blob
        .arrayBuffer()
        .then { buffer ->
            handleMessage(protoBuf.load(buffer.asByteArray()))
        }

    private fun onClose() {
        Arbor.d("Socket closed")
        connected = false
    }

    private fun handleMessage(message: ServerMessage) = terminal.displayMessage(message)

    private fun ByteArray.asUInt8Array(): ArrayBufferView = Uint8Array(toTypedArray())

    private fun ArrayBuffer.asByteArray(): ByteArray = Int8Array(this).unsafeCast<ByteArray>()

    private fun Blob.arrayBuffer(): Promise<ArrayBuffer> = asDynamic().arrayBuffer() as Promise<ArrayBuffer>

}
