package com.toxicbakery.game.dungeon

import com.benasher44.uuid.uuid4
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.model.session.GameSessionState
import com.toxicbakery.game.dungeon.model.session.PlayerSessionId
import io.ktor.http.cio.websocket.Frame
import io.ktor.websocket.WebSocketServerSession

class WebSocketGameSession(
    override val sessionId: PlayerSessionId = PlayerSessionId(uuid4().toString()),
    override val gameSessionState: GameSessionState = GameSessionState.Init,
    private val webSocketServerSession: WebSocketServerSession
) : GameSession {

    override suspend fun send(msg: String) {
        webSocketServerSession.send(Frame.Text(msg))
    }

    override suspend fun close() {
        webSocketServerSession.close()
    }

    override fun setGameSessionState(gameSessionState: GameSessionState): GameSession = WebSocketGameSession(
        sessionId = sessionId,
        gameSessionState = gameSessionState,
        webSocketServerSession = webSocketServerSession
    )

}
