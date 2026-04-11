package com.coc.zkqcode.core.data.websocket

import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.stop
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout

/**
 * Embedded WebSocket server on port 16839 at path /zkq.
 * The app_process APP connects to us as a client and exchanges JSON messages.
 */
class LocalWebSocketServer {
    companion object {
        const val PORT = 16839
        const val PATH = "/zkq"
    }

    private val gson = Gson()
    private val actionMutex = Mutex()

    @Volatile
    private var clientSession: WebSocketSession? = null

    // Signals that the app_process client has connected and sent its announcement
    private var clientConnectedDeferred = CompletableDeferred<Unit>()

    // Awaits the next response from the client for a dispatched action
    private var responseDeferred: CompletableDeferred<JsonObject>? = null

    private val server = embeddedServer(CIO, port = PORT) {
        install(WebSockets)
        routing {
            webSocket(PATH) {
                showDebugInfo("LocalWebSocketServer: client connected from ${this.hashCode()}")
                clientSession = this

                try {
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            handleFrame(frame.readText())
                        }
                    }
                } catch (e: Exception) {
                    showDebugInfo("LocalWebSocketServer: session error: ${e.message}")
                } finally {
                    // Clear session on disconnect so a reconnecting client can re-establish
                    if (clientSession == this) {
                        clientSession = null
                    }
                    showDebugInfo("LocalWebSocketServer: client disconnected")
                }
            }
        }
    }

    private fun handleFrame(text: String) {
        try {
            val json = gson.fromJson(text, JsonObject::class.java)
            val actionType = json.get("actionType")?.asString

            if (actionType == "client_connected") {
                // Announcement message from the app_process client
                showDebugInfo("LocalWebSocketServer: received client_connected announcement")
                if (!clientConnectedDeferred.isCompleted) {
                    clientConnectedDeferred.complete(Unit)
                }
                return
            }

            // Treat any other message as a response to a pending action
            responseDeferred?.complete(json)
        } catch (e: Exception) {
            showDebugInfo("LocalWebSocketServer: error parsing frame: ${e.message}")
        }
    }

    /**
     * Start the Ktor server and suspend until the app_process client connects
     * (sends its "client_connected" announcement) or [timeoutMs] elapses.
     *
     * @return true if the client connected within the timeout, false otherwise.
     */
    suspend fun startAndAwaitClient(timeoutMs: Long = 10_000L): Boolean {
        clientConnectedDeferred = CompletableDeferred()
        server.start(wait = false)
        showDebugInfo("LocalWebSocketServer: listening on port $PORT")

        return try {
            withTimeout(timeoutMs) {
                clientConnectedDeferred.await()
            }
            showDebugInfo("LocalWebSocketServer: client ready")
            true
        } catch (e: Exception) {
            showDebugInfo("LocalWebSocketServer: client did not connect within ${timeoutMs}ms")
            false
        }
    }

    /**
     * Send a JSON action to the connected client and await its response synchronously.
     */
    suspend fun sendActionSync(action: Any, timeout: Long = 5000L): JsonObject? {
        val session = clientSession
        if (session == null) {
            showDebugInfo("LocalWebSocketServer: no client session available")
            return null
        }

        return actionMutex.withLock {
            val deferred = CompletableDeferred<JsonObject>()
            responseDeferred = deferred

            try {
                val json = gson.toJson(action)
                session.send(Frame.Text(json))
                withTimeout(timeout) {
                    deferred.await()
                }
            } catch (e: Exception) {
                showDebugInfo("LocalWebSocketServer: action timeout or error: ${e.message}")
                null
            } finally {
                responseDeferred = null
            }
        }
    }

    /** Whether a client is currently connected. */
    fun isClientConnected(): Boolean = clientSession != null

    /** Gracefully shut down the Ktor server. */
    fun stopServer() {
        showDebugInfo("LocalWebSocketServer: stopping server")
        try {
            clientSession = null
            server.stop(gracePeriodMillis = 500, timeoutMillis = 1000)
        } catch (e: Exception) {
            showDebugInfo("LocalWebSocketServer: error stopping server: ${e.message}")
        }
    }
}
