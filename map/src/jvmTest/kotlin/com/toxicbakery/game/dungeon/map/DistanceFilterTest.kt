package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.model.character.Location
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("MagicNumber")
class DistanceFilterTest {

    @Test
    fun nearby() {
        val distanceFilter = DistanceFilter(8, 1)
        assertTrue(distanceFilter.nearby(location(0, 0), location(0, 0)))
        assertTrue(distanceFilter.nearby(location(0, 0), location(1, 1)))
        assertFalse(distanceFilter.nearby(location(0, 0), location(2, 2)))
        assertTrue(distanceFilter.nearby(location(0, 0), location(7, 0)))
        assertTrue(distanceFilter.nearby(location(0, 0), location(7, 7)))
        assertTrue(distanceFilter.nearby(location(0, 0), location(0, 7)))
        assertFalse(distanceFilter.nearby(location(0, 0), location(5, 5)))
    }

    @Test
    fun nearby_differentWorlds() {
        assertFalse(
            DistanceFilter(8, 1).nearby(
                Location(0, 0, 0),
                Location(0, 0, 1)
            )
        )
    }

    private fun location(x: Int, y: Int) = Location(x, y, 0)

}
