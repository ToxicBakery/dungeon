package com.toxicbakery.game.dungeon.map

data class Config(
    /**
     * Size of the square map. Must be a power of 2. Should be greater than 256
     */
    val size: Int = 8192,

    /**
     * Region size for loading the map from disk in chunks. Must be a power of 2 greater than 4 and less than map size.
     *
     * Larger regions will resulting in less disk space being used for the keys but will require more memory at runtime
     * for processing and rendering. Client window sizes are small, default size is less than 11 so a region should be
     * larger than the window size but also not be so large that it causes heap problems.
     */
    val regionSize: Int = 16,

    // Step
    val stepSize: Int = 16,
    // Noise
    val noiseWater: Double = -0.5,
    val noiseGround: Double = -1.5,
    val noiseMountain: Double = 0.5,
    // Ratios
    val ratioDesert: Int = 40,
    val ratioPlain: Int = 20,
    val ratioTree: Int = 40
)
