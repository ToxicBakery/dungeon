package com.toxicbakery.game.dungeon.exception

class UnknownCommandException(command: String) : Exception("""Unknown command "$command".""")
