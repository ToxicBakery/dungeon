package com.toxicbakery.game.dungeon

import co.touchlab.stately.concurrency.AtomicInt
import com.toxicbakery.game.dungeon.auth.Credentials
import com.toxicbakery.game.dungeon.auth.PlayerWithCredentials
import com.toxicbakery.game.dungeon.exception.AuthenticationException
import com.toxicbakery.game.dungeon.exception.NoPlayerWithIdException

// FIXME Move the business logic out of the delegate and migrate to a real db
internal object InMemoryPersistenceDelegate : PersistenceDelegate {

    private val playerIdGenerator: AtomicInt = AtomicInt(0)
    private val playerMap: MutableMap<String, PlayerWithCredentials> = hashMapOf()

    private val nextPlayerId: Int
        get() = playerIdGenerator.incrementAndGet()

    override fun authenticatePlayer(
        id: String,
        credentials: Credentials
    ): Player {
        val playerWithCredentials = playerMap[id]
        if (playerWithCredentials == null) throw NoPlayerWithIdException(id)
        else if (playerWithCredentials.credentials != credentials) throw AuthenticationException()
        return playerWithCredentials.player
    }

    override fun createPlayer(
        player: Player,
        credentials: Credentials
    ): Player = player.copy(id = nextPlayerId)

    override fun getPlayerById(
        id: String
    ): Player = playerMap[id]?.player ?: throw NoPlayerWithIdException(id)

}
