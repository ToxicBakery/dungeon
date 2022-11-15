package com.toxicbakery.game.dungeon.util

import java.text.SimpleDateFormat
import java.util.Date
import kotlin.time.Duration.Companion.seconds

private val formatYears = SimpleDateFormat("yyyy")
private val formatMonths = SimpleDateFormat("MMM")
private val formatDays = SimpleDateFormat("dd")

actual val Long.secondsToYears: String
    get() = formatYears.format(Date(seconds.inWholeMilliseconds))

actual val Long.secondsToMonths: String
    get() = formatMonths.format(Date(seconds.inWholeMilliseconds))

actual val Long.secondsToDays: String
    get() = formatDays.format(Date(seconds.inWholeMilliseconds))
