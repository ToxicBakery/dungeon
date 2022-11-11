package com.toxicbakery.game.dungeon.ui

import com.toxicbakery.game.dungeon.model.Lookable

internal fun List<Lookable>.lookableDescriptions() = lookableDescriptionStrings()
    .filter(String::isNotEmpty)
    .joinToString(separator = "\n\t")
    .let { displayables -> if (displayables.isNotEmpty()) "\n$displayables" else "" }

private fun List<Lookable>.lookableDescriptionStrings() = map { lookable ->
    when (lookable) {
        is Lookable.Animal ->
            if (lookable.isPassive) "A ${lookable.name} wanders around"
            else "A ${lookable.name} is on the hunt"

        is Lookable.Creature ->
            if (lookable.isPassive) "A ${lookable.name} wanders around"
            else "A ${lookable.name} is charging towards you"

        is Lookable.Npc -> "You see ${lookable.name} looking back at you"
        is Lookable.Player -> "You see ${lookable.name} looking back at you"
        else -> ""
    }
}
