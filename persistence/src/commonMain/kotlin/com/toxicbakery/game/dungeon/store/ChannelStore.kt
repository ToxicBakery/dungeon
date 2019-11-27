package com.toxicbakery.game.dungeon.store

import kotlinx.coroutines.flow.Flow

/**
 * Store that allows for [set], [modify], and [observe] of an internal channel. This creates a contract similar to
 * that of Rx Subjects.
 */
interface ChannelStore<T> {

    /**
     * Set a value into the channel.
     */
    suspend fun set(value: T)

    /**
     * Attempt to get the latest value and return it into a mapping function to perform modification for submission
     * back into the channel.
     */
    suspend fun modify(func: suspend (T) -> T)

    /**
     * Listen to [set] and [modify] events on the channel.
     */
    suspend fun observe(): Flow<T>

}
