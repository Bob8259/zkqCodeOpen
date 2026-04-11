package com.coc.zkqcode.core.system.checkpermissions

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.data.database.ServerConnectionMethod
import com.coc.zkqcode.core.data.websocket.LocalWebSocketServer
import com.coc.zkqcode.core.data.websocket.ServerTransportHelper
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import kotlinx.coroutines.delay

object ServerHelper {
    // Retained so CheckRoot can pass it to ServerActions after connection is established
    var localServer: LocalWebSocketServer? = null
        private set

    suspend fun waitForServerResponse(): ServerConnectionMethod? {
        tryLocalServerConnection()?.let { method ->
            GlobalVars.serverConnectionMethod = method
            return method
        }

        // Local WS server failed; stop it before falling back to shell HTTP
        localServer?.stopServer()
        localServer = null

        tryShellConnectionFallback()?.let { method ->
            GlobalVars.serverConnectionMethod = method
            return method
        }

        GlobalVars.serverConnectionMethod = null
        return null
    }

    /**
     * Start an embedded WebSocket server on port 16839 and wait up to 10 seconds
     * for the app_process client to connect and send its announcement.
     */
    private suspend fun tryLocalServerConnection(): ServerConnectionMethod? {
        val server = LocalWebSocketServer()
        localServer = server

        val connected = server.startAndAwaitClient(timeoutMs = 10_000L)
        if (connected) {
            showDebugInfo("Local WS server connection established")
            return ServerConnectionMethod.LOCAL_WS_SERVER
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
