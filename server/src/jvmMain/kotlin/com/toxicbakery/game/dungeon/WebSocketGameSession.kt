package com.toxicbakery.game.dungeon

import co.touchlab.stately.concurrency.AtomicBoolean
import com.benasher44.uuid.uuid4
import com.toxicbakery.game.dungeon.client.ClientMessage.*
import com.toxicbakery.game.dungeon.client.ExpectedResponseType
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.model.session.PlayerSessionId
import io.ktor.http.cio.websocket.Frame
import io.ktor.websocket.WebSocketServerSession
import kotlinx.serialization.json.Json
import kotlinx.serialization.stringify

class WebSocketGameSession(
    override val sessionId: PlayerSessionId = PlayerSessionId(uuid4().toString()),
    private val webSocketServerSession: WebSocketServerSession
) : GameSession {

    private val _isClosed = AtomicBoolean(false)

    override val isClosed: Boolean
        get() = _isClosed.value

    override suspend fun send(
        msg: String,
        expectedResponseType: ExpectedResponseType
    ) {
        if (_isClosed.value) return
        val output = Json(jsonConfiguration)
            .stringify(ServerMessage(msg, expectedResponseType))
        webSocketServerSession.send(Frame.Text(output))
    }

    override suspend fun close() {
        webSocketServerSession.close()
        _isClosed.value = true
    }

}
