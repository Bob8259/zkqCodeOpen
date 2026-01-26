package com.coc.zkqcode.core.util.fileactions

import com.coc.zkqcode.core.ui.components.GlobalVars
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndDie
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeout
import java.util.UUID

object ReadWriteHelper {
    private val gson = Gson()
    private val pendingRequests = mutableMapOf<String, CompletableDeferred<JsonObject>>()

    fun writeJson(path: String, content: String) {
        val writeAction = mapOf(
            "actionType" to "file_action",
            "subAction" to "write",
            "path" to path,
            "content" to content
        )
        GlobalVars.fileActions?.getConnection()?.sendAction(writeAction)
            ?: logAndDie("Write to Json failed")
    }

    suspend fun readJson(path: String): String? {
        val requestId = UUID.randomUUID().toString()
        val deferred = CompletableDeferred<JsonObject>()
        pendingRequests[requestId] = deferred

        val readAction = mapOf(
            "actionType" to "file_action",
            "subAction" to "read",
            "path" to path,
            "requestId" to requestId
        )
        GlobalVars.fileActions?.getConnection()?.sendAction(readAction)
            ?: logAndDie("Read Json failed")

        return try {
            val response = withTimeout(5000L) {
                deferred.await()
            }
            pendingRequests.remove(requestId)
            if (response.has("status") && response.get("status").asString == "success") {
                response.get("data")?.asString
            } else {
                null
            }
        } catch (e: Exception) {
            pendingRequests.remove(requestId)
            null
        }
    }

    suspend fun checkExists(path: String): Boolean {
        val requestId = UUID.randomUUID().toString()
        val deferred = CompletableDeferred<JsonObject>()
        pendingRequests[requestId] = deferred

        val checkExistsAction = mapOf(
            "actionType" to "file_action",
            "subAction" to "check_exists",
            "path" to path,
            "requestId" to requestId
        )
        GlobalVars.fileActions?.getConnection()?.sendAction(checkExistsAction)
            ?: logAndDie("Check exists failed")

        return try {
            val response = withTimeout(5000L) {
                deferred.await()
            }
            pendingRequests.remove(requestId)
            response.has("status") && response.get("status").asString == "success"
        } catch (e: Exception) {
            pendingRequests.remove(requestId)
            false
        }
    }

    fun handleResponse(response: JsonObject) {
        if (response.has("requestId")) {
            val requestId = response.get("requestId").asString
            pendingRequests[requestId]?.complete(response)
        }
    }

}
