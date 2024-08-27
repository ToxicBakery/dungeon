package com.toxicbakery.game.dungeon.machine

import com.toxicbakery.game.dungeon.machine.ai.aiMachineModule
import com.toxicbakery.game.dungeon.machine.authentication.authenticationMachineModule
import com.toxicbakery.game.dungeon.machine.command.commandMachineModule
import com.toxicbakery.game.dungeon.machine.init.initMachineModule
import com.toxicbakery.game.dungeon.machine.registration.registrationMachineModule
import org.kodein.di.DI

val machineModules = DI.Module("machineModules") {
    import(aiMachineModule)
    import(authenticationMachineModule)
    import(commandMachineModule)
    import(initMachineModule)
    import(registrationMachineModule)
}
