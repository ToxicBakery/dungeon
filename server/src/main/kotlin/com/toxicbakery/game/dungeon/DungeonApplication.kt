package com.toxicbakery.game.dungeon

import com.toxicbakery.game.dungeon.model.session.GameSession
import com.toxicbakery.logging.Arbor
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.http.content.defaultResource
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import java.time.Duration
import kotlinx.coroutines.channels.consumeEach
import org.kodein.di.DI
import org.kodein.di.instance

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

private val applicationKodein = DI {
    extend(commonApplicationKodein)
    import(dungeonServerModule)
}

private val dungeonServer: DungeonServer by applicationKodein.instance()

fun Application.module() {
    install(Authentication) {
        basic {
            validate { credentials ->
                if (credentials.name == "test" && credentials.password == "password") {
                    UserIdPrincipal(credentials.name)
                } else null
            }
        }
    }
    install(DefaultHeaders)
    install(CallLogging)
    install(WebSockets) {
        pingPeriod = Duration.ofMinutes(1)
        timeout = Duration.ofMinutes(1)
        maxFrameSize = MAXIMUM_FRAME_SIZE_BYTES
        masking = false
    }
    routing {
        webSocket("/ws") {
            val gameSession: GameSession = WebSocketGameSession(webSocketServerSession = this)
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
