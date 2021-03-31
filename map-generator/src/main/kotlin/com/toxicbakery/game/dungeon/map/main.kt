package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.map.BuildConfig.MAP_SIZE
import com.toxicbakery.game.dungeon.map.BuildConfig.REGION_SIZE
import com.toxicbakery.game.dungeon.map.preview.BmpMapPreviewer
import com.toxicbakery.game.dungeon.map.preview.HtmlMapPreviewer
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.with
import org.mapdb.DB
import org.mapdb.DBMaker
import kotlin.time.ExperimentalTime

private val applicationKodein = Kodein {
    bind<DB>(MAP_DB) with instance(
        DBMaker.fileDB("dungeon.db")
            .closeOnJvmShutdown()
            .executorEnable()
            .fileMmapEnable()
            .make()
    )
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
            regionSize = REGION_SIZE
        ),
        previewers = listOf(
            BmpMapPreviewer(),
            HtmlMapPreviewer()
        )
    )
}
