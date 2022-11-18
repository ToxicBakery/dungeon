@file:Suppress("MagicNumber")

package com.toxicbakery.game.dungeon.map

enum class MapLegend(
    val byteRepresentation: Byte,
    val ascii: String,
    val type: MapLegendType,
) {
    // Undefined space such as in initial map allocation
    // This also default to empty for space a character can not see
    NULL(0x00, "    ", MapLegendType.NULL),

    // Characters
    PLAYER(0x01, "<oo>", MapLegendType.LIVING),
    NPC(0x02, "<**>", MapLegendType.LIVING),

    // Terrain
    FOREST_1(0x10, ".^..", MapLegendType.LAND),
    FOREST_2(0x11, ".^.^", MapLegendType.LAND),
    FOREST_3(0x12, ".^^^", MapLegendType.LAND),
    FOREST_4(0x13, "^^^^", MapLegendType.LAND),
    OCEAN(0x14, "~~~~", MapLegendType.WATER),
    RIVER(0x15, "~~~~", MapLegendType.WATER),
    DESERT(0x16, "....", MapLegendType.LAND),
    PLAIN(0x17, "....", MapLegendType.LAND),
    BEACH(0x18, "....", MapLegendType.LAND),
    LAKE(0x19, "....", MapLegendType.WATER),
    MOUNTAIN(0x1C, """/\/\""", MapLegendType.LAND),

    // Animals
    ANIMAL_AGGRESSIVE(0x50, ".><.", MapLegendType.LIVING),
    ANIMAL_PASSIVE(0x51, ".<>.", MapLegendType.LIVING),

    // Creatures
    CREATURE(0x60, ">&&<", MapLegendType.LIVING),

    /**
     * A displayable lacking correlation to a [MapLegend].
     */
    WTF(0xFF.toByte(), "!!!!", MapLegendType.NULL);

    val htmlRepresentation: String
        get() = "<span class=\"_${byteRepresentation}\">$htmlSafeAscii</span>"

    val htmlSafeAscii: String = ascii
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace(" ", "&nbsp;")

    companion object {

        private val legendMap: Map<Byte, MapLegend> = values()
            .associateBy { legend -> legend.byteRepresentation }

        fun representingByte(b: Byte): MapLegend = legendMap.getOrElse(b) { NULL }
    }
}
