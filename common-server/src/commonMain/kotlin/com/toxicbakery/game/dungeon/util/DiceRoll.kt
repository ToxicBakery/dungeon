package com.toxicbakery.game.dungeon.util

import kotlin.random.Random
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.singleton

private class DiceRollImpl : DiceRoll {
    override fun roll(chance: Int): Boolean = Random.nextInt(chance) == chance / 2
}

interface DiceRoll {
    /**
     * Roll dice for a chance of a hit.
     */
    fun roll(chance: Int): Boolean
}

val diceRollModule = Kodein.Module("diceRollModule") {
    bind<DiceRoll>() with singleton {
        DiceRollImpl()
    }
}