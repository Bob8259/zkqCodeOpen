package com.coc.zkqcode.utils.database

import androidx.compose.runtime.MutableState
import com.coc.zkqcode.utils.components.GlobalVars
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject

object SchemaExporter {

    /**
     * Exports the specified Schema definitions to a JSON string (key-value format)
     * @param keys List of module Keys to export. If empty, exports all by default.
     * @param accountCount Number of accounts, used for exporting account configurations
     * @param configStates States maintained by the UI layer; if provided, priority is given to fetching the latest values from here
     */
    fun exportSchemasToJson(
        keys: List<String> = emptyList(),
        accountCount: Int = 0,
        configCount: Int = 0,
        configStates: Map<String, MutableState<String>>? = null
    ): String {
        val jsonObject = JsonObject()

        // Define all mapping relationships
        val sourceMap = mapOf(
            "GLOBAL_SETTINGS" to Schema.GLOBAL_SETTINGS,
            "ACCOUNT_SETTINGS" to Schema.ACCOUNT_SETTINGS,
            "MAIN_BASE_SETTINGS" to Schema.MAIN_BASE_SETTINGS,
            "MAIN_BASE_TROOPS_AND_SPELLS" to Schema.MAIN_BASE_TROOPS_AND_SPELLS,
            "MAIN_BASE_PETS" to Schema.MAIN_BASE_PETS,
            "MAIN_BASE_BUILDINGS" to Schema.MAIN_BASE_BUILDINGS
        )

        val schemasToExport = if (keys.isEmpty()) {
            sourceMap.values.flatten()
            
        } else {
            keys.flatMap { key -> sourceMap[key] ?: emptyList() }
        }

        // Convert each SettingDef to key-value pair

        schemasToExport.forEach { settingDef ->
            // Prioritize fetching from configStates, then GlobalVars.fileActions, and finally use the default value
            val currentValue = configStates?.get(settingDef.key)?.value
                ?: GlobalVars.fileActions?.getValue(settingDef.key)
                ?: settingDef.defaultValue.toString()

            if (settingDef.key == "gem_count")
                println("gem_count value:$currentValue")
            jsonObject.addProperty(settingDef.key, currentValue)
        }

        // If account configurations need to be exported
        if (keys.contains("ACCOUNT_SETTINGS") && accountCount > 0) {
            for (i in 1..accountCount) {
                Schema.ACCOUNT_SETTINGS.forEach { settingDef ->
                    val key = "${settingDef.key}${i}"
                    val currentValue = configStates?.get(key)?.value
                        ?: GlobalVars.fileActions?.getValue(key)
                        ?: settingDef.defaultValue.toString()
                    jsonObject.addProperty(key, currentValue)
                }
            }
        }

        // If config Main Base settings need to be exported
        if (keys.contains("MAIN_BASE_SETTINGS") && configCount > 0) {
            for (i in 1..configCount) {
                Schema.MAIN_BASE_SETTINGS.forEach { settingDef ->
                    val key = "${settingDef.key}_c$i"
                    val currentValue = configStates?.get(key)?.value
                        ?: GlobalVars.fileActions?.getValue(key)
                        ?: settingDef.defaultValue.toString()
                    jsonObject.addProperty(key, currentValue)
                }
            }
        }

        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(jsonObject)
    }

    /**
     * Notify the server to write the file via WebSocket
     * @param accountCount Number of accounts, used for exporting account configurations
     * @param configStates States maintained by the UI layer
     */
    fun saveSchemaViaServer(
        directory: String,
        fileName: String,
        keys: List<String> = emptyList(),
        accountCount: Int = 0,
        configCount: Int = 0,
        configStates: Map<String, MutableState<String>>? = null
    ) {

        val jsonContent = exportSchemasToJson(keys, accountCount, configCount, configStates)
        val fullPath =
            if (directory.endsWith("/")) "$directory$fileName" else "$directory/$fileName"

        val writeAction = mapOf(
            "actionType" to "file_action",
            "subAction" to "write",
            "path" to fullPath,
            "content" to jsonContent
        )

        val connection = GlobalVars.fileActions?.getConnection()
        if (connection != null) {
            connection.sendAction(writeAction)
            // Update local configJson to keep it in sync
            try {
                val gson = com.google.gson.Gson()
                val newJson = gson.fromJson(jsonContent, JsonObject::class.java)
                GlobalVars.fileActions?.updateConfig(newJson)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            println("ERROR: ServerConnection 为空，请检查初始化")
        }
    }
}