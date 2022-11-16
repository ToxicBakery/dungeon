package com.toxicbakery.game.dungeon

import com.benasher44.uuid.uuid4
import com.toxicbakery.game.dungeon.model.client.ClientMessage
import com.toxicbakery.game.dungeon.model.client.ClientMessage.ServerMessage
import com.toxicbakery.game.dungeon.model.client.ExpectedResponseType
import com.toxicbakery.game.dungeon.model.session.GameSession
import io.ktor.server.websocket.WebSocketServerSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.serialization.encodeToHexString
import kotlinx.serialization.protobuf.ProtoBuf

data class WebSocketGameSession(
    override val sessionId: String = uuid4().toString(),
    private val webSocketServerSession: WebSocketServerSession
) : GameSession {

    private var _isClosed = false

    override val isClosed: Boolean
        get() = _isClosed

    override suspend fun sendMessage(
        msg: String,
        expectedResponseType: ExpectedResponseType
    ) {
        if (_isClosed) return
        val clientMessage: ClientMessage = ServerMessage(msg, expectedResponseType)
        val output: String = ProtoBuf.encodeToHexString(ClientMessage.serializer(), clientMessage)
        webSocketServerSession.send(Frame.Text(output))
    }

    override suspend fun sendClientMessage(clientMessage: ClientMessage) {
        if (_isClosed) return
        val output: String = ProtoBuf.encodeToHexString(ClientMessage.serializer(), clientMessage)
        webSocketServerSession.send(Frame.Text(output))
    }

    override suspend fun close() {
        webSocketServerSession.close()
        _isClosed = true
    }
}
