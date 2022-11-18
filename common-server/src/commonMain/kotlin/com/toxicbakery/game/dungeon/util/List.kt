package com.toxicbakery.game.dungeon.util

import kotlin.random.Random

fun <T> List<T>.getRandom(): T? = when {
    isEmpty() -> null
    else -> get(Random.nextInt(size))
}