package com.toxicbakery.game.dungeon.persistence.player

import com.toxicbakery.game.dungeon.exception.AlreadyRegisteredException
import com.toxicbakery.game.dungeon.exception.AuthenticationException
import com.toxicbakery.game.dungeon.exception.NoPlayerWithIdException
import com.toxicbakery.game.dungeon.exception.NoPlayerWithUsernameException
import com.toxicbakery.game.dungeon.map.DistanceFilter
import com.toxicbakery.game.dungeon.model.Lookable.Player
import com.toxicbakery.game.dungeon.model.auth.Credentials
import com.toxicbakery.game.dungeon.model.auth.PlayerWithCredentials
import com.toxicbakery.game.dungeon.model.world.Location
import com.toxicbakery.game.dungeon.persistence.store.BroadcastChannelStore
import com.toxicbakery.game.dungeon.persistence.store.ChannelStore

// FIXME Move the business logic out of the delegate and migrate to a real db
internal object InMemoryPersistencePlayerDatabaseDelegate : PersistencePlayerDatabaseDelegate {

    private val playerMapStore: ChannelStore<Map<String, PlayerWithCredentials>> = PlayerMapStore

    private fun Map<String, PlayerWithCredentials>.getPlayerWithUsername(username: String): Player =
        values.first { playerWithCredentials ->
            playerWithCredentials.credentials.username == username
        }.player

    private suspend fun getPlayerWithCredentialsByUsername(username: String): PlayerWithCredentials =
        playerMapStore.value()
            .values
            .firstOrNull { playerWithCredentials -> playerWithCredentials.credentials.username == username }
            ?: throw NoPlayerWithUsernameException(username)

    override suspend fun authenticatePlayer(credentials: Credentials): Player {
        val playerWithCredentials = getPlayerWithCredentialsByUsername(credentials.username)
        if (playerWithCredentials.credentials != credentials) throw AuthenticationException()
        return playerWithCredentials.player
    }

    override suspend fun updatePlayer(player: Player) {
        playerMapStore.modify { playerMap ->
            playerMap + (player.id to requireNotNull(playerMap[player.id]).copy(player = player))
        }
    }

    override suspend fun createPlayer(credentials: Credentials): Player {
        playerMapStore.modify { playerMap ->
            try {
                playerMap.getPlayerWithUsername(credentials.username)
                throw AlreadyRegisteredException()
            } catch (_: NoSuchElementException) {
                val player = Player(name = credentials.username)
                // Player doesn't exist, they may be added to the DB
                playerMap + (player.id to PlayerWithCredentials(player = player, credentials = credentials))
            }
        }

        return playerMapStore.value().getPlayerWithUsername(credentials.username)
    }

    override suspend fun getPlayerById(id: String): Player =
        playerMapStore.value()[id]?.player ?: throw NoPlayerWithIdException(id)

    // TODO Create a distance based filter
    override suspend fun getPlayersNear(
        location: Location,
        distanceFilter: DistanceFilter
    ): List<Player> = playerMapStore.value()
        .values
        .map(PlayerWithCredentials::player)
        .filter { player -> distanceFilter.nearby(location, player.location) }

    private object PlayerMapStore : BroadcastChannelStore<Map<String, PlayerWithCredentials>>(mapOf())
}
