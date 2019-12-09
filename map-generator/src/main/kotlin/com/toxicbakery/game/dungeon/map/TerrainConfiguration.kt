package com.toxicbakery.game.dungeon.map

class TerrainConfiguration(
    val mapSize: Int = 128,
    desertFrequency: Float = 0.2f,
    plainFrequency: Float = 0.75f,
    oceanFrequency: Float = 0.4f,
    val desertPasses:Int = 3,
    val forestPasses: Int = 10
) {
    val desertMinimumHeight: Float = 1f - desertFrequency
    val plainMaxHeight: Float = plainFrequency
    val oceanMaxHeight: Float = oceanFrequency
}
