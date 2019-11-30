package com.toxicbakery.game.dungeon

import co.touchlab.stately.concurrency.AtomicBoolean
import com.benasher44.uuid.uuid4
import com.toxicbakery.game.dungeon.client.ClientMessage.ServerMessage
import com.toxicbakery.game.dungeon.client.ExpectedResponseType
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.model.session.PlayerSessionId
import com.toxicbakery.game.dungeon.util.binaryFrame
import io.ktor.websocket.WebSocketServerSession
import kotlinx.serialization.dump
import kotlinx.serialization.protobuf.ProtoBuf

class WebSocketGameSession(
    override val sessionId: PlayerSessionId = PlayerSessionId(uuid4().toString()),
    private val webSocketServerSession: WebSocketServerSession
) : GameSession {

    private val _isClosed = AtomicBoolean(false)
    private val protoBuf: ProtoBuf = ProtoBuf()

    override val isClosed: Boolean
        get() = _isClosed.value

    override suspend fun send(
        msg: String,
        expectedResponseType: ExpectedResponseType
    ) {
        if (_isClosed.value) return
        val output = protoBuf.dump(ServerMessage(msg, expectedResponseType))
        webSocketServerSession.send(binaryFrame(output))
    }

    override suspend fun close() {
        webSocketServerSession.close()
        _isClosed.value = true
    }

}
