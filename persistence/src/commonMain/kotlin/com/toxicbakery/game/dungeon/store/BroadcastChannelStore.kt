package com.toxicbakery.game.dungeon.store

import com.toxicbakery.game.dungeon.storeDispatcher
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext

/**
 * A channel implementation similar to that of Rx BehaviorSubject.
 */
abstract class BroadcastChannelStore<T> : ChannelStore<T> {

    private val channel: ConflatedBroadcastChannel<T>

    /**
     * Create the behaviour without an initial value
     */
    constructor() {
        channel = ConflatedBroadcastChannel()
    }

    /**
     * Create the behavior with an initial value
     */
    constructor(initialValue: T) {
        channel = ConflatedBroadcastChannel(initialValue)
    }

    override suspend fun set(value: T) = channel.send(value)

    override suspend fun modify(func: suspend (T) -> T) {
        withContext(storeDispatcher) {
            set(func(channel.value))
        }
    }

    override suspend fun observe() = channel.asFlow()

}
