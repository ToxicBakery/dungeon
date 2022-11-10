package com.toxicbakery.game.dungeon.map

import kotlin.test.Test
import kotlin.test.assertEquals

class WrappingUtilKtTest {

    @Test
    fun wrapTo() {
        assertEquals(0, 0.wrapTo(1))
        assertEquals(0, 1.wrapTo(1))
        assertEquals(1, 1.wrapTo(2))
        assertEquals(0, 100.wrapTo(1))
        assertEquals(0, (-1).wrapTo(1))
        assertEquals(0, (-100).wrapTo(1))
    }
}
