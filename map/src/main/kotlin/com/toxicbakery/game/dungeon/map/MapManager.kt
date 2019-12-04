package com.toxicbakery.game.dungeon.map

import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.singleton
import org.mapdb.Atomic
import org.mapdb.DB
import org.mapdb.DBMaker
import org.mapdb.HTreeMap

/**
 * Size of the table measuring the width (width == height, square map)
 */
private const val TABLE_SIZE_REF = "table_size_ref"
private const val TABLE_MAP = "map_"

/**id "com.github.gmazzo.buildconfig" version "1.6.1"
 * Root map (id = 0)
 */
private const val TABLE_MAP_ROOT = "${TABLE_MAP}_0"

class MapManager(
    private val tableSize: Atomic.Integer,
    private val map: HTreeMap<RegionLocation, Region>
) {

}

val mapManagerModule = Kodein.Module("mapManagerModule") {
    bind<MapManager>() with singleton {
        val oneHundredMB = 100L * 1024L * 1024L
        val db = DBMaker.fileDB("dungeon.db")
            .closeOnJvmShutdown()
            .executorEnable()
            .fileMmapEnable()
            .allocateStartSize(oneHundredMB)
            .make()

        val size = db.atomicInteger(TABLE_SIZE_REF)
            .createOrOpen()

        val map = db.hashMap(
            name = TABLE_MAP_ROOT,
            keySerializer = RegionLocation.Serializer,
            valueSerializer = Region.Serializer
        ).createOrOpen()

        MapManager(
            tableSize = size,
            map = map
        )
    }
}
