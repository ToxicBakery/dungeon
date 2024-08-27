package com.toxicbakery.game.dungeon.defaults

import com.toxicbakery.game.dungeon.map.MapLegendType
import com.toxicbakery.game.dungeon.model.character.stats.Stats
import kotlin.jvm.JvmStatic
import kotlin.random.Random

enum class BaseAnimal(
    val displayName: String,
    val stats: Stats,
    val distribution: Double,
    val isPassive: Boolean,
    val spawnMapType: MapLegendType = MapLegendType.LAND,
) {
    Sheep(
        displayName = "sheep",
        distribution = 0.5,
        stats = Stats(
            health = 10,
            strength = 2,
            dexterity = 1,
            defence = 1,
            luck = 1,
            stamina = 6,
        ),
        isPassive = true
    ),
    Horse(
        displayName = "horse",
        distribution = 0.4,
        stats = Stats(
            health = 70,
            strength = 10,
            dexterity = 1,
            defence = 1,
            luck = 5,
            stamina = 10,
        ),
        isPassive = true
    ),
    Bear(
        displayName = "bear",
        distribution = 0.1,
        stats = Stats(
            health = 100,
            strength = 7,
            dexterity = 3,
            defence = 5,
            luck = 5,
            stamina = 8,
        ),
        isPassive = false
    ),
    Shark(
        displayName = "shark",
        distribution = 0.2,
        stats = Stats(
            health = 50,
            strength = 10,
            dexterity = 6,
            defence = 3,
            luck = 2,
            stamina = 10,
        ),
        isPassive = false,
        spawnMapType = MapLegendType.WATER,
    ),
    Wolf(
        displayName = "wolf",
        distribution = 0.1,
        stats = Stats(
            health = 70,
            strength = 4,
            dexterity = 3,
            defence = 4,
            luck = 5,
            stamina = 9,
        ),
        isPassive = false
    );

    companion object {
        val names: List<String> by lazy {
            entries.map { animal -> animal.name }
        }

        @JvmStatic
        fun pickNextAnimal(): BaseAnimal {
            val pick = Random.nextDouble()
            return entries.asSequence()
                .filter { animal -> animal.distribution > pick }
                .fold(listOf<BaseAnimal>()) { acc, animal -> acc + animal }
                .let { animals ->
                    if (animals.isEmpty()) pickNextAnimal()
                    else animals[Random.nextInt(animals.size)]
                }
        }
    }
}
