package com.coc.zkqcode.jar.code.universal.smalltools.data

import android.os.Environment
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.fileactions.FileHelper
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.ui.schema.details.GlobalSettings
import com.google.gson.JsonObject

const val CLAN_GAME_TASK_BASE_NONE = "NONE"
const val CLAN_GAME_TASK_BASE_MAIN_WORLD = "MAIN_WORLD"
const val CLAN_GAME_TASK_BASE_BUILDER_BASE = "BUILDER_BASE"

private val sdPath = Environment.getExternalStorageDirectory().path
private val memoryPath = "$sdPath/zkqFiles/memory.json"

suspend fun readMemory(key: String): String {
    val json = FileHelper.readJson(memoryPath)
    return json?.get(key)?.asString ?: ""
}

suspend fun writeMemory(key: String, value: String): Boolean {
    val json = FileHelper.readJson(memoryPath) ?: JsonObject()
    json.addProperty(key, value)
    return FileHelper.writeJson(memoryPath, json.toString())
}

suspend fun readClanGameAcceptedTaskBase(): String {
    val key = StorageKeys.withAccountNumber(StorageKeys.CLAN_GAME_ACCEPTED_TASK_BASE, InGameVars.currentAccountNumber)
    return when (readMemory(key)) {
        CLAN_GAME_TASK_BASE_MAIN_WORLD -> CLAN_GAME_TASK_BASE_MAIN_WORLD
        CLAN_GAME_TASK_BASE_BUILDER_BASE -> CLAN_GAME_TASK_BASE_BUILDER_BASE
        else -> CLAN_GAME_TASK_BASE_NONE
    }
}

suspend fun writeClanGameAcceptedTaskBase(value: String): Boolean {
    val key = StorageKeys.withAccountNumber(StorageKeys.CLAN_GAME_ACCEPTED_TASK_BASE, InGameVars.currentAccountNumber)
    val normalizedValue = when (value) {
        CLAN_GAME_TASK_BASE_MAIN_WORLD -> CLAN_GAME_TASK_BASE_MAIN_WORLD
        CLAN_GAME_TASK_BASE_BUILDER_BASE -> CLAN_GAME_TASK_BASE_BUILDER_BASE
        else -> CLAN_GAME_TASK_BASE_NONE
    }
    return writeMemory(key, normalizedValue)
}

/**
 * Check if enough time has passed since the last recorded timestamp for the given key.
 * Returns true if the interval has elapsed (or no previous record exists), meaning the caller should proceed.
 */
suspend fun checkMemoryFile(key: String, intervalMinutes: Int): Boolean {
    if (GlobalVars.configStates[GlobalSettings.DEBUG_MODE.key]?.value == "1") return true

    val lastMinutes = readMemory(key).toLongOrNull() ?: return true
    val currentMinutes = System.currentTimeMillis() / 60_000
    return (currentMinutes - lastMinutes) >= intervalMinutes
}
