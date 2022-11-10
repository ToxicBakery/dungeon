package com.toxicbakery.game.dungeon

import com.benasher44.uuid.uuid4
import com.toxicbakery.game.dungeon.model.client.ClientMessage
import com.toxicbakery.game.dungeon.model.client.ClientMessage.ServerMessage
import com.toxicbakery.game.dungeon.model.client.ExpectedResponseType
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.util.textFrame
import io.ktor.http.cio.websocket.close
import io.ktor.websocket.WebSocketServerSession
import kotlinx.serialization.encodeToHexString
import kotlinx.serialization.protobuf.ProtoBuf

data class WebSocketGameSession(
    override val sessionId: String = uuid4().toString(),
    private val webSocketServerSession: WebSocketServerSession
) : GameSession {

    @Volatile
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
        webSocketServerSession.send(textFrame(output))
    }

    override suspend fun sendClientMessage(clientMessage: ClientMessage) {
        if (_isClosed) return
        val output: String = ProtoBuf.encodeToHexString(ClientMessage.serializer(), clientMessage)
        webSocketServerSession.send(textFrame(output))
    }

    override suspend fun close() {
        webSocketServerSession.close()
        _isClosed = true
    }
}
