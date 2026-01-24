package com.coc.zkqcode.jar.code

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import kotlin.math.abs
import androidx.core.graphics.get

class FindMultiColors {
    private val screenShot = ScreenShot()

    /**
     * Finds the first occurrence of a multi-color schema in the bitmap.
     * @param bitmap The screenshot to search in. If null, a new screenshot will be taken.
     * @param schema The color schema to look for.
     * @return The Point where the main color was found, or null if not found.
     */
    fun findMultiColors(bitmap: Bitmap? = null, schema: ColorSchema): Point? {
        var usedBitmap = bitmap
        var shouldRecycle = false
        if (usedBitmap == null) {
            usedBitmap = screenShot.takeScreenshot()
            shouldRecycle = true
        }

        if (usedBitmap == null) return null

        try {
            val x1 = schema.x1
            val y1 = schema.y1
            val x2 = schema.x2
            val y2 = schema.y2
            val mainColor = schema.mainColor
            val threshold = schema.threshold
            val offsets = schema.offsets ?: emptyList()

            // Iterate through the search area
            for (y in y1..y2) {
                if (y >= usedBitmap.height) continue
                for (x in x1..x2) {
                    if (x >= usedBitmap.width) continue

                    val pixel = usedBitmap[x, y]
                    if (isColorMatch(pixel, mainColor, threshold)) {
                        // Main color matched, now check offsets
                        var allOffsetsMatch = true
                        for (offset in offsets) {
                            if (offset == null) continue
                            val targetX = x + offset.dx
                            val targetY = y + offset.dy

                            // Check bounds
                            if (targetX < 0 || targetX >= usedBitmap.width || targetY < 0 || targetY >= usedBitmap.height) {
                                allOffsetsMatch = false
                                break
                            }

                            val offsetPixel = usedBitmap[targetX, targetY]
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
        } finally {
            if (shouldRecycle) {
                usedBitmap.recycle()
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
}
