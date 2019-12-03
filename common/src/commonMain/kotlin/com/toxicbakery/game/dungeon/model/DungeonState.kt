package com.toxicbakery.game.dungeon.model

import com.toxicbakery.game.dungeon.model.character.Player
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.model.session.PlayerSession
import com.toxicbakery.game.dungeon.model.session.PlayerSessionId

data class DungeonState(
    private val playerSessionsByPlayerId: Map<Int, PlayerSession> = mapOf(),
    private val gameSessionsBySessionId: Map<PlayerSessionId, GameSession> = mapOf()
) {

    val playerSessionsList: List<PlayerSession>
        get() = playerSessionsByPlayerId.values.toList()

    val gameSessionList: List<GameSession>
        get() = gameSessionsBySessionId.values.toList()

    operator fun get(gameSession: GameSession): GameSession? = playerSessionsByPlayerId
        .values
        .firstOrNull { playerSession -> playerSession.session.sessionId == gameSession.sessionId }
        ?.let(PlayerSession::session)

    operator fun plus(session: GameSession) = copy(
        gameSessionsBySessionId = gameSessionsBySessionId + (session.sessionId to session)
    )

    operator fun set(
        player: Player,
        gameSession: GameSession
    ): DungeonState = PlayerSession(
        player = player,
        session = gameSession
    ).let { playerSession ->
        copy(
            playerSessionsByPlayerId = playerSessionsByPlayerId + (player.id to playerSession),
            gameSessionsBySessionId = gameSessionsBySessionId + (gameSession.sessionId to gameSession)
        )
    }

    operator fun minus(
        player: Player
    ): DungeonState = playerSessionsByPlayerId[player.id]
        ?.session
        ?.let { session ->
            copy(
                playerSessionsByPlayerId = playerSessionsByPlayerId - player.id,
                gameSessionsBySessionId = gameSessionsBySessionId - session.sessionId
            )
        }
        ?: this

    operator fun minus(
        session: GameSession
    ): DungeonState = gameSessionsBySessionId[session.sessionId]
        ?.let {
            val playerId = playerSessionsByPlayerId.values
                .firstOrNull { playerSession -> playerSession.session.sessionId == session.sessionId }
                ?.player
                ?.id

            if (playerId == null) copy(gameSessionsBySessionId = gameSessionsBySessionId - session.sessionId)
            else copy(
                playerSessionsByPlayerId = playerSessionsByPlayerId - playerId,
                gameSessionsBySessionId = gameSessionsBySessionId - session.sessionId
            )
        }
        ?: this

}
