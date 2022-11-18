package com.toxicbakery.game.dungeon.defaults

import com.toxicbakery.game.dungeon.map.MapLegend
import com.toxicbakery.game.dungeon.map.MapLegendType
import com.toxicbakery.game.dungeon.map.MapManager
import com.toxicbakery.game.dungeon.map.WindowDescription
import com.toxicbakery.game.dungeon.model.Lookable.Animal
import com.toxicbakery.game.dungeon.model.world.Location
import kotlin.random.Random
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

private class AnimalGeneratorImpl(
    private val mapManager: MapManager,
) : AnimalGenerator {

    private val mapSize: Int
        get() = mapManager.mapSize()

    override suspend fun create(baseAnimal: BaseAnimal): Animal = when {
        baseAnimal.isLandAnimal -> Animal(
            name = baseAnimal.displayName,
            stats = baseAnimal.stats,
            statsBase = baseAnimal.stats,
            location = getRandomLocationOfType(MapLegendType.LAND),
            isPassive = baseAnimal.isPassive,
        )

        else -> TODO()
    }

    private fun getRandomLocationOfType(mapLegendType: MapLegendType): Location {
        while (true) {
            val (location, drawLocation) = getRandomLocation()
            if (drawLocation.type == mapLegendType)
                return location
        }
    }

    private fun getRandomLocation(): Pair<Location, MapLegend> {
        val location = Location(
            x = Random.nextInt(mapSize),
            y = Random.nextInt(mapSize),
        )

        val drawLocation = mapManager.drawLocation(
            windowDescription = WindowDescription(
                location = location,
                size = 1,
            )
        ).let(MapLegend.Companion::representingByte)

        return location to drawLocation
    }
}

interface AnimalGenerator {
    suspend fun create(baseAnimal: BaseAnimal): Animal
}

val animalGeneratorModule = DI.Module("animalGeneratorModule") {
    bind<AnimalGenerator>() with singleton {
        AnimalGeneratorImpl(
            mapManager = instance(),
        )
    }
}
