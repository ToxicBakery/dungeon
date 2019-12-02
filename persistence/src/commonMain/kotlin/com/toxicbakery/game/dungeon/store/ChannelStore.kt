package com.toxicbakery.game.dungeon.store

import kotlinx.coroutines.flow.Flow

/**
 * Store that allows for [set], [modify], and [observe] of an internal channel. This creates a contract similar to
 * that of Rx Subjects.
 */
interface ChannelStore<T> : Store<T> {

    /**
     * Listen to [set] and [modify] events on the channel.
     */
    suspend fun observe(): Flow<T>

}
