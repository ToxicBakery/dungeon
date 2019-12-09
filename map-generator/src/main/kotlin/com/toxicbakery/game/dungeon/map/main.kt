package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.map.model.Window
import com.toxicbakery.game.dungeon.map.preview.BmpMapPreviewer
import org.kodein.di.Kodein
import org.kodein.di.erased.instance
import kotlin.time.ExperimentalTime

private val applicationKodein = Kodein {
    import(mapManagerModule)
    import(mapGeneratorModule)
}

@ExperimentalTime
@Suppress("MagicNumber")
fun main() {
    val mapGenerator: MapGenerator by applicationKodein.instance()

    // Create a map
    mapGenerator.generateMap(
        mapConfig = MapGenerator.MapConfig(
            mapSize = 1024,
            regionSize = 4
        ),
        previewers = listOf(
            BmpMapPreviewer()
        )
    )
}

private fun Window.render() = windowRows
    .also { println("Window") }
    .forEach { row ->
        row.forEach { b -> print(MapLegend.representingByte(b).ascii) }
        println()
    }
