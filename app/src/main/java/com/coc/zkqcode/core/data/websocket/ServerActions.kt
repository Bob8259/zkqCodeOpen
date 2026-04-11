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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ServerActions(
    private val localServer: LocalWebSocketServer? = null,
    private val onConfigLoaded: (() -> Unit)? = null
) {
    private val gson = Gson()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    var configJson by mutableStateOf(JsonObject())
        private set
    var isLoading by mutableStateOf(true)
        private set

    private val actionMutex = Mutex()

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
            ServerConnectionMethod.LOCAL_WS_SERVER -> {
                scope.launch {
                    loadStartupConfigWithLocalServer()
                }
            }
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

    /**
     * Load startup config by sending actions through the local WebSocket server.
     */
    private suspend fun loadStartupConfigWithLocalServer() {
        val server = localServer
        if (server == null) {
            showDebugInfo("Local WS server instance is not available.")
            isLoading = false
            return
        }

        actionMutex.withLock {
            try {
                InitConfigs.buildStartupActions(baseDir, configPath).forEach { action ->
                    val response = server.sendActionSync(action)
                    if (response != null) {
                        handleResponse(response)
                    }
                }
            } catch (e: Exception) {
                showDebugInfo("Local WS server startup load failed: ${e.message}")
            } finally {
                if (isLoading) {
                    isLoading = false
                }
            }
        }
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
            ServerConnectionMethod.LOCAL_WS_SERVER -> {
                localServer?.sendActionSync(action, timeout)
            }
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

    fun updateConfig(newJson: JsonObject) {
        configJson = newJson
    }

    fun close() {
        scope.cancel()
        localServer?.stopServer()
    }
}
