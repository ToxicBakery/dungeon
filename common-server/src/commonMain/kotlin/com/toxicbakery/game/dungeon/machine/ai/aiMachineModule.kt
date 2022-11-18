package com.toxicbakery.game.dungeon.machine.ai

import com.toxicbakery.game.dungeon.machine.TickableMachine
import com.toxicbakery.game.dungeon.model.Lookable.Animal
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory

val aiMachineModule = DI.Module("aiMachineModule") {
    import(aggressiveAnimalMachineModule)
    import(passiveAnimalMachineModule)
    bind<TickableMachine<*>>() with factory { animal: Animal ->
        when {
            animal.isPassive -> factory<Animal, PassiveAnimalMachine>()(animal)
            else -> factory<Animal, AggressiveAnimalMachine>()(animal)
        }
    }
}
