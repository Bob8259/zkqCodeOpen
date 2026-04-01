package com.coc.zkqcode.core.data.websocket

import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.fileactions.FileHelper
import com.coc.zkqcode.core.util.fileactions.InitConfigs
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout


class ServerActions(
    private val serverConnection: ServerConnection,
    private val onConfigLoaded: (() -> Unit)? = null
) {
    private val gson = Gson()
    var configJson by mutableStateOf(JsonObject())
        private set
    var isLoading by mutableStateOf(true)
        private set

    //    private val connectionMutex = Mutex()
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
        // Server is already started by ServerManager; this method only connects as a client
        serverConnection.connect(
            // Do not modify these logics. This is designed for an ultra-fast config loading.
            // This will send two messages to the server, if the config file exists, then we will discard the check_exists response, and only load the config from read response.
            // If the config does not exist, then check_exists will return an error, so that we know the config does not exist.
            onOpen = {
                InitConfigs.sendStartupRequests(serverConnection, baseDir, configPath)
            },
            onMessage = onMessage@{ message ->
                try {
                    val response = gson.fromJson(message, JsonObject::class.java)

                    // Handle connection test response (legacy or if we still want it specific)
                    // But now we use actionDeferred for everything
                    actionDeferred?.complete(response)

                    if (response.has("status") && response.get("status").asString == "success" &&
                        response.has("data") && response.get("data").asString == "connected"
                    ) {
                        return@onMessage
                    }

                    // Route all responses to specific handlers if needed

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
                } catch (e: Exception) {
                    showDebugInfo("Error parsing message: ${e.message}")
                    isLoading = false
                }
            },
            onFailure = { t ->
                showDebugInfo("Connection failed: ${t.message}")
                isLoading = false
                actionDeferred?.completeExceptionally(t)
            }
        )
    }

    suspend fun writeToConfigFile(key: String, content: String) {
        configJson.addProperty(key, content)
        FileHelper.writeJson(configPath, gson.toJson(configJson))
    }


    suspend fun sendActionSync(action: Any, timeout: Long = 5000L): JsonObject? {
        return actionMutex.withLock {
            val deferred = CompletableDeferred<JsonObject>()
            actionDeferred = deferred
            serverConnection.sendAction(action)
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
        serverConnection.close()
        performConnect()
    }

    fun updateConfig(newJson: JsonObject) {
        configJson = newJson
    }

    fun close() {
        serverConnection.close()
    }
}