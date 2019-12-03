package com.toxicbakery.game.dungeon.model.session

import com.toxicbakery.game.dungeon.model.client.ExpectedResponseType
import com.toxicbakery.game.dungeon.model.client.PlayerData

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
     * Write message back to the client
     */
    suspend fun sendMessage(
        msg: String,
        expectedResponseType: ExpectedResponseType = ExpectedResponseType.Normal
    )

    /**
     * Write the player data to the client
     */
    suspend fun sendPlayerData(playerData: PlayerData)

    /**
     * Terminate the session.
     */
    suspend fun close()

    companion object {
        const val NULL_PLAYER_ID = -1
    }

}
