package com.toxicbakery.game.dungeon.persistence.store

import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.singleton

@OptIn(ExperimentalCoroutinesApi::class)
class GameClock(
    startDay: Int,
    private val clockMark: Instant
) {

    private val startDayInSeconds: Long = startDay * SECONDS_PER_DAY

    val gameSeconds: Long
        get() = startDayInSeconds + clockMark.epochSeconds

    fun CoroutineScope.observeGameTicks() = produce<Long> {
        while (true) {
            delay(TICK_RATE)
            gameSeconds
        }
    }

    companion object {
        private const val SECONDS_PER_DAY: Long = 86_400L
        private const val TICK_RATE: Long = 100L
    }
}

// Start the game clock sometime between the years 500 and 700 + up to slightly less than one year of days
private const val MIN_YEAR = 500
private const val MAX_YEAR = 700
private const val DAYS_PER_YEAR = 365
private val targetYear = Random.nextInt(MIN_YEAR, MAX_YEAR)
private val targetDay = Random.nextInt(0, DAYS_PER_YEAR - 1)
private val gameDaysStart = targetYear * DAYS_PER_YEAR + targetDay
private val now: Instant
    get() = Clock.System.now()

val gameClockModule = Kodein.Module("gameClockModule") {
    bind<GameClock>() with singleton {
        GameClock(
            startDay = gameDaysStart,
            clockMark = now
        )
    }
}
