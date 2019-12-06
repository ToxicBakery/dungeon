package com.toxicbakery.game.dungeon.model

import com.toxicbakery.game.dungeon.model.character.Player
import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.game.dungeon.model.session.PlayerSession

data class DungeonState(
    private val playerSessionsByPlayerId: Map<Int, PlayerSession> = mapOf(),
    private val gameSessionsBySessionId: Map<String, GameSession> = mapOf()
) {

    val playerSessionsList: List<PlayerSession>
        get() = playerSessionsByPlayerId.values.toList()

    val gameSessionList: List<GameSession>
        get() = gameSessionsBySessionId.values.toList()

    fun getPlayerSessionById(playerId: Int): PlayerSession = playerSessionsByPlayerId.getValue(playerId)

    fun getGameSession(gameSession: GameSession): GameSession = gameSessionsBySessionId.getValue(gameSession.sessionId)

    fun updatePlayer(player: Player): DungeonState = copy(
        playerSessionsByPlayerId = playerSessionsByPlayerId +
                (player.id to playerSessionsByPlayerId.getValue(player.id).copy(player = player))
    )

    fun getAuthenticatedGameSession(gameSession: GameSession): GameSession? =
        gameSessionsBySessionId[gameSession.sessionId]
            ?.let { storedSession -> playerSessionsByPlayerId[storedSession.playerId] }
            ?.let(PlayerSession::session)

    fun addUnauthenticatedSession(session: GameSession) = copy(
        gameSessionsBySessionId = gameSessionsBySessionId + (session.sessionId to session)
    )

    fun setAuthenticatedPlayer(
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

    fun removePlayerAndSession(player: Player): DungeonState = playerSessionsByPlayerId.getValue(player.id)
        .session
        .let { session ->
            copy(
                playerSessionsByPlayerId = playerSessionsByPlayerId - player.id,
                gameSessionsBySessionId = gameSessionsBySessionId - session.sessionId
            )
        }

    fun removePlayerAndSession(gameSession: GameSession): DungeonState =
        gameSessionsBySessionId.getValue(gameSession.sessionId).let { storedSession ->
            copy(
                playerSessionsByPlayerId = playerSessionsByPlayerId.minus(storedSession.playerId),
                gameSessionsBySessionId = gameSessionsBySessionId.minus(storedSession.sessionId)
            )
        }

}
