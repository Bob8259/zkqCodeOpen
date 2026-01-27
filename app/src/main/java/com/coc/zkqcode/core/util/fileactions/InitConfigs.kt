package com.coc.zkqcode.core.util.fileactions

import com.coc.zkqcode.core.data.websocket.ServerConnection
import com.google.gson.Gson
import com.google.gson.JsonObject

object InitConfigs {
    data class InitResult(
        val configJson: JsonObject? = null,
        val isLoaded: Boolean = false,
        val shouldCallback: Boolean = false
    )

    fun sendStartupRequests(serverConnection: ServerConnection, baseDir: String, configPath: String) {
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
    }

    fun handleStartupResponse(response: JsonObject, gson: Gson): InitResult {
        var configJson: JsonObject? = null
        var isLoaded = false
        var shouldCallback = false

        // Handle response based on status and data
        if (response.has("status") && response.get("status")?.asString == "success") {
            if (response.has("data")) {
                val data = response.get("data")?.asString
                // If data starts with { and ends with }, it's likely our config JSON
                if (data?.trim()?.startsWith("{") == true && data.trim().endsWith("}")) {
                    try {
                        val loadedJson = gson.fromJson(data, JsonObject::class.java)
                        configJson = loadedJson
                        isLoaded = true
                    } catch (e: Exception) {
                        println("Error parsing data as config: ${e.message}")
                    }
                }
            }
            // Always trigger callback after receiving a success response
            shouldCallback = true
        }

        if (response.has("status") && response.get("status")?.asString == "error") {
            val errorMsg = response.get("message")?.asString ?: ""
            if (errorMsg.contains("zkq_config.json")) {
                // If the server returns an error for the config file (e.g., "File not found"),
                // we still trigger the callback to use default values.
                shouldCallback = true
                isLoaded = true
            }
        }

        return InitResult(configJson, isLoaded, shouldCallback)
    }
}
