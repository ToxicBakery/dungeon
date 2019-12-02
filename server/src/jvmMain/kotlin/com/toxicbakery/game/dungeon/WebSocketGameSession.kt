package com.toxicbakery.game.dungeon

import co.touchlab.stately.concurrency.AtomicBoolean
import com.benasher44.uuid.uuid4
import com.toxicbakery.game.dungeon.client.ClientMessage.PlayerDataMessage
import com.toxicbakery.game.dungeon.client.ClientMessage.ServerMessage
import com.toxicbakery.game.dungeon.client.ExpectedResponseType
import com.toxicbakery.game.dungeon.client.PlayerData
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.model.session.PlayerSessionId
import com.toxicbakery.game.dungeon.util.textFrame
import com.toxicbakery.logging.Arbor
import io.ktor.websocket.WebSocketServerSession
import kotlinx.serialization.dumps
import kotlinx.serialization.protobuf.ProtoBuf

class WebSocketGameSession(
    override val sessionId: PlayerSessionId = PlayerSessionId(uuid4().toString()),
    private val webSocketServerSession: WebSocketServerSession
) : GameSession {

    private val _isClosed = AtomicBoolean(false)
    private val protoBuf: ProtoBuf = ProtoBuf()

    override val isClosed: Boolean
        get() = _isClosed.value

    override suspend fun sendMessage(
        msg: String,
        expectedResponseType: ExpectedResponseType
    ) {
        if (_isClosed.value) return
        val output:String = protoBuf.dumps(ServerMessage(msg, expectedResponseType))
        Arbor.d("Sending message: %s", output)
        webSocketServerSession.send(textFrame(output))
    }

    override suspend fun sendPlayerData(playerData: PlayerData) {
        if (_isClosed.value) return
        val output: String = protoBuf.dumps(PlayerDataMessage(playerData))
        Arbor.d("Sending PlayerData: %s", output)
        webSocketServerSession.send(textFrame(output))
    }

    override suspend fun close() {
        webSocketServerSession.close()
        _isClosed.value = true
    }

}
