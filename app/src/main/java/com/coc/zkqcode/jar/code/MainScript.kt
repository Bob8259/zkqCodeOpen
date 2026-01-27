package com.coc.zkqcode.jar.code

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.fileactions.FileHelper.readJson
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.google.gson.JsonObject
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


class MainScript {
    private val findTool = FindMultiColors()
    internal var localMemory: JsonObject? = null
    internal lateinit var currentAccountNumber :Int
    internal lateinit var currentGamePackage:String
    suspend fun runMainScript() {
        localMemory = readJson("/sdcard/zkqFiles/memory.json")
        while (currentCoroutineContext().isActive) {//The Main Loop
            currentAccountNumber = localMemory?.get("accountNumber")?.asInt ?: 1

            enterMainScreen(currentAccountNumber)
//            val startTime = System.currentTimeMillis()
//            val foundPoint = findTool.findMultiColors(null, MyColors.Test)
//            val executionTime = System.currentTimeMillis() - startTime
//            if (foundPoint != null) {
//                ShowMessage("Match FOUND 123: (${foundPoint.x}, ${foundPoint.y})\nTime consumed: $executionTime")
//            } else {
//                ShowMessage("没找到.\nTime consumed: $executionTime")
//            }
//            val remainingDelay = 0L.coerceAtLeast(2000L - executionTime)
//            delay(remainingDelay)
        }
    }
}
