package com.toxicbakery.game.dungeon.model.session

import com.toxicbakery.game.dungeon.model.client.ClientMessage
import com.toxicbakery.game.dungeon.model.client.ExpectedResponseType

/**
 * Representation of a connection to a client.
 */
interface GameSession {

    /**
     * True if the session has been terminated.
     */
    val isClosed: Boolean

    /**
     * A unique identifier representing this session.
     */
    val sessionId: PlayerSessionId

    /**
     * Id of the player once authenticated.
     */
    val playerId: Int
        get() = NULL_PLAYER_ID

    /**
     * Write message back to the client. This is a helper for sending [ClientMessage.ServerMessage] instances.
     */
    suspend fun sendMessage(
        msg: String,
        expectedResponseType: ExpectedResponseType = ExpectedResponseType.Normal
    )

    /**
     * Write a client message out.
     */
    suspend fun sendClientMessage(clientMessage: ClientMessage)

    /**
     * Terminate the session.
     */
    suspend fun close()

    companion object {
        const val NULL_PLAYER_ID = -1
    }

}
