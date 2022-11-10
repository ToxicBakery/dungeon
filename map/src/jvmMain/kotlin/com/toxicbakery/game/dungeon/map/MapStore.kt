package com.toxicbakery.game.dungeon.map

import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.singleton
import org.mapdb.Atomic
import org.mapdb.DBMaker
import org.mapdb.HTreeMap
import org.mapdb.Serializer

private class MapStoreImpl(
    override val mapSizeAtomic: Atomic.Integer,
    override val map: HTreeMap<RegionLocation, Byte>
) : MapStore

interface MapStore {

    val mapSizeAtomic: Atomic.Integer

    val map: HTreeMap<RegionLocation, Byte>
}

/*
 * 100MB init DB size due to 8192 map with 16 region size results in a map around 85MB.
 */
private const val ONE_HUNDRED_MB = 100L * 1024L * 1024L
private const val TABLE_MAP_SIZE_REF = "table_map_size_ref"
private const val TABLE_MAP = "map_"
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

    val map = db.hashMap(
        name = TABLE_MAP_ROOT,
        keySerializer = RegionLocation.Serializer,
        valueSerializer = Serializer.BYTE
    ).createOrOpen()

    bind<MapStore>() with singleton {
        MapStoreImpl(
            mapSizeAtomic = tableSize,
            map = map
        )
    }
}
