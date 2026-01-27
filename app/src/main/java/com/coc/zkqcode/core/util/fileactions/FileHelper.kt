package com.coc.zkqcode.core.util.fileactions

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object FileHelper {
    private val mutex = Mutex()
    private val gson = Gson()
    private var pendingResponse = CompletableDeferred<JsonObject>()

    suspend fun writeJson(path: String, content: String): Boolean {
        return mutex.withLock {
            pendingResponse = CompletableDeferred()

            val writeAction = mapOf(
                "actionType" to "file_action",
                "subAction" to "write",
                "path" to path,
                "content" to content
            )
            GlobalVars.serverActions?.getConnection()?.sendAction(writeAction)
                ?: logAndStop("Write to Json failed")

            try {
                val response = withTimeout(5000L) {
                    pendingResponse.await()
                }
                response.has("status") && response.get("status").asString == "success"
            } catch (e: Exception) {
                showDebugInfo("Write error: $e")
                false
            }
        }
    }

    suspend fun readJson(path: String): JsonObject? {
        return mutex.withLock {
            pendingResponse = CompletableDeferred()

            val readAction = mapOf(
                "actionType" to "file_action",
                "subAction" to "read",
                "path" to path
            )
            GlobalVars.serverActions?.getConnection()?.sendAction(readAction)
                ?: logAndStop("Read Json failed")

            try {
                val response = withTimeout(5000L) {
                    pendingResponse.await()
                }
                showDebugInfo("response $response")
                if (response.has("status") && response.get("status").asString == "success") {
                    val data = response.get("data")?.asString
                    if (data != null && data.trim().startsWith("{") && data.trim().endsWith("}")) {
                        gson.fromJson(data, JsonObject::class.java)
                    } else {
                        null
                    }
                } else {
                    null
                }
            } catch (e: Exception) {
                showDebugInfo("error$e")
                null
            }
        }
    }

    suspend fun checkExists(path: String): Boolean {
        return mutex.withLock {
            pendingResponse = CompletableDeferred()

            val checkExistsAction = mapOf(
                "actionType" to "file_action",
                "subAction" to "check_exists",
                "path" to path
            )
            GlobalVars.serverActions?.getConnection()?.sendAction(checkExistsAction)
                ?: logAndStop("Check exists failed")

            try {
                val response = withTimeout(5000L) {
                    pendingResponse.await()
                }
                response.has("status") && response.get("status").asString == "success"
            } catch (e: Exception) {
                showDebugInfo("Check exists error: $e")
                false
            }
        }
    }

    fun handleResponse(response: JsonObject) {
        pendingResponse.complete(response)
    }

}
