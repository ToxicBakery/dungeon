package com.toxicbakery.game.dungeon.model.client

import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable

/**
 * Message base type. Client messages represent data transfers between the client and server.
 */
@Serializable
sealed class ClientMessage {

    /**
     * Message sent by the user.
     */
    @Serializable
    data class UserMessage(
        @SerialId(1)
        val message: String
    ) : ClientMessage()

    /**
     * Message sent by the server that may expect a response.
     */
    @Serializable
    data class ServerMessage(
        @SerialId(1)
        val message: String,
        @SerialId(2)
        val expectedResponseType: ExpectedResponseType = ExpectedResponseType.Normal
    ) : ClientMessage()

    /**
     * Message sent by the server conveying the current player information.
     */
    @Serializable
    data class PlayerDataMessage(
        @SerialId(1)
        val playerData: PlayerData
    ) : ClientMessage()

}
