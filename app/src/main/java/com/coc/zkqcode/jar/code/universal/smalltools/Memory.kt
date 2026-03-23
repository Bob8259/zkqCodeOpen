package com.coc.zkqcode.jar.code.universal.smalltools

import android.os.Environment
import com.coc.zkqcode.core.util.fileactions.FileHelper
import com.google.gson.JsonObject

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

/**
 * Check if enough time has passed since the last recorded timestamp for the given key.
 * Returns true if the interval has elapsed (or no previous record exists), meaning the caller should proceed.
 */
suspend fun checkMemoryFile(key: String, intervalMinutes: Int): Boolean {
    val lastMinutes = readMemory(key).toLongOrNull() ?: return true
    val currentMinutes = System.currentTimeMillis() / 60_000
    return (currentMinutes - lastMinutes) >= intervalMinutes
}
