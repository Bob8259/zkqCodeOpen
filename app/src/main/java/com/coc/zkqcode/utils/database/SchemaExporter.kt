package com.coc.zkqcode.utils.database

import com.coc.zkqcode.utils.components.GlobalVars
import com.coc.zkqcode.utils.database.SchemaExporter.DEFAULT_KEYS
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject

object SchemaExporter {

    private val DEFAULT_KEYS = listOf(
        "GLOBAL_SETTINGS",
        "ACCOUNT_SETTINGS",
        "MAIN_BASE_SETTINGS",
        "MAIN_BASE_TROOPS_AND_SPELLS",
        "MAIN_BASE_BUILDINGS",
        "MAIN_BASE_PETS",
        "NIGHT_BASE_SETTINGS",
        "NIGHT_BASE_TROOPS"
    )

    /**
     * Exports the specified Schema definitions to a JSON string (key-value format)
     * @param keys List of module Keys to export. If empty, uses [DEFAULT_KEYS].
     * @param accountCount Number of accounts, used for exporting account configurations
     * @param configCount Number of configuration profiles
     */
    fun exportSchemasToJson(
        keys: List<String> = DEFAULT_KEYS,
        accountCount: Int = 0,
        configCount: Int = 0
    ): String {
        val jsonObject = JsonObject()

        // Define all mapping relationships
        val sourceMap = mapOf(
            "GLOBAL_SETTINGS" to Schema.GLOBAL_SETTINGS,
            "ACCOUNT_SETTINGS" to Schema.ACCOUNT_SETTINGS,
            "MAIN_BASE_SETTINGS" to Schema.MAIN_BASE_SETTINGS,
            "MAIN_BASE_TROOPS_AND_SPELLS" to Schema.MAIN_BASE_TROOPS_AND_SPELLS,
            "MAIN_BASE_PETS" to Schema.MAIN_BASE_PETS,
            "MAIN_BASE_BUILDINGS" to Schema.MAIN_BASE_BUILDINGS,
            "NIGHT_BASE_SETTINGS" to Schema.NIGHT_BASE_SETTINGS,
            "NIGHT_BASE_TROOPS" to Schema.NIGHT_BASE_TROOPS
        )

        // 1. Export base schemas
        val schemasToExport = if (keys.isEmpty()) {
            sourceMap.values.flatten()
        } else {
            keys.flatMap { key -> sourceMap[key] ?: emptyList() }
        }

        schemasToExport.forEach { settingDef ->
            jsonObject.addProperty(
                settingDef.key,
                getCurrentValue(settingDef.key, settingDef.defaultValue)
            )
        }

        // 2. Export account-specific configurations
        if (keys.contains("ACCOUNT_SETTINGS") && accountCount > 0) {
            for (i in 1..accountCount) {
                Schema.ACCOUNT_SETTINGS.forEach { settingDef ->
                    val suffixedKey = "${settingDef.key}$i"
                    jsonObject.addProperty(
                        suffixedKey,
                        getCurrentValue(suffixedKey, settingDef.defaultValue)
                    )
                }
            }
        }

        // 3. Export profile-specific configurations (Main Base)
        val profileKeys = listOf(
            "MAIN_BASE_SETTINGS",
            "MAIN_BASE_TROOPS_AND_SPELLS",
            "MAIN_BASE_BUILDINGS",
            "MAIN_BASE_PETS",
            "NIGHT_BASE_SETTINGS",
            "NIGHT_BASE_TROOPS",
        )
        if (configCount > 0) {
            profileKeys.filter { keys.contains(it) }.forEach { schemaKey ->
                sourceMap[schemaKey]?.let { schema ->
                    for (i in 1..configCount) {
                        schema.forEach { settingDef ->
                            val suffixedKey = "${settingDef.key}_c$i"
                            jsonObject.addProperty(
                                suffixedKey,
                                getCurrentValue(suffixedKey, settingDef.defaultValue)
                            )
                        }
                    }
                }
            }
        }

        return GsonBuilder().setPrettyPrinting().create().toJson(jsonObject)
    }

    /**
     * Helper to retrieve current value from priority sources:
     * 1. UI configStates (live data)
     * 2. fileActions (previously saved)
     * 3. Default value (fallback)
     */
    private fun getCurrentValue(key: String, defaultValue: Any): String {
        return GlobalVars.configStates[key]?.value
            ?: GlobalVars.fileActions?.getValue(key)
            ?: defaultValue.toString()
    }

    /**
     * Notify the server to write the file via WebSocket
     * @param directory Directory path to save the file
     * @param fileName Name of the file to save
     * @param keys List of module Keys to export
     * @param accountCount Number of accounts
     * @param configCount Number of configuration profiles
     */
    fun saveSchemaViaServer(
        directory: String,
        fileName: String,
        keys: List<String> = DEFAULT_KEYS,
        accountCount: Int = 0,
        configCount: Int = 0
    ) {
        val jsonContent = exportSchemasToJson(keys, accountCount, configCount)
        val fullPath =
            if (directory.endsWith("/")) "$directory$fileName" else "$directory/$fileName"

        val writeAction = mapOf(
            "actionType" to "file_action",
            "subAction" to "write",
            "path" to fullPath,
            "content" to jsonContent
        )

        GlobalVars.fileActions?.let { actions ->
            actions.getConnection().sendAction(writeAction)
            // Update local configJson to keep it in sync
            try {
                val gson = com.google.gson.Gson()
                val newJson = gson.fromJson(jsonContent, JsonObject::class.java)
                actions.updateConfig(newJson)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } ?: println("ERROR: GlobalVars.fileActions is null")
    }
}