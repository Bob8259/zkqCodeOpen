package com.coc.zkqcode.core.data.database

import com.coc.zkqcode.core.util.fileactions.FileHelper
import com.google.gson.Gson

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject

object SchemaExporter {

    fun exportSchemasToJson(
        keys: List<String> = SchemaRegistry.ALL_MODULES.map { it.name },
        accountCount: Int = 0,
        configCount: Int = 0
    ): String {
        val jsonObject = JsonObject()

        SchemaRegistry.ALL_MODULES.filter { keys.contains(it.name) }.forEach { module ->
            when (module.scope) {
                Scope.GLOBAL -> {
                    module.settings.forEach { settingDef ->
                        jsonObject.addProperty(
                            settingDef.key,
                            getCurrentValue(settingDef.key, settingDef.defaultValue)
                        )
                    }
                }
                Scope.ACCOUNT -> {
                    if (accountCount > 0) {
                        for (i in 1..accountCount) {
                            module.settings.forEach { settingDef ->
                                val suffixedKey = "${settingDef.key}$i"
                                jsonObject.addProperty(
                                    suffixedKey,
                                    getCurrentValue(suffixedKey, settingDef.defaultValue)
                                )
                            }
                        }
                    }
                }
                Scope.PROFILE -> {
                    if (configCount > 0) {
                        for (i in 1..configCount) {
                            module.settings.forEach { settingDef ->
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
    suspend fun saveSchemaViaServer(
        directory: String,
        fileName: String,
        keys: List<String> = SchemaRegistry.ALL_MODULES.map { it.name },
        accountCount: Int = 0,
        configCount: Int = 0
    ) {
        val jsonContent = exportSchemasToJson(keys = keys, accountCount = accountCount, configCount = configCount)
        val fullPath =
            if (directory.endsWith("/")) "$directory$fileName" else "$directory/$fileName"

        FileHelper.writeJson(fullPath, jsonContent)
        
        // Update local configJson to keep it in sync
        GlobalVars.fileActions?.let { actions ->
            try {
                val gson = Gson()
                val newJson = gson.fromJson(jsonContent, JsonObject::class.java)
                actions.updateConfig(newJson)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}