package com.coc.zkqcode.utils.database

import android.os.Environment
import androidx.compose.runtime.mutableStateOf
import com.coc.zkqcode.utils.components.GlobalVars
import com.coc.zkqcode.utils.fileactions.FileActions
import com.coc.zkqcode.utils.state.AppMode
import com.coc.zkqcode.utils.state.AppStateManager

object ConfigManager {

    /**
     * Initializes all config states from the provided FileActions.
     */
    fun initializeAllConfigs(actions: FileActions) {
        // 1. GLOBAL_SETTINGS
        Schema.GLOBAL_SETTINGS.all.forEach { def ->
            val savedValue = actions.getValue(def.key)
            GlobalVars.configStates.getOrPut(def.key) {
                mutableStateOf(savedValue ?: def.defaultValue.toString())
            }.value = savedValue ?: def.defaultValue.toString()
        }

        // 2. ACCOUNT_SETTINGS
        val accountCount = actions.getValue("account_count")?.toIntOrNull() ?: 3
        for (i in 1..accountCount) {
            Schema.ACCOUNT_SETTINGS.all.forEach { def ->
                val key = "${def.key}${i}"
                val savedValue = actions.getValue(key)
                val defaultValue = if (def.key == "global_path" || def.key == "cn_path" || def.key == "data_content") {
                    i.toString()
                } else {
                    def.defaultValue.toString()
                }
                GlobalVars.configStates.getOrPut(key) {
                    mutableStateOf(savedValue ?: defaultValue)
                }.value = savedValue ?: defaultValue
            }
        }

        // 3. Profile Settings (MAIN_BASE, NIGHT_BASE, etc.)
        val configCount = actions.getValue("config_count")?.toIntOrNull() ?: 3
        val profileSchemas = listOf(
            Schema.MAIN_BASE_SETTINGS.all,
            Schema.MAIN_BASE_TROOPS_AND_SPELLS.all,
            Schema.MAIN_BASE_BUILDINGS.all,
            Schema.MAIN_BASE_PETS.all,
            Schema.NIGHT_BASE_SETTINGS.all,
            Schema.NIGHT_BASE_TROOPS.all,
            Schema.MAIN_BASE_BUILDING_PRIORITIES.all
        )

        for (i in 1..configCount) {
            profileSchemas.forEach { schemaList ->
                schemaList.forEach { def ->
                    val key = "${def.key}_c$i"
                    val savedValue = actions.getValue(key)
                    GlobalVars.configStates.getOrPut(key) {
                        mutableStateOf(savedValue ?: def.defaultValue.toString())
                    }.value = savedValue ?: def.defaultValue.toString()
                }
            }
        }
    }

    /**
     * Saves all configs to the JSON file via the server.
     */
    fun saveConfigs(onSaveSuccess: () -> Unit = {}) {
        val baseDir = "${Environment.getExternalStorageDirectory().path}/zkqFiles/"
        val accountCountStr = GlobalVars.configStates["account_count"]?.value ?: "3"
        val configCountStr = GlobalVars.configStates["config_count"]?.value ?: "3"
        
        val accountCount = accountCountStr.toIntOrNull() ?: 3
        val configCount = configCountStr.toIntOrNull() ?: 3

        SchemaExporter.saveSchemaViaServer(
            baseDir,
            "zkq_config.json",
            accountCount = accountCount,
            configCount = configCount
        )

        GlobalVars.updateWindowPosition = true
        onSaveSuccess()
    }

    /**
     * Save configs and run the bot.
     */
    fun saveAndRun(onSaveSuccess: () -> Unit = {}) {
        AppStateManager.setMode(AppMode.Run)
        saveConfigs(onSaveSuccess)
    }
}
