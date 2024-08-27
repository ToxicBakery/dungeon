package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.map.BuildConfig.MAP_SIZE
import com.toxicbakery.game.dungeon.map.preview.BmpMapPreviewer
import com.toxicbakery.game.dungeon.map.preview.HtmlMapPreviewer
import kotlin.time.ExperimentalTime
import org.kodein.di.DI
import org.kodein.di.instance

private val applicationKodein = DI {
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
            mapSize = MAP_SIZE,
        ),
        previewers = listOf(
            BmpMapPreviewer(),
            HtmlMapPreviewer()
        )
    )
}
