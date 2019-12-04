package com.toxicbakery.game.dungeon.exception

class NoPlayerWithIdException(id: Int) : Exception("No player exists with id $id")
