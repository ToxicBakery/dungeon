package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.model.character.Location
import org.kodein.di.Kodein
import org.kodein.di.erased.instance

private val applicationKodein = Kodein {
    import(mapManagerModule)
}

@Suppress("MagicNumber")
fun main() {
    val mapManager: MapManager by applicationKodein.instance()
    mapManager.mapSize = 16384
    mapManager.regionSize = 32
    mapManager.generateMap()
    println(
        mapManager.drawWindow(
            Window(
                location = Location(
                    x = 0,
                    y = 0
                ),
                size = 5
            )
        )
    )
}
