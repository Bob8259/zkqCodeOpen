package com.coc.zkqcode.core.util.fileactions

import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.coc.zkqcode.core.data.websocket.ServerConnection
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.google.gson.Gson
import com.google.gson.JsonObject
import timber.log.Timber

class FileActions(
    private val serverConnection: ServerConnection,
    private val onConfigLoaded: (() -> Unit)? = null
) {
    private val gson = Gson()
    var configJson by mutableStateOf(JsonObject())
        private set
    var isLoading by mutableStateOf(true)
        private set

    fun getValue(key: String): String? {
        return if (configJson.has(key)) configJson.get(key).asString else null
    }

    private val baseDir = "${Environment.getExternalStorageDirectory().path}/zkqFiles/"
    private val configPath = "${baseDir}zkq_config.json"

    init {
        serverConnection.connect(
            // Do not modify these logics. This is designed for an ultra-fast config loading.
            // This will send two messages to the server, if the config file exists, then we will discard the check_exists response, and only load the config from read response.
            // If the config does not exist, then check_exists will return an error, so that we know the config does not exist.
            onOpen = {
                // Initial check for directory
                serverConnection.sendAction(
                    mapOf(
                        "actionType" to "file_action",
                        "subAction" to "check_exists",
                        "path" to baseDir
                    )
                )
                // Try to read existing config
                serverConnection.sendAction(
                    mapOf(
                        "actionType" to "file_action",
                        "subAction" to "read",
                        "path" to configPath
                    )
                )
            },
            onMessage = { message ->
                try {
                    val response = gson.fromJson(message, JsonObject::class.java)
                    // Route all responses to ReadWriteHelper
                    FileHelper.handleResponse(response)
                    
                    // Handle response based on status and data
                    if (response.has("status") && response.get("status").asString == "success") {
                        if (response.has("data")) {
                            val data = response.get("data").asString
                            // If data starts with { and ends with }, it's likely our config JSON
                            if (data.trim().startsWith("{") && data.trim().endsWith("}")) {
                                try {
                                    val loadedJson = gson.fromJson(data, JsonObject::class.java)
                                    // Update the existing configJson instead of replacing the object
                                    configJson = loadedJson
                                    isLoading = false
                                } catch (e: Exception) {
                                    println("Error parsing data as config: ${e.message}")
                                }
                            }

                        }
                        // Always trigger callback after receiving a success response
                        onConfigLoaded?.invoke()
                    }

                    if (response.has("status") && response.get("status").asString == "error") {
                        val errorMsg = response.get("message")?.asString ?: ""
                        if (errorMsg.contains("zkq_config.json")) {
                            // If the server returns an error for the config file (e.g., "File not found"),
                            // we still trigger the callback to use default values.
                            onConfigLoaded?.invoke()
                            isLoading = false
                        }
                    }
                } catch (e: Exception) {
                    println("Error parsing message: ${e.message}")
                    isLoading = false
                }
            },
            onFailure = { t ->
                println("Connection failed: ${t.message}")
                isLoading = false
            }
        )
    }

    suspend fun writeToConfigFile(key: String, content: String) {
        configJson.addProperty(key, content)
        FileHelper.writeJson(configPath, gson.toJson(configJson))
    }

    // 在 FileActions.kt 中增加这个方法
    fun getConnection(): ServerConnection {
        return this.serverConnection
    }

    fun updateConfig(newJson: JsonObject) {
        configJson = newJson
    }

    fun close() {
        serverConnection.close()
    }
}
