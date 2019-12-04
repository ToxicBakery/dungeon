package com.toxicbakery.game.dungeon.machine.authentication

enum class AuthenticationState {
    Init,
    AwaitingUsername,
    AwaitingPassword,
    Authenticated
}
