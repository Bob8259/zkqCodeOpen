package com.coc.zkqcode.core.util.fileactions

import com.coc.zkqcode.core.ui.components.GlobalVars
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndDie
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object ReadWriteHelper {
    private val gson = Gson()
    private val mutex = Mutex()
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
            GlobalVars.fileActions?.getConnection()?.sendAction(writeAction)
                ?: logAndDie("Write to Json failed")

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

    suspend fun readJson(path: String): String? {
        return mutex.withLock {
            pendingResponse = CompletableDeferred()

            val readAction = mapOf(
                "actionType" to "file_action",
                "subAction" to "read",
                "path" to path
            )
            GlobalVars.fileActions?.getConnection()?.sendAction(readAction)
                ?: logAndDie("Read Json failed")

            try {
                val response = withTimeout(5000L) {
                    pendingResponse.await()
                }
                showDebugInfo("response${response.toString()}")
                if (response.has("status") && response.get("status").asString == "success") {
                    response.get("data")?.asString
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
            GlobalVars.fileActions?.getConnection()?.sendAction(checkExistsAction)
                ?: logAndDie("Check exists failed")

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
