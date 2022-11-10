package com.toxicbakery.game.dungeon.exception

class UnknownCommandException : Exception {
    constructor(command: String) : super("""Unknown command: "$command".""")
    constructor(command: String, message: String) : super(
        """($command) Unknown subcommand: ${message.take(20)}"""
    )
}
