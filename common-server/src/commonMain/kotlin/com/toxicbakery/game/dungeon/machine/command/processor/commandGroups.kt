package com.toxicbakery.game.dungeon.machine.command.processor

import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.inSet
import org.kodein.di.erased.instance
import org.kodein.di.erased.setBinding
import org.kodein.di.erased.singleton

val commandGroupsModule = Kodein.Module("commandGroupsModule") {
    bind() from setBinding<CommandGroupRef>()
    bind<CommandGroupRef>().inSet() with singleton {
        CommandGroupRef(
            name = "Movement",
            commands = listOf(
                instance(ProcessorLook.COMMAND),
                instance(ProcessorWalk.COMMAND)
            )
        )
    }
    bind<CommandGroupRef>().inSet() with singleton {
        CommandGroupRef(
            name = "Info",
            commands = listOf(
                instance(ProcessorWho.COMMAND)
            )
        )
    }
    bind<CommandGroupRef>().inSet() with singleton {
        CommandGroupRef(
            name = "Communication",
            commands = listOf(
                instance(ProcessorGsay.COMMAND),
                instance(ProcessorSay.COMMAND),
                instance(ProcessorShout.COMMAND)
            )
        )
    }
}
