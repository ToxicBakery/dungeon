package com.toxicbakery.game.dungeon.persistence.store

import com.toxicbakery.game.dungeon.tickDispatcher
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.singleton

@OptIn(ExperimentalCoroutinesApi::class)
class GameClock(
    private val gameStartYearEpochSeconds: Long
) {

    private val offset = now.epochSeconds

    val gameSeconds: Long
        get() = gameStartYearEpochSeconds + (now.epochSeconds - offset)

    val gameTickFlow: Flow<Long> = CoroutineScope(tickDispatcher)
        .observeGameTicks()
        .consumeAsFlow()

    private fun CoroutineScope.observeGameTicks(): ReceiveChannel<Long> = produce {
        while (true) {
            delay(TICK_RATE)
            send(gameSeconds)
        }
    }

    companion object {
        private const val TICK_RATE: Long = 100L
    }
}

private const val YEAR_1200 = -24298873902
private const val YEAR_1400 = -17987440302
private val gameStartYearEpochSeconds = Random.nextLong(YEAR_1200, YEAR_1400)
private val now: Instant
    get() = Clock.System.now()

val gameClockModule = Kodein.Module("gameClockModule") {
    bind<GameClock>() with singleton {
        GameClock(
            gameStartYearEpochSeconds = gameStartYearEpochSeconds
        )
    }
}
