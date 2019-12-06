package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.model.character.Location
import org.kodein.di.Kodein
import org.kodein.di.erased.instance

private val applicationKodein = Kodein {
    import(mapManagerModule)
    import(mapGeneratorModule)
}

@Suppress("MagicNumber")
fun main() {
    val mapManager: MapManager by applicationKodein.instance()
    val mapGenerator: MapGenerator by applicationKodein.instance()

    // Create a map
    mapGenerator.generateMap(
        mapConfig = MapGenerator.MapConfig(
            mapSize = 8192,
            regionSize = 16
        )
    )

    // Preview the home position
    println(
        mapManager.drawWindow(
            WindowDescription(
                location = Location(
                    x = 0,
                    y = 0
                ),
                size = 11
            )
        ).windowRows.joinToString("\n", transform = { windowRow ->
            windowRow.row.joinToString("")
        })
    )
}
