package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.map.model.Window
import com.toxicbakery.game.dungeon.model.world.Location
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

private class MapManagerImpl(
    private val mapStore: MapStore
) : MapBaseFunctionality(
    mapSizeAtomic = mapStore.mapSizeAtomic,
), MapManager {

    override fun mapSize(): Int = mapSize

    override fun drawWindow(
        windowDescription: WindowDescription,
        mapOverlay: (MapOverlay) -> Unit
    ): Window {
        windowDescription.validate()
        val rows = List(windowDescription.size) { ByteArray(windowDescription.size) }
        val topLeftLocation = windowDescription.getTopLeftLocation(mapSize)
        for (y in 0 until windowDescription.size) {
            for (x in 0 until windowDescription.size) {
                val regionLocation = RegionLocation(
                    x = (topLeftLocation.x + x) wrapTo mapSize,
                    y = (topLeftLocation.y + y) wrapTo mapSize,
                )
                rows[y][x] = mapStore.map[regionLocation] ?: MapLegend.NULL.byteRepresentation
            }
        }

        mapOverlay(
            MapDrawExtension(
                mapSize = mapSize,
                windowDescription = windowDescription,
                rows = rows
            )
        )

        return Window(
            windowRows = rows
        )
    }

    override fun drawLocation(windowDescription: WindowDescription): Byte {
        windowDescription.validate()
        val regionLocation = RegionLocation(windowDescription.location.x, windowDescription.location.y)
        return mapStore.map[regionLocation] ?: MapLegend.NULL.byteRepresentation
    }

    override fun drawCompleteMap(): Window {
        val rows = List(mapSize) { ByteArray(mapSize) }
        mapStore.map.forEach { (regionLocation, byte) ->
            rows[regionLocation.y][regionLocation.x] = byte
        }
        return Window(rows)
    }

    private fun WindowDescription.validate() {
        if (size > mapSize) error("Request $size exceeds world dimension $mapSize")
        else if (size and 1 == 0) error("Request $size must be odd")
    }

    private class MapDrawExtension(
        private val mapSize: Int,
        windowDescription: WindowDescription,
        private val rows: List<ByteArray>,
    ) : MapOverlay {

        private val topLeftLocation = windowDescription.getTopLeftLocation(mapSize)
        private val boundingBox = BoundingBox(
            leftLocation = topLeftLocation,
            rightLocation = windowDescription.getBottomRightLocation(mapSize),
            mapSize = mapSize,
        )

        override fun addOverlayItem(
            location: Location,
            mapLegend: MapLegend
        ) {
            // Evict overlays that are out of bounds
            if (!boundingBox.isBound(location)) return
            val x = (location.x - topLeftLocation.x) wrapTo mapSize
            val y = (location.y - topLeftLocation.y) wrapTo mapSize
            rows[y][x] = mapLegend.byteRepresentation
        }
    }
}

actual val mapManagerModule = DI.Module("mapManagerModule") {
    import(mapStoreModule)
    bind<MapManager>() with singleton {
        MapManagerImpl(
            mapStore = instance()
        )
    }
}
