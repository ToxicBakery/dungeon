package com.toxicbakery.game.dungeon.model.client

import com.toxicbakery.game.dungeon.map.model.Window
import com.toxicbakery.game.dungeon.model.world.LookLocation
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

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
        @ProtoNumber(1)
        val message: String
    ) : ClientMessage()

    /**
     * Message sent by the server that may expect a response.
     */
    @Serializable
    data class ServerMessage(
        @ProtoNumber(1)
        val message: String,
        @ProtoNumber(2)
        val expectedResponseType: ExpectedResponseType = ExpectedResponseType.Normal
    ) : ClientMessage()

    /**
     * Message sent by the server conveying the current player information.
     */
    @Serializable
    data class PlayerDataMessage(
        @ProtoNumber(1)
        val playerData: PlayerData
    ) : ClientMessage()

    @Serializable
    data class MapMessage(
        @ProtoNumber(1)
        val window: Window
    ) : ClientMessage()

    @Serializable
    data class DirectedLookMessage(
        @ProtoNumber(1)
        val lookLocation: LookLocation
    ) : ClientMessage()
}
