package com.toxicbakery.game.dungeon.client

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

    @Serializable
    data class ServerMessage(
        @SerialId(1)
        val message: String,
        @SerialId(2)
        val expectedResponseType: ExpectedResponseType = ExpectedResponseType.Normal
    ) : ClientMessage()

}

enum class ExpectedResponseType {
    Normal,
    Secure
}
