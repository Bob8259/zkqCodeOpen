package com.coc.zkqcode.core.data.websocket

import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.data.database.ServerConnectionMethod
import com.coc.zkqcode.core.util.fileactions.FileHelper
import com.coc.zkqcode.core.util.fileactions.InitConfigs
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout

class ServerActions(
    private val serverConnection: ServerConnection? = null,
    private val onConfigLoaded: (() -> Unit)? = null
) {
    private val gson = Gson()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    var configJson by mutableStateOf(JsonObject())
        private set
    var isLoading by mutableStateOf(true)
        private set

    private val actionMutex = Mutex()
    private var actionDeferred: CompletableDeferred<JsonObject>? = null

    fun getValue(key: String): String? {
        return if (configJson.has(key)) configJson.get(key)?.asString else null
    }

    private val baseDir = "${Environment.getExternalStorageDirectory().path}/zkqFiles/"
    private val configPath = "${baseDir}zkq_config.json"

    init {
        performConnect()
    }

    private fun performConnect() {
        when (val method = GlobalVars.serverConnectionMethod) {
            ServerConnectionMethod.WEBSOCKET -> connectWithWebSocket()
            ServerConnectionMethod.LIBSU_CURL,
            ServerConnectionMethod.LIBSU_WGET -> {
                scope.launch {
                    loadStartupConfigWithShell(method)
                }
            }
            null -> {
                showDebugInfo("Server connection method not initialized.")
                isLoading = false
            }
        }
    }

    private fun connectWithWebSocket() {
        val connection = serverConnection
        if (connection == null) {
            showDebugInfo("WebSocket server connection is not available.")
            isLoading = false
            return
        }

        // Server is already started by ServerManager; this method only connects as a client.
        connection.connect(
            onOpen = {
                // Keep the original startup request order so config loading stays fast.
                InitConfigs.sendStartupRequests(connection, baseDir, configPath)
            },
            onMessage = onMessage@{ message ->
                try {
                    handleResponse(gson.fromJson(message, JsonObject::class.java))
                } catch (e: Exception) {
                    showDebugInfo("Error parsing message: ${e.message}")
                    isLoading = false
                }
            },
            onFailure = { throwable ->
                showDebugInfo("Connection failed: ${throwable.message}")
                isLoading = false
                actionDeferred?.completeExceptionally(throwable)
            }
        )
    }

    private suspend fun loadStartupConfigWithShell(method: ServerConnectionMethod) {
        actionMutex.withLock {
            try {
                InitConfigs.buildStartupActions(baseDir, configPath).forEach { action ->
                    val response = ServerTransportHelper.sendShellHttpAction(action, method)
                    if (response != null) {
                        handleResponse(response)
                    }
                }
            } catch (e: Exception) {
                showDebugInfo("Shell startup load failed: ${e.message}")
            } finally {
                if (isLoading) {
                    isLoading = false
                }
            }
        }
    }

    private fun handleResponse(response: JsonObject) {
        actionDeferred?.complete(response)

        if (ServerTransportHelper.isConnectionSuccess(response)) {
            return
        }

        val initResult = InitConfigs.handleStartupResponse(response, gson)
        if (initResult.configJson != null) {
            configJson = initResult.configJson
        }
        if (initResult.isLoaded) {
            isLoading = false
        }
        if (initResult.shouldCallback) {
            onConfigLoaded?.invoke()
        }
    }

    suspend fun writeToConfigFile(key: String, content: String) {
        configJson.addProperty(key, content)
        FileHelper.writeJson(configPath, gson.toJson(configJson))
    }

    suspend fun sendActionSync(action: Any, timeout: Long = 5000L): JsonObject? {
        return when (val method = GlobalVars.serverConnectionMethod) {
            ServerConnectionMethod.WEBSOCKET -> sendWebSocketActionSync(action, timeout)
            ServerConnectionMethod.LIBSU_CURL,
            ServerConnectionMethod.LIBSU_WGET -> {
                actionMutex.withLock {
                    ServerTransportHelper.sendShellHttpAction(action, method)
                }
            }
            null -> {
                showDebugInfo("Server connection method not initialized.")
                null
            }
        }
    }

    private suspend fun sendWebSocketActionSync(action: Any, timeout: Long): JsonObject? {
        val connection = serverConnection
        if (connection == null) {
            showDebugInfo("WebSocket server connection is not available.")
            return null
        }

        return actionMutex.withLock {
            val deferred = CompletableDeferred<JsonObject>()
            actionDeferred = deferred
            connection.sendAction(action)
            try {
                withTimeout(timeout) {
                    deferred.await()
                }
            } catch (e: Exception) {
                showDebugInfo("Action timeout or error: ${e.message}")
                reconnect()
                null
            } finally {
                actionDeferred = null
            }
        }
    }

    private fun reconnect() {
        if (GlobalVars.serverConnectionMethod != ServerConnectionMethod.WEBSOCKET) {
            return
        }
        serverConnection?.close()
        performConnect()
    }

    fun updateConfig(newJson: JsonObject) {
        configJson = newJson
    }

    fun close() {
        scope.cancel()
        serverConnection?.close()
    }
}