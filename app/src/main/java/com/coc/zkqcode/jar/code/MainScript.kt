package com.coc.zkqcode.jar.code

import android.util.Log
import com.coc.zkqcode.jar.code.colorschema.MyColors
import kotlinx.coroutines.*


class MainScript {
    private val findTool = FindMultiColors()

    fun runScript(){
//        CoroutineScope(Dispatchers.IO).launch {
//            Log.d("MainScript", "Starting detection loop every 5 seconds...")
//            while (isActive) {
//                val startTime = System.currentTimeMillis()
//
//                val foundPoint = findTool.findMultiColors(null, MyColors.Test)
//                if (foundPoint != null) {
//                    Log.d("MainScript", "Match FOUND at: (${foundPoint.x}, ${foundPoint.y})")
//                } else {
//                    Log.d("MainScript", "No match found.")
//                }
//
//                val executionTime = System.currentTimeMillis() - startTime
//                println("execution time $executionTime")
//                val remainingDelay = 0L.coerceAtLeast(5000L - executionTime)
//                delay(remainingDelay)
//            }
//        }
    }
}
