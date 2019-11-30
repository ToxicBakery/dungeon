package com.toxicbakery.game.dungeon.model.session

import com.toxicbakery.game.dungeon.client.ExpectedResponseType

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
     * Write data back to the client
     */
    suspend fun send(
        msg: String,
        expectedResponseType: ExpectedResponseType = ExpectedResponseType.Normal
    )

    /**
     * Terminate the session.
     */
    suspend fun close()

}
