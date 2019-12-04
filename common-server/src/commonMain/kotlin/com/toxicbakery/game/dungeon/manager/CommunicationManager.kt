package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.model.character.Player
import com.toxicbakery.game.dungeon.model.session.PlayerSession
import com.toxicbakery.game.dungeon.persistence.store.DungeonStateStore
import kotlinx.coroutines.flow.first
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private class CommunicationManagerImpl(
    private val dungeonStateStore: DungeonStateStore
) : CommunicationManager {

    private suspend fun playerSessionList(): List<PlayerSession> =
        dungeonStateStore.observe()
            .first()
            .playerSessionsList

    override suspend fun say(
        player: Player,
        message: String
    ) = playerSessionList()
        .filter { playerSession: PlayerSession -> playerSession.player.location == player.location }
        .forEach { session: PlayerSession -> session.sendMessage("[SAY] ${player.name}: $message") }

    override suspend fun gsay(
        player: Player,
        message: String
    ) = playerSessionList()
        .forEach { session: PlayerSession -> session.sendMessage("[GSAY] ${player.name}: $message") }

    override suspend fun who(): List<Player> = playerSessionList().map(PlayerSession::player)

    private suspend fun PlayerSession.sendMessage(message: String) = session.sendMessage(message)

}

interface CommunicationManager {

    /**
     * Send a message to a player in the room.
     */
    suspend fun say(
        player: Player,
        message: String
    )

    /**
     * Send a global message.
     */
    suspend fun gsay(
        player: Player,
        message: String
    )

    /**
     * Get all connected and authenticated players.
     */
    suspend fun who(): List<Player>

}

val communicationManagerModule = Kodein.Module("communicationManagerModule") {
    bind<CommunicationManager>() with provider {
        CommunicationManagerImpl(
            dungeonStateStore = instance()
        )
    }
}
