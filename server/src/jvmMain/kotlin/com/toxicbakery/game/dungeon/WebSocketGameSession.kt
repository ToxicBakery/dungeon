package com.toxicbakery.game.dungeon

import co.touchlab.stately.concurrency.AtomicBoolean
import com.benasher44.uuid.uuid4
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.model.session.PlayerSessionId
import io.ktor.http.cio.websocket.Frame
import io.ktor.websocket.WebSocketServerSession

class WebSocketGameSession(
    override val sessionId: PlayerSessionId = PlayerSessionId(uuid4().toString()),
    private val webSocketServerSession: WebSocketServerSession
) : GameSession {

    private val _isClosed = AtomicBoolean(false)

    override val isClosed: Boolean
        get() = _isClosed.value

    override suspend fun send(
        msg: String,
        inputResponseType: GameSession.InputResponseType
    ) {
        if (_isClosed.value) return
        webSocketServerSession.send(Frame.Text(msg))
    }

    override suspend fun close() {
        webSocketServerSession.close()
        _isClosed.value = true
    }

}
