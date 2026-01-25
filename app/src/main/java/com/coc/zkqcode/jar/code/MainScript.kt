package com.coc.zkqcode.jar.code

import android.util.Log
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.utils.basic.ShowMessage
import kotlinx.coroutines.*


class MainScript {
    private val findTool = FindMultiColors()

    fun runScript(){
        CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                val startTime = System.currentTimeMillis()

                val foundPoint = findTool.findMultiColors(null, MyColors.Test)


                val executionTime = System.currentTimeMillis() - startTime
                if (foundPoint != null) {
                    Log.d("zkq_debug", "Match FOUND at: (${foundPoint.x}, ${foundPoint.y})")
                    ShowMessage
                } else {
                    Log.d("zkq_debug", "No match found.")
                }
                val remainingDelay = 0L.coerceAtLeast(2000L - executionTime)
                delay(remainingDelay)
            }
        }
    }
}
