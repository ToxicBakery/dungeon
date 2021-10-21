package com.toxicbakery.game.dungeon.map

/**
 * Wrap a given value between zero inclusive and size exclusive. Values will be recursively wrapped until they are in
 * bounds.
 *
 * `0 <= value < size`
 *
 * @param size the maximum value, exclusive
 */
infix fun Int.wrapTo(
    size: Int
): Int {
    require(size > 0) { "Wrap size must be a positive value greater than 0." }
    return when {
        this < 0 -> (size + this).wrapTo(size)
        this >= size -> (this - size).wrapTo(size)
        else -> this
    }
}
