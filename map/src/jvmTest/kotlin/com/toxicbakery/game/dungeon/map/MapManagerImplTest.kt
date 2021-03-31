package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.map.model.Window
import com.toxicbakery.game.dungeon.model.character.Location
import org.junit.Assert.assertEquals
import org.junit.Test
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.mapdb.DB
import org.mapdb.DBMaker
import java.nio.file.Paths

class MapManagerImplTest : KodeinAware {

    private val mapManager: MapManager by instance()

    override val kodein: Kodein = Kodein {
        val dbPath = javaClass.classLoader
            .getResource("test-dungeon.db")
            ?.toURI()
            ?.let { Paths.get(it) }
            ?.toFile()
            ?.absolutePath
            ?: error("Failed to locate test data")

        bind<DB>(MAP_DB) with instance(
            DBMaker.fileDB(dbPath)
                .readOnly()
                .executorEnable()
                .fileMmapEnable()
                .make()
        )
        import(mapManagerModule)
    }

    @Test
    fun validateDbLoads() {
        assertEquals(8, mapManager.mapSize())
    }

    @Test
    fun validateFullMap() {
        val readMap = mapManager.drawCompleteMap().toAsciiDisplay()

        assertEquals(expectedAsciiMap, readMap)
    }

    @Test
    fun validateWindowZeroZero() {
        val readRegion = mapManager.drawWindow(
            WindowDescription(
                location = Location(
                    x = 0,
                    y = 0,
                    worldId = 0
                ),
                size = 1
            )
        ).toAsciiDisplay()

        assertEquals("~~~~", readRegion)
    }

    @Test
    fun validateWindowOneOneSizeThree() {
        val readRegion = mapManager.drawWindow(
            WindowDescription(
                location = Location(
                    x = 1,
                    y = 1,
                    worldId = 0
                ),
                size = 3
            )
        ).toAsciiDisplay()

        assertEquals(expectedRegionOneOneSizeThree, readRegion)
    }

    @Test
    fun validateWindowOneSixSizeThree() {
        val readRegion = mapManager.drawWindow(
            WindowDescription(
                location = Location(
                    x = 1,
                    y = 6,
                    worldId = 0
                ),
                size = 3
            )
        ).toAsciiDisplay()

        assertEquals(expectedRegionOneSixSizeThree, readRegion)
    }

    private fun Window.toAsciiDisplay(): String = windowRows
        .joinToString(separator = "\n") { row ->
            row.joinToString(separator = "") { b ->
                MapLegend.representingByte(b).ascii
            }
        }

    companion object {
        private val expectedAsciiMap = """
            ~~~~~~~~~~~~~~~~~~~~~~~~........~~~~................~~~~~~~~~~~~
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~....~~~~....................~~~~~~~~
            ~~~~~~~~~~~~~~~~~~~~....~~~~................~~~~............~~~~
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~.....^.^.^..............~~~~....~~~~
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~.........^..............~~~~~~~~~~~~
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~................~~~~~~~~~~~~~~~~~~~~
            ....~~~~~~~~~~~~~~~~~~~~~~~~~~~~....~~~~....~~~~~~~~............
            ....~~~~~~~~~~~~~~~~....~~~~~~~~~~~~~~~~............~~~~~~~~....
            ~~~~~~~~~~~~~~~~~~~~....~~~~~~~~~~~~~~~~........~~~~~~~~~~~~....
            ~~~~....~~~~~~~~~~~~....~~~~~~~~~~~~~~~~....~~~~~~~~~~~~........
            ~~~~~~~~....~~~~....~~~~~~~~~~~~~~~~~~~~~~~~....~~~~~~~~....~~~~
            ~~~~~~~~~~~~~~~~~~~~~~~~....................~~~~~~~~~~~~~~~~~~~~
            ~~~~~~~~~~~~~~~~~~~~~~~~..../\/\.^^^....~~~~~~~~~~~~~~~~....~~~~
            ....~~~~~~~~~~~~~~~~........................~~~~~~~~~~~~....~~~~
            ............~~~~~~~~~~~~....~~~~~~~~~~~~~~~~~~~~~~~~....~~~~....
            ~~~~~~~~....~~~~~~~~~~~~........~~~~~~~~~~~~........~~~~~~~~....
        """.trimIndent()

        private val expectedRegionOneOneSizeThree = """
            ............
            ........~~~~
            ............
        """.trimIndent()

        private val expectedRegionOneSixSizeThree = """
            ....~~~~~~~~
            ~~~~~~~~~~~~
            ~~~~~~~~~~~~
        """.trimIndent()
    }

}
