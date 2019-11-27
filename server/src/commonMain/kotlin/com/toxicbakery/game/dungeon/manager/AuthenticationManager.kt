package com.toxicbakery.game.dungeon.manager

import com.toxicbakery.game.dungeon.Database
import com.toxicbakery.game.dungeon.Player
import com.toxicbakery.game.dungeon.auth.Credentials
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

private class AuthenticationManagerImpl(
    private val database: Database
) : AuthenticationManager {



    override suspend fun authenticatePlayerById(
        id: String,
        credentials: Credentials
    ) = database.authenticatePlayer(id, credentials)

    override suspend fun registerPlayer(
        player: Player,
        credentials: Credentials
    ) = database.createPlayer(player, credentials)

}

interface AuthenticationManager {

    suspend fun authenticatePlayerById(
        id: String,
        credentials: Credentials
    ): Player

    suspend fun registerPlayer(
        player: Player,
        credentials: Credentials
    ): Player

}

val authenticationManagerModule = Kodein.Module("authenticationManagerModule") {
    bind<AuthenticationManager>() with provider {
        AuthenticationManagerImpl(
            database = instance()
        )
    }
}
