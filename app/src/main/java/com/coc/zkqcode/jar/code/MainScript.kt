package com.coc.zkqcode.jar.code

import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.utils.basic.ShowMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class MainScript {
    private val findTool = FindMultiColors()

    fun runScript() {

        CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                val startTime = System.currentTimeMillis()
                println("start")
                val foundPoint = findTool.findMultiColors(null, MyColors.Test)
                println("end")
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
}
