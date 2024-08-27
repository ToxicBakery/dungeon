package com.toxicbakery.game.dungeon.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private val months = arrayOf(
    "Jan",
    "Feb",
    "Mar",
    "Apr",
    "May",
    "Jun",
    "Jul",
    "Aug",
    "Sep",
    "Nov",
    "Dec",
)

actual val Long.secondsToYears: String
    get() = Instant.fromEpochSeconds(this)
        .toLocalDateTime(TimeZone.UTC)
        .year
        .toString()

actual val Long.secondsToMonths: String
    get() = Instant.fromEpochSeconds(this)
        .toLocalDateTime(TimeZone.UTC)
        .monthNumber
        .let { months[it] }

actual val Long.secondsToDays: String
    get() = Instant.fromEpochSeconds(this)
        .toLocalDateTime(TimeZone.UTC)
        .dayOfMonth.let { dayInt ->
            dayInt.toString().let { dayString ->
                if (dayString.length == 1) "0$dayString" else dayString
            }
        }
