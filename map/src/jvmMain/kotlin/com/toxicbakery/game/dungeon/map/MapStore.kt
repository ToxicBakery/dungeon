package com.toxicbakery.game.dungeon.map

import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton
import org.mapdb.Atomic
import org.mapdb.DB
import org.mapdb.DBMaker
import org.mapdb.HTreeMap

private class MapStoreImpl(
    override val mapIsFinalized: Atomic.Boolean,
    override val mapSizeAtomic: Atomic.Integer,
    override val regionSizeAtomic: Atomic.Integer,
    override val map: HTreeMap<RegionLocation, Region>,
) : MapStore

interface MapStore {

    val mapIsFinalized: Atomic.Boolean

    val mapSizeAtomic: Atomic.Integer

    val regionSizeAtomic: Atomic.Integer

    val map: HTreeMap<RegionLocation, Region>

}

/**
 * Size of the table measuring the width (width == height, square map). Must be a power of 2.
 */
private const val TABLE_MAP_SIZE_REF = "table_map_size_ref"

/**
 * Size of regions in the map. Must be a power of 2.
 */
private const val TABLE_REGION_SIZE_REF = "table_region_size_ref"

/**
 * Determine if the map has been previously generated.
 */
private const val TABLE_MAP_IS_FINALIZED = "table_map_is_finalized"

/**
 * Path reference of the DB to be injected by Kodein as a constant.
 */
const val MAP_DB = "MAP_DB_"

private const val TABLE_MAP = "map_"
private const val TABLE_MAP_ROOT = "${TABLE_MAP}_0"

val mapStoreModule = Kodein.Module("mapStoreModule") {
    bind<MapStore>() with singleton {
        val db: DB = instance(MAP_DB)

        val tableSize = db.atomicInteger(TABLE_MAP_SIZE_REF)
            .createOrOpen()

        val regionSize = db.atomicInteger(TABLE_REGION_SIZE_REF)
            .createOrOpen()

        val mapIsFinalized = db.atomicBoolean(TABLE_MAP_IS_FINALIZED, false)
            .createOrOpen()

        val map = db.hashMap(
            name = TABLE_MAP_ROOT,
            keySerializer = RegionLocation.Serializer,
            valueSerializer = Region.Serializer
        ).createOrOpen()

        MapStoreImpl(
            mapIsFinalized = mapIsFinalized,
            mapSizeAtomic = tableSize,
            regionSizeAtomic = regionSize,
            map = map
        )
    }
}
