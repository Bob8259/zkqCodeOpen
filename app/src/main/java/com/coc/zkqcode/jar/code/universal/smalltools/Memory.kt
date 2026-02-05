package com.coc.zkqcode.jar.code.universal.smalltools

import android.os.Environment
import com.coc.zkqcode.core.util.fileactions.FileHelper
import com.google.gson.JsonObject

//Memory list
//
//MainBaseTrainTroops${InGamesVars.currentAccountNumber}
//NightBaseRemoveObstacles${InGamesVars.currentAccountNumber}
//NightBaseTrainTroops${InGamesVars.currentAccountNumber}

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
