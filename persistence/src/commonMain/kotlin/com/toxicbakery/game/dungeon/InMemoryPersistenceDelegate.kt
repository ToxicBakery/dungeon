package com.toxicbakery.game.dungeon

import co.touchlab.stately.annotation.Throws
import co.touchlab.stately.concurrency.AtomicInt
import com.toxicbakery.game.dungeon.auth.Credentials
import com.toxicbakery.game.dungeon.auth.PlayerWithCredentials
import com.toxicbakery.game.dungeon.character.Player
import com.toxicbakery.game.dungeon.exception.AuthenticationException
import com.toxicbakery.game.dungeon.exception.NoPlayerWithIdException
import com.toxicbakery.game.dungeon.exception.NoPlayerWithUsernameException
import com.toxicbakery.game.dungeon.store.BroadcastChannelStore
import com.toxicbakery.game.dungeon.store.ChannelStore
import kotlinx.coroutines.flow.first

// FIXME Move the business logic out of the delegate and migrate to a real db
internal object InMemoryPersistenceDelegate : PersistenceDelegate {

    private val playerIdGenerator: AtomicInt = AtomicInt(0)
    private val playerMapStore: ChannelStore<Map<Int, PlayerWithCredentials>> = PlayerMapStore

    private val nextPlayerId: Int
        get() = playerIdGenerator.incrementAndGet()

    @Throws(NoPlayerWithUsernameException::class)
    private suspend fun getPlayerWithCredentialsByUsername(
        username: String
    ): PlayerWithCredentials = playerMapStore
        .observe()
        .first()
        .values
        .firstOrNull { playerWithCredentials -> playerWithCredentials.credentials.username == username }
        ?: throw NoPlayerWithUsernameException(username)

    @Throws(NoPlayerWithUsernameException::class, AuthenticationException::class)
    override suspend fun authenticatePlayer(
        credentials: Credentials
    ): Player {
        val playerWithCredentials = getPlayerWithCredentialsByUsername(credentials.username)
        if (playerWithCredentials.credentials != credentials) throw AuthenticationException()
        return playerWithCredentials.player
    }

    override suspend fun changeName(player: Player) {
        playerMapStore.modify { playerMap ->
            playerMap + (player.id to requireNotNull(playerMap[player.id]).copy(
                player = player
            ))
        }
    }

    override suspend fun createPlayer(
        credentials: Credentials
    ): Player = Player(id = nextPlayerId, name = credentials.username).also { player ->
        playerMapStore.modify { playerMap ->
            playerMap + (player.id to PlayerWithCredentials(
                player = player,
                credentials = credentials
            ))
        }
    }

    override suspend fun getPlayerById(
        id: Int
    ): Player = playerMapStore.observe()
        .first()[id]
        ?.player
        ?: throw NoPlayerWithIdException(id)

    private object PlayerMapStore : BroadcastChannelStore<Map<Int, PlayerWithCredentials>>(mapOf())

}
