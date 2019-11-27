package com.toxicbakery.game.dungeon.model

import com.toxicbakery.game.dungeon.Player
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.model.session.PlayerSession
import com.toxicbakery.game.dungeon.model.session.PlayerSessionId

data class DungeonState(
    private val playerSessionsByPlayerId: Map<Int, PlayerSession> = mapOf(),
    private val playerSessionsBySessionId: Map<PlayerSessionId, GameSession> = mapOf()
) {

    val playerSessionsList: List<PlayerSession>
        get() = playerSessionsByPlayerId.values.toList()

    val gameSessionList: List<GameSession>
        get() = playerSessionsBySessionId.values.toList()

    operator fun get(gameSession: GameSession): GameSession? = playerSessionsByPlayerId
        .values
        .firstOrNull { playerSession -> playerSession.session.sessionId == gameSession.sessionId }
        ?.let(PlayerSession::session)

    operator fun get(player: Player): PlayerSession? = playerSessionsByPlayerId[player.id]

    operator fun plus(session: GameSession) = copy(
        playerSessionsBySessionId = playerSessionsBySessionId + (session.sessionId to session)
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
            playerSessionsBySessionId = playerSessionsBySessionId + (gameSession.sessionId to gameSession)
        )
    }

    operator fun minus(
        player: Player
    ): DungeonState = get(player)
        ?.session
        ?.let { session ->
            copy(
                playerSessionsByPlayerId = playerSessionsByPlayerId - player.id,
                playerSessionsBySessionId = playerSessionsBySessionId - session.sessionId
            )
        }
        ?: this

    operator fun minus(
        session: GameSession
    ): DungeonState = playerSessionsBySessionId[session.sessionId]
        ?.let {
            val playerId = playerSessionsByPlayerId.values
                .firstOrNull { playerSession -> playerSession.session.sessionId == session.sessionId }
                ?.player
                ?.id

            if (playerId == null) copy(playerSessionsBySessionId = playerSessionsBySessionId - session.sessionId)
            else copy(
                playerSessionsByPlayerId = playerSessionsByPlayerId - playerId,
                playerSessionsBySessionId = playerSessionsBySessionId - session.sessionId
            )
        }
        ?: this

}
