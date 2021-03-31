@file:Suppress("MagicNumber")

package com.toxicbakery.game.dungeon.map

enum class MapLegend(
    val byteRepresentation: Byte,
    val ascii: String
) {

    // Undefined space such as in initial map allocation
    NULL(0x00, "    "),

    // Characters
    PLAYER(0x01, "<oo>"),
    NPC(0x02, "<**>"),

    // Terrain
    FOREST_1(0x10, ".^.."),
    FOREST_2(0x11, ".^.^"),
    FOREST_3(0x12, ".^^^"),
    FOREST_4(0x13, "^^^^"),
    OCEAN(0x14, "~~~~"),
    RIVER(0x15, "~~~~"),
    DESERT(0x16, "...."),
    PLAIN(0x17, "...."),
    BEACH(0x18, "...."),
    LAKE(0x19, "...."),
    MOUNTAIN(0x1C, """/\/\"""),


    // Animals
    ANIMAL_AGGRESSIVE(0x50, ".><."),
    ANIMAL_PASSIVE(0x51, ".<>."),

    // Creatures
    CREATURE(0x60, ">&&<"),

    /**
     * A displayable lacking correlation to a [MapLegend].
     */
    WTF(0xFF.toByte(), "!!!!");

    val htmlRepresentation: String
        get() = "<span class=\"_${byteRepresentation}\">${htmlSafeAscii}</span>"

    val htmlSafeAscii: String = ascii
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")

    init {
        // All map legend elements should be 4 characters in length
        require(ascii.length == 4)
    }

    companion object {

        private val legendMap: Map<Byte, MapLegend> = values()
            .map { legend -> legend.byteRepresentation to legend }
            .toMap()

        fun representingByte(b: Byte): MapLegend = legendMap.getOrElse(b, { NULL })

    }

}
