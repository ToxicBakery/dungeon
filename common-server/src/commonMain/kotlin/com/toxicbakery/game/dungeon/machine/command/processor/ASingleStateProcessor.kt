package com.toxicbakery.game.dungeon.machine.command.processor

abstract class ASingleStateProcessor : SingleStateProcessor {

    override val currentState: SingleState = SingleState.Init
}

interface SingleStateProcessor : CommandProcessor<SingleState>

sealed class SingleState {
    object Init : SingleState()
}
