package com.toxicbakery.game.dungeon.model.session

import com.toxicbakery.game.dungeon.client.ExpectedResponseType

class AuthenticatedGameSession(
    override val playerId: Int,
    private val gameSession: GameSession
) : GameSession {

    override val isClosed: Boolean
        get() = gameSession.isClosed

    override val sessionId: PlayerSessionId
        get() = gameSession.sessionId

    override suspend fun send(msg: String, expectedResponseType: ExpectedResponseType) =
        gameSession.send(msg, expectedResponseType)

    override suspend fun close() = gameSession.close()

}
