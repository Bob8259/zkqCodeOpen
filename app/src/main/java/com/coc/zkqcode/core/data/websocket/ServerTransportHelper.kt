package com.coc.zkqcode.core.data.websocket

import com.coc.zkqcode.core.data.database.ServerConnectionMethod
import com.coc.zkqcode.core.util.basic.RunShell
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Shared helpers for addressing the local server through shell-backed HTTP (curl/wget).
 */
object ServerTransportHelper {
    const val HTTP_URL = "http://localhost:6839/zkq"

    private val gson = Gson()

    fun buildHttpUrl(action: Any): String {
        val jsonObject = gson.toJsonTree(action).asJsonObject
        val query = jsonObject.entrySet().joinToString("&") { (key, value) ->
            "${encode(key)}=${encode(jsonElementToString(value))}"
        }
        return "$HTTP_URL?$query"
    }

    suspend fun sendShellHttpAction(
        action: Any,
        method: ServerConnectionMethod
    ): JsonObject? {
        val response = sendShellHttpRequest(buildHttpUrl(action), method)
        return parseResponse(response)
    }

    suspend fun sendShellHttpRequest(
        url: String,
        method: ServerConnectionMethod
    ): String? {
        val command = buildShellCommand(url, method)
        val output = RunShell.run(command, checkIsPlaying = false)
            .joinToString("\n")
            .trim()
        return output.ifBlank { null }
    }

    fun parseResponse(response: String?): JsonObject? {
        if (response.isNullOrBlank()) {
            return null
        }
        return runCatching {
            gson.fromJson(response, JsonObject::class.java)
        }.getOrNull()
    }

    fun isConnectionSuccess(response: JsonObject?): Boolean {
        return response?.get("status")?.asString == "success" &&
            response.get("data")?.asString == "connected"
    }

    private fun buildShellCommand(
        url: String,
        method: ServerConnectionMethod
    ): String {
        val escapedUrl = url.replace("'", "'\"'\"'")
        return when (method) {
            ServerConnectionMethod.LIBSU_CURL ->
                "curl -g -sS --max-time 2 '$escapedUrl'"
            ServerConnectionMethod.LIBSU_WGET ->
                "wget -q -T 2 -O - '$escapedUrl'"
            ServerConnectionMethod.LOCAL_WS_SERVER ->
                error("Local WS server transport cannot execute shell HTTP requests.")
        }
    }

    private fun encode(value: String): String {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
    }

    private fun jsonElementToString(element: JsonElement): String {
        return when {
            element.isJsonNull -> ""
            element.isJsonPrimitive -> element.asJsonPrimitive.run {
                when {
                    isString -> asString
                    isBoolean -> asBoolean.toString()
                    isNumber -> asNumber.toString()
                    else -> toString()
                }
            }
            else -> gson.toJson(element)
        }
    }
}
