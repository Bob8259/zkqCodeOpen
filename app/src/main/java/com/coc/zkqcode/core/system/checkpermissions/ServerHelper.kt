package com.coc.zkqcode.core.system.checkpermissions

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.data.database.ServerConnectionMethod
import com.coc.zkqcode.core.data.websocket.ServerTransportHelper
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

object ServerHelper {
    suspend fun waitForServerResponse(): ServerConnectionMethod? {
        tryWebSocketConnection()?.let { method ->
            GlobalVars.serverConnectionMethod = method
            return method
        }

        tryShellConnectionFallback()?.let { method ->
            GlobalVars.serverConnectionMethod = method
            return method
        }

        GlobalVars.serverConnectionMethod = null
        return null
    }

    private suspend fun tryWebSocketConnection(): ServerConnectionMethod? {
        val client = OkHttpClient()
        val endTime = System.currentTimeMillis() + 8000L

        // Retry within the 8-second window because OkHttp fires onFailure immediately
        // when the server is not yet listening (connection refused).
        while (System.currentTimeMillis() < endTime) {
            val remaining = endTime - System.currentTimeMillis()
            if (remaining <= 0) break

            val deferred = CompletableDeferred<String?>()
            val request = Request.Builder().url(ServerTransportHelper.WEBSOCKET_URL).build()
            val webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    webSocket.send("""{"actionType": "connection_test"}""")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    deferred.complete(text)
                    webSocket.close(1000, "Done")
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    deferred.complete(null)
                }
            })

            val response = withTimeoutOrNull(remaining) {
                deferred.await()
            }
            webSocket.cancel()

            if (response != null) {
                return ServerConnectionMethod.WEBSOCKET
            }
            delay(500)
        }
        return null
    }

    private suspend fun tryShellConnectionFallback(): ServerConnectionMethod? {
        val endTime = System.currentTimeMillis() + 8000L
        val action = mapOf("actionType" to "connection_test")

        while (System.currentTimeMillis() < endTime) {
            if (tryShellMethod(action, ServerConnectionMethod.LIBSU_CURL)) {
                return ServerConnectionMethod.LIBSU_CURL
            }
            if (tryShellMethod(action, ServerConnectionMethod.LIBSU_WGET)) {
                return ServerConnectionMethod.LIBSU_WGET
            }
            delay(300)
        }
        return null
    }

    private suspend fun tryShellMethod(
        action: Map<String, String>,
        method: ServerConnectionMethod
    ): Boolean {
        val response = ServerTransportHelper.sendShellHttpAction(action, method)
        return ServerTransportHelper.isConnectionSuccess(response)
    }
}
