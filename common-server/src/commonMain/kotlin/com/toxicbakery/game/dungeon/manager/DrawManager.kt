package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.map.MapLegend
import com.toxicbakery.game.dungeon.map.MapManager
import com.toxicbakery.game.dungeon.map.WindowDescription
import com.toxicbakery.game.dungeon.map.model.Window
import com.toxicbakery.game.dungeon.model.ILookable
import com.toxicbakery.game.dungeon.model.Lookable
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.model.world.Location
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

private class DrawManagerImpl(
    private val mapManager: MapManager,
    private val nearbyManager: NearbyManager,
    private val playerManager: PlayerManager,
) : DrawManager {

    override suspend fun getWindow(gameSession: GameSession): Window =
        getWindow(playerManager.getPlayerByGameSession(gameSession))

    private suspend fun getWindow(player: Lookable.Player): Window {
        val windowDescription = windowDescriptionFor(player)
        val nearbyThings = nearbyManager.getNearbyLookables(
            target = player.location,
            distance = windowDescription.size / 2 + 1
        ).locationMapped()
        return mapManager.drawWindow(windowDescription) { mapOverlay ->
            nearbyThings.forEach { (location, displayable) ->
                mapOverlay.addOverlayItem(
                    location = location,
                    mapLegend = displayable.toMapLegend
                )
            }
        }
    }

    private fun List<ILookable>.locationMapped(): Map<Location, ILookable> =
        associate { displayable -> displayable.location to displayable }

    companion object {
        private const val WINDOW_SIZE = 9

        private val ILookable.toMapLegend: MapLegend
            get() = when (this) {
                is Lookable.Player -> MapLegend.PLAYER
                is Lookable.NpcCharacter -> MapLegend.NPC
                is Lookable.Animal -> if (isPassive) MapLegend.ANIMAL_PASSIVE else MapLegend.ANIMAL_AGGRESSIVE
                is Lookable.Creature -> MapLegend.CREATURE
                else -> MapLegend.WTF
            }

        private fun windowDescriptionFor(player: Lookable.Player) = WindowDescription(
            location = player.location,
            size = WINDOW_SIZE
        )
    }
}

interface DrawManager {
    suspend fun getWindow(gameSession: GameSession): Window
}

val drawManagerModule = DI.Module("drawManagerModule") {
    bind<DrawManager>() with singleton {
        DrawManagerImpl(
            mapManager = instance(),
            nearbyManager = instance(),
            playerManager = instance(),
        )
    }
}
