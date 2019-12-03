package com.toxicbakery.game.dungeon.persistence.store

import com.toxicbakery.game.dungeon.storeDispatcher
import com.toxicbakery.logging.Arbor
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext

/**
 * A channel implementation similar to that of Rx BehaviorSubject.
 */
abstract class BroadcastChannelStore<T>(initialValue: T) : ChannelStore<T> {

    private val channel: ConflatedBroadcastChannel<T> = ConflatedBroadcastChannel(initialValue)

    override suspend fun set(value: T) = channel.send(value)

    override suspend fun modify(func: suspend (T) -> T) {
        withContext(storeDispatcher) {
            Arbor.d("Updating store %s", this@BroadcastChannelStore::class.simpleName)
            set(func(channel.value))
        }
    }

    override suspend fun observe() = channel.asFlow()

    override suspend fun value(): T = channel.value

}
