package com.toxicbakery.game.dungeon.machine.command.processor

import org.kodein.di.DI
import org.kodein.di.bindSet
import org.kodein.di.instance
import org.kodein.di.singleton

val commandGroupsModule = DI.Module("commandGroupsModule") {
    bindSet {
        add {
            singleton {
                CommandGroupRef(
                    name = "Movement",
                    commands = listOf(
                        instance(ProcessorLook.COMMAND),
                        instance(ProcessorWalk.COMMAND)
                    )
                )
            }
        }
        add {
            singleton {
                CommandGroupRef(
                    name = "Info",
                    commands = listOf(
                        instance(ProcessorWho.COMMAND)
                    )
                )
            }
        }
        add {
            singleton {
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
    }
}
