package com.coc.zkqcode.jar.code

import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.utils.basic.ShowMessage
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


class MainScript {
    private val findTool = FindMultiColors()

    suspend fun runScript() {
        while (currentCoroutineContext().isActive) {
            val startTime = System.currentTimeMillis()
            val foundPoint = findTool.findMultiColors(null, MyColors.Test)
            val executionTime = System.currentTimeMillis() - startTime
            if (foundPoint != null) {
                ShowMessage("Match FOUND at: (${foundPoint.x}, ${foundPoint.y})\nTime consumed: $executionTime")
            } else {
                ShowMessage("No match found.\nTime consumed: $executionTime")
            }
            val remainingDelay = 0L.coerceAtLeast(2000L - executionTime)
            delay(remainingDelay)
        }
    }
}
