package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.logging.Arbor
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.routing
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import org.kodein.di.Kodein
import org.kodein.di.erased.instance
import java.time.Duration

class DungeonApplication(
    kodein: Kodein
) {

    private val dungeonServer: DungeonServer by kodein.instance()

    @UseExperimental(ExperimentalCoroutinesApi::class)
    fun Application.main() {
        install(DefaultHeaders)
        install(CallLogging)
        install(WebSockets) {
            pingPeriod = Duration.ofMinutes(1)
            maxFrameSize = MAXIMUM_FRAME_SIZE_BYTES
        }
        routing {
            webSocket("/ws") {

                val gameSession: GameSession = WebSocketGameSession(
                    webSocketServerSession = this
                )
                dungeonServer.onNewSession(gameSession)
                try {
                    incoming.consumeEach { frame: Frame ->
                        process(gameSession, frame)
                    }
                } finally {
                    dungeonServer.onLostSession(gameSession)
                }
            }
            static {
                defaultResource(
                    "index.html",
                    "web"
                )
                resources("web")
            }
        }
    }

    private suspend fun process(
        session: GameSession,
        frame: Frame
    ) {
        when (frame) {
            is Frame.Text -> dungeonServer.receivedMessage(session, frame.readText())
            else -> Arbor.w("Ignoring unhandled frame of type %s", frame.frameType)
        }
    }

}
