package com.toxicbakery.game.dungeon.exception

class NoPlayerWithIdException(id: String) : Exception("No player exists with id $id")
