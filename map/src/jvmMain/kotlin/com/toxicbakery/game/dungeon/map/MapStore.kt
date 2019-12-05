package com.toxicbakery.game.dungeon.map

import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.singleton
import org.mapdb.Atomic
import org.mapdb.DBMaker
import org.mapdb.HTreeMap

private class MapStoreImpl(
    override val mapSizeAtomic: Atomic.Integer,
    override val regionSizeAtomic: Atomic.Integer,
    override val map: HTreeMap<RegionLocation, Region>
) : MapStore

interface MapStore {

    val mapSizeAtomic: Atomic.Integer

    val regionSizeAtomic: Atomic.Integer

    val map: HTreeMap<RegionLocation, Region>

}


/**
 * 100MB init DB size due to 8192 map with 16 region size results in a map around 85MB.
 */
private const val ONE_HUNDRED_MB = 100L * 1024L * 1024L
/**
 * Size of the table measuring the width (width == height, square map). Must be a power of 2.
 */
private const val TABLE_MAP_SIZE_REF = "table_map_size_ref"
/**
 * Size of regions in the map. Must be a power of 2.
 */
private const val TABLE_REGION_SIZE_REF = "table_region_size_ref"
private const val TABLE_MAP = "map_"
/**id "com.github.gmazzo.buildconfig" version "1.6.1"
 * Root map (id = 0)
 */
private const val TABLE_MAP_ROOT = "${TABLE_MAP}_0"

val mapStoreModule = Kodein.Module("mapStoreModule") {
    val db = DBMaker.fileDB("dungeon.db")
        .closeOnJvmShutdown()
        .executorEnable()
        .fileMmapEnable()
        .allocateStartSize(ONE_HUNDRED_MB)
        .make()

    val tableSize = db.atomicInteger(TABLE_MAP_SIZE_REF)
        .createOrOpen()

    val regionSize = db.atomicInteger(TABLE_REGION_SIZE_REF)
        .createOrOpen()

    val map = db.hashMap(
        name = TABLE_MAP_ROOT,
        keySerializer = RegionLocation.Serializer,
        valueSerializer = Region.Serializer
    ).createOrOpen()

    bind<MapStore>() with singleton {
        MapStoreImpl(
            mapSizeAtomic = tableSize,
            regionSizeAtomic = regionSize,
            map = map
        )
    }
}
