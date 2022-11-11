package com.toxicbakery.game.dungeon.persistence.store

import com.toxicbakery.game.dungeon.machine.Machine
import com.toxicbakery.game.dungeon.storeDispatcher
import com.toxicbakery.logging.Arbor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext

/**
 * A channel implementation similar to that of Rx BehaviorSubject.
 */
abstract class BroadcastChannelStore<T>(initialValue: T) : ChannelStore<T> {

    private val _state = MutableStateFlow(initialValue)

    override suspend fun set(value: T) {
        _state.value = value
    }

    override suspend fun modify(func: suspend (T) -> T) {
        // Ignore the noOp store instance
        if (this === noOpStore) return

        withContext(storeDispatcher) {
            Arbor.d("Updating store %s", this@BroadcastChannelStore::class.simpleName)
            set(func(_state.value))
        }
    }

    override suspend fun observe(): Flow<T> = _state.asSharedFlow()

    override suspend fun value(): T = _state.value

    companion object {
        val noOpStore = object : BroadcastChannelStore<Map<String, Machine<*>>>(mapOf()) {}
    }
}