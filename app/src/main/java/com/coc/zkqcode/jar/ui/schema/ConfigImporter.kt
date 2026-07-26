package com.coc.zkqcode.jar.ui.schema

import androidx.compose.runtime.mutableStateOf
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.fileactions.FileHelper
import com.google.gson.JsonObject

/**
 * Imports configuration from a JSON file into the live configStates,
 * so the UI updates immediately ("在线变化").
 *
 * The imported JSON is expected to be in the same flat key-value format as
 * [SchemaExporter.exportSchemasToJson] produces, e.g.:
 *   { "account_count": "3", "delay_multiplier": "1.0", "enable_c1": "1", … }
 */
object ConfigImporter {

    /**
     * Import configuration from a JSON file at [path].
     *
     * 1. Read the file via [FileHelper.readJson].
     * 2. Update [GlobalVars.configStates] with every matching key.
     * 3. Handle account_count / config_count changes (expand entries if needed).
     *
     * @return The number of keys that were actually imported, or -1 on read failure.
     */
    suspend fun importFromFile(path: String): Int {
        val json = FileHelper.readJson(path) ?: return -1
        return importFromJson(json)
    }

    /**
     * Import configuration from an already-parsed [JsonObject].
     *
     * Visible for testing or programmatic use.
     */
    fun importFromJson(json: JsonObject): Int {
        var importedCount = 0

        // 1) Extract dimension counts first so we can expand before the main loop.
        val importedAccountCount = json.get("account_count")?.asString
        val importedConfigCount = json.get("config_count")?.asString

        if (importedAccountCount != null) {
            val count = importedAccountCount.toIntOrNull()
            if (count != null && count > 0) {
                val clamped = count.coerceAtMost(5000)
                GlobalVars.configStates.getOrPut("account_count") { mutableStateOf("3") }
                    .value = importedAccountCount
                ConfigManager.expandAccountConfigs(clamped)
            }
        }

        if (importedConfigCount != null) {
            val count = importedConfigCount.toIntOrNull()
            if (count != null && count > 0) {
                val clamped = count.coerceAtMost(100)
                GlobalVars.configStates.getOrPut("config_count") { mutableStateOf("3") }
                    .value = importedConfigCount
                ConfigManager.expandProfileConfigs(clamped)
            }
        }

        // 2) Build a set of all valid keys so we know whether a key belongs to the schema.
        val knownKeys = buildKnownKeys()

        // 3) Iterate every entry in the imported JSON.
        for ((key, element) in json.entrySet()) {
            if (key == "account_count" || key == "config_count") {
                continue // already handled above
            }
            val value = element?.asString ?: continue
            if (key !in knownKeys) {
                // Skip keys that do not belong to the current schema (e.g. from an older version).
                continue
            }

            val state = GlobalVars.configStates[key]
            if (state != null) {
                state.value = value
            } else {
                // Key is known but somehow not yet in configStates — create it.
                GlobalVars.configStates[key] = mutableStateOf(value)
            }
            importedCount++
        }

        return importedCount
    }

    /**
     * Build the complete set of all possible config keys based on the current
     * account_count and config_count in memory.
     */
    private fun buildKnownKeys(): Set<String> {
        val keys = mutableSetOf<String>()
        val accountCount =
            (GlobalVars.configStates["account_count"]?.value?.toIntOrNull() ?: 3).coerceAtMost(5000)
        val configCount =
            (GlobalVars.configStates["config_count"]?.value?.toIntOrNull() ?: 3).coerceAtMost(100)

        SchemaRegistry.ALL_MODULES.forEach { module ->
            when (module.scope) {
                Scope.GLOBAL -> {
                    module.settings.forEach { def ->
                        keys.add(def.key)
                    }
                }
                Scope.ACCOUNT -> {
                    for (i in 1..accountCount) {
                        module.settings.forEach { def ->
                            keys.add("${def.key}$i")
                        }
                    }
                }
                Scope.PROFILE -> {
                    for (i in 1..configCount) {
                        module.settings.forEach { def ->
                            keys.add("${def.key}_c$i")
                        }
                    }
                }
            }
        }
        return keys
    }
}
