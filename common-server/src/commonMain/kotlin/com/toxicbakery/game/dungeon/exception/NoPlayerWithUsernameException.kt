package com.toxicbakery.game.dungeon.exception

class NoPlayerWithUsernameException(username: String) : Exception("No player exists with username $username")
