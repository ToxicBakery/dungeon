package com.toxicbakery.game.dungeon.model.session

import com.toxicbakery.game.dungeon.model.client.ClientMessage
import com.toxicbakery.game.dungeon.model.client.ExpectedResponseType

data class AuthenticatedGameSession(
    override val playerId: String,
    private val gameSession: GameSession
) : GameSession {

    override val isClosed: Boolean
        get() = gameSession.isClosed

    override val sessionId: String
        get() = gameSession.sessionId

    override suspend fun sendMessage(msg: String, expectedResponseType: ExpectedResponseType) =
        gameSession.sendMessage(msg, expectedResponseType)

    override suspend fun sendClientMessage(clientMessage: ClientMessage) =
        gameSession.sendClientMessage(clientMessage)

    override suspend fun close() = gameSession.close()
}
