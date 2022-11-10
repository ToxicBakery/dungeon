package com.toxicbakery.game.dungeon.persistence.store

/**
 * Basic store allowing for values to be manipulated with [set] and [modify].
 */
interface Store<T> {

    /**
     * Set a value into the channel.
     */
    suspend fun set(value: T)

    /**
     * Get the latest set value and return a new instance.
     */
    suspend fun modify(func: suspend (T) -> T)

    /**
     * Get the latest set value.
     */
    suspend fun value(): T
}
