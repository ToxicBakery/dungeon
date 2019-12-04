package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.model.client.PlayerData
import com.toxicbakery.game.dungeon.model.session.GameSession
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

/**
 * Implementation that combines the [PlayerManager] and [WorldManager] to produce [PlayerData].
 */
private class PlayerDataManagerImpl(
    private val playerManager: PlayerManager,
    private val worldManager: WorldManager
) : PlayerDataManager {

    override suspend fun getPlayerData(
        gameSession: GameSession
    ): PlayerData = playerManager
        .getPlayerByGameSession(gameSession)
        .let { player ->
            val world = worldManager.getWorldById(player.location.worldId)
            PlayerData(
                worldName = world.name,
                location = player.location,
                maxHealth = player.stats.health,
                stats = player.getComputedStats()
            )
        }

}

/**
 * Manager for generating player data used for sending to the client for display.
 */
interface PlayerDataManager {

    /**
     * Compute the current player data.
     */
    suspend fun getPlayerData(
        gameSession: GameSession
    ): PlayerData

}

val playerDataManagerModule = Kodein.Module("playerDataManagerModule") {
    bind<PlayerDataManager>() with provider {
        PlayerDataManagerImpl(
            playerManager = instance(),
            worldManager = instance()
        )
    }
}
