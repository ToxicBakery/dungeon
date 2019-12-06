package com.toxicbakery.game.dungeon.map

// Undefined map value
const val MAP_KEY_NULL: Byte = 0x0


const val MAP_KEY_PLAYER: Byte = 0x1
const val MAP_KEY_NPC: Byte = 0x2

// Terrain
const val MAP_KEY_FOREST = 0x10

@Suppress("MagicNumber")
enum class MapLegend(
    val byteRepresentation: Byte
) {

    // Undefined space such as in initial map allocation
    NULL(0x00),

    // Characters
    PLAYER(0x01),
    NPC(0x02),

    // Terrain
    FOREST_1(0x10),
    FOREST_2(0x11),
    FOREST_3(0x12),
    FOREST_4(0x13),
    OCEAN(0x14),
    RIVER(0x15),
    DESERT(0x16),
    PLAIN(0x17),
    BEACH_N(0x18),
    BEACH_S(0x19),
    BEACH_W(0x1A),
    BEACH_E(0x1B),
    MOUNTAIN(0x1C),


    // Animals
    ANIMAL_AGGRESSIVE(0x50),
    ANIMAL_PASSIVE(0x51),

    // Creatures
    CREATURE(0x60);

    companion object {

        // Byte lookup map
        val lookupMap: Map<Byte, MapLegend> = values()
            .map { legend -> legend.byteRepresentation to legend }
            .toMap()

    }

}
