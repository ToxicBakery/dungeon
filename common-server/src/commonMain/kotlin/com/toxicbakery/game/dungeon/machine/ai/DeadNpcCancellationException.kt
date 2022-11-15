package com.toxicbakery.game.dungeon.machine.ai

import com.toxicbakery.game.dungeon.model.character.Npc
import kotlinx.coroutines.CancellationException

internal class DeadNpcCancellationException(npc: Npc) : CancellationException(
    "Ending tick monitor after ${npc.name} died at ${npc.location}"
)