package com.coc.zkqcode.core.util.fileactions

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.google.gson.Gson
import com.google.gson.JsonObject


object FileHelper {
    private val gson = Gson()

    suspend fun writeJson(path: String, content: String): Boolean {
        val serverActions = GlobalVars.serverActions ?: logAndStop("Server actions not found")
        val writeAction = mapOf(
            "actionType" to "file_action",
            "subAction" to "write",
            "path" to path,
            "content" to content
        )
        val response = serverActions.sendActionSync(writeAction)
        return response?.has("status") == true && response.get("status").asString == "success"
    }

    suspend fun readJson(path: String): JsonObject? {
        val serverActions = GlobalVars.serverActions ?: logAndStop("Server actions not found")
        val readAction = mapOf(
            "actionType" to "file_action",
            "subAction" to "read",
            "path" to path
        )
        val response = serverActions.sendActionSync(readAction)
        return if (response != null && response.has("status") && response.get("status").asString == "success") {
            val data = response.get("data")?.asString
            if (data != null && data.trim().startsWith("{") && data.trim().endsWith("}")) {
                gson.fromJson(data, JsonObject::class.java)
            } else {
                null
            }
        } else {
            null
        }
    }

    suspend fun checkExists(path: String): Boolean {
        val serverActions = GlobalVars.serverActions ?: logAndStop("Server actions not found")
        val checkExistsAction = mapOf(
            "actionType" to "file_action",
            "subAction" to "check_exists",
            "path" to path
        )
        val response = serverActions.sendActionSync(checkExistsAction)
        return response?.has("status") == true && response.get("status").asString == "success"
    }
}
