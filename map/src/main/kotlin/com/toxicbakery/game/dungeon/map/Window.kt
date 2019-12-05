package com.toxicbakery.game.dungeon.map

import com.toxicbakery.game.dungeon.model.character.Location

class Window(
    val location: Location,
    val size: Int
) {

    val topLeftLocation: Location =
        Location(
            x = location.x - (size / 2),
            y = location.y - (size / 2)
        )

    val bottomRightLocation = Location(
        x = location.x + (size / 2),
        y = location.y + (size / 2)
    )

}
