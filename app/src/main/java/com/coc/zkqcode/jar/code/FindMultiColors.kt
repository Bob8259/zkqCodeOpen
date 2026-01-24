package com.coc.zkqcode.jar.code

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.util.Log
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import kotlinx.coroutines.*
import kotlin.math.abs

class FindMultiColors {
    private val screenShot = ScreenShot()

    /**
     * Finds the first occurrence of a multi-color schema in the bitmap.
     * @param bitmap The screenshot to search in.
     * @param schema The color schema to look for.
     * @return The Point where the main color was found, or null if not found.
     */
    fun findMultiColors(bitmap: Bitmap, schema: ColorSchema): Point? {
        val x1 = schema.x1
        val y1 = schema.y1
        val x2 = schema.x2
        val y2 = schema.y2
        val mainColor = schema.mainColor
        val threshold = schema.threshold
        val offsets = schema.offsets ?: emptyList()

        // Iterate through the search area
        for (y in y1..y2) {
            if (y >= bitmap.height) continue
            for (x in x1..x2) {
                if (x >= bitmap.width) continue
                
                val pixel = bitmap.getPixel(x, y)
                if (isColorMatch(pixel, mainColor, threshold)) {
                    // Main color matched, now check offsets
                    var allOffsetsMatch = true
                    for (offset in offsets) {
                        if (offset == null) continue
                        val targetX = x + offset.dx
                        val targetY = y + offset.dy
                        
                        // Check bounds
                        if (targetX < 0 || targetX >= bitmap.width || targetY < 0 || targetY >= bitmap.height) {
                            allOffsetsMatch = false
                            break
                        }
                        
                        val offsetPixel = bitmap.getPixel(targetX, targetY)
                        if (!isColorMatch(offsetPixel, offset.color, threshold)) {
                            allOffsetsMatch = false
                            break
                        }
                    }
                    
                    if (allOffsetsMatch) {
                        return Point(x, y)
                    }
                }
            }
        }
        return null
    }

    /**
     * Checks if two colors match within a certain threshold (absolute difference per channel).
     */
    private fun isColorMatch(c1: Int, c2: Int, threshold: Int): Boolean {
        val r1 = Color.red(c1)
        val g1 = Color.green(c1)
        val b1 = Color.blue(c1)
        
        val r2 = Color.red(c2)
        val g2 = Color.green(c2)
        val b2 = Color.blue(c2)
        
        return abs(r1 - r2) <= threshold &&
               abs(g1 - g2) <= threshold &&
               abs(b1 - b2) <= threshold
    }

    /**
     * Starts a loop that runs every 5 seconds to find the Test color schema.
     */
    fun startDetectionLoop() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("FindMultiColors", "Starting detection loop every 5 seconds...")
            while (isActive) {
                val startTime = System.currentTimeMillis()
                val bitmap = screenShot.takeScreenshot()
                
                if (bitmap != null) {
                    val foundPoint = findMultiColors(bitmap, MyColors.Test)
                    if (foundPoint != null) {
                        Log.d("FindMultiColors", "Match FOUND at: (${foundPoint.x}, ${foundPoint.y})")
                    } else {
                        Log.d("FindMultiColors", "No match found.")
                    }
                    bitmap.recycle()
                } else {
                    Log.e("FindMultiColors", "Failed to capture screenshot.")
                }
                
                val executionTime = System.currentTimeMillis() - startTime
                val remainingDelay = 0L.coerceAtLeast(5000L - executionTime)
                delay(remainingDelay)
            }
        }
    }
}