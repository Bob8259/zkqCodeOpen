package com.coc.zkqcode.utils.fileactions

import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.coc.zkqcode.utils.websocket.ServerConnection
import com.google.gson.Gson
import com.google.gson.JsonObject

class FileActions(
    private val serverConnection: ServerConnection,
    private val onConfigLoaded: (() -> Unit)? = null
) {
    private val gson = Gson()
    var configJson by mutableStateOf(JsonObject())
        private set

    fun getValue(key: String): String? {
        return if (configJson.has(key)) configJson.get(key).asString else null
    }

    private val baseDir = "${Environment.getExternalStorageDirectory().path}/zkqFiles/"
    private val configPath = "${baseDir}global_config.json"

    init {
        serverConnection.connect(
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
                    // Handle response based on status and data
                    if (response.has("status") && response.get("status").asString == "success") {
                        if (response.has("data")) {
                            val data = response.get("data").asString
                            // If data starts with { and ends with }, it's likely our config JSON
                            if (data.trim().startsWith("{") && data.trim().endsWith("}")) {
                                try {
                                    val loadedJson = gson.fromJson(data, JsonObject::class.java)
                                    // Update the existing configJson instead of replacing the object
                                    // to ensure Compose observers are notified correctly if they observe properties
                                    // Or just replace the whole JsonObject if it's a mutableStateOf
                                    configJson = loadedJson
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
                        if (errorMsg.contains("global_config.json")) {
                            // If the server returns an error for the config file (e.g., "File not found"),
                            // we still trigger the callback to use default values.
                            onConfigLoaded?.invoke()
                        }
                    }
                } catch (e: Exception) {
                    println("Error parsing message: ${e.message}")
                }
            },
            onFailure = { t -> println("Connection failed: ${t.message}") }
        )
    }

    fun writeToConfigFile(key: String, content: String) {
        configJson.addProperty(key, content)
        val writeAction = mapOf(
            "actionType" to "file_action",
            "subAction" to "write",
            "path" to configPath,
            "content" to gson.toJson(configJson)
        )
        serverConnection.sendAction(writeAction)
    }

    // 在 FileActions.kt 中增加这个方法
    fun getConnection(): ServerConnection {
        return this.serverConnection
    }

    fun updateConfig(newJson: JsonObject) {
        configJson = newJson
    }
}
