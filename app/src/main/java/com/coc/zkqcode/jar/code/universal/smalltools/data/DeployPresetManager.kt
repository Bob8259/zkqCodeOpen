package com.coc.zkqcode.jar.code.universal.smalltools.data

import androidx.compose.runtime.mutableStateOf
import com.coc.zkqcode.core.data.database.GlobalVars
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class DeployPreset(
    val remark: String = "",
    val armyConfigMode: String = "0",
    val armyLink: String = "",
    val deployMode: String = "0",
    val dragSpeed: String = "600",
    val clickDensity: String = "50"
)

object DeployPresetManager {
    private const val STORAGE_KEY = "deploy_presets"
    private val gson = Gson()

    private fun getPresetsJson(): String {
        val state = GlobalVars.configStates.getOrPut(STORAGE_KEY) { mutableStateOf("[]") }
        return state.value
    }

    private fun setPresetsJson(json: String) {
        GlobalVars.configStates.getOrPut(STORAGE_KEY) { mutableStateOf("[]") }.value = json
    }

    fun listPresets(): List<DeployPreset> {
        val json = getPresetsJson()
        if (json.isBlank() || json == "[]") return emptyList()
        return try {
            val type = object : TypeToken<List<DeployPreset>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun savePreset(
        remark: String,
        armyConfigMode: String,
        armyLink: String,
        deployMode: String,
        dragSpeed: String,
        clickDensity: String
    ) {
        val presets = listPresets().toMutableList()
        val existing = presets.indexOfFirst { it.remark == remark }
        val newPreset = DeployPreset(remark, armyConfigMode, armyLink, deployMode, dragSpeed, clickDensity)
        if (existing >= 0) {
            presets[existing] = newPreset
        } else {
            presets.add(newPreset)
        }
        setPresetsJson(gson.toJson(presets))
    }

    fun deletePreset(remark: String) {
        val presets = listPresets().toMutableList()
        presets.removeAll { it.remark == remark }
        setPresetsJson(gson.toJson(presets))
    }

    fun loadPreset(remark: String): DeployPreset? {
        return listPresets().firstOrNull { it.remark == remark }
    }
}
