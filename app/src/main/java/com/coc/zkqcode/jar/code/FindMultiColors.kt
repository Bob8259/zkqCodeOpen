package com.coc.zkqcode.jar.code

import android.graphics.Bitmap
import android.graphics.Point
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.zkqnative.NativeTools

class FindMultiColors {
    private val screenShot = ScreenShot()

    /**
     * Finds the first occurrence of a multi-color schema in the bitmap using native code for performance.
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
            // Flatten the offsets list into an IntArray: [dx, dy, color, dx, dy, color...]
            val flatOffsets = mutableListOf<Int>()
            schema.offsets?.forEach {
                if (it != null) {
                    flatOffsets.add(it.dx)
                    flatOffsets.add(it.dy)
                    flatOffsets.add(it.color)
                }
            }

            val result = NativeTools.nativeFindMultiColors(
                usedBitmap,
                schema.x1, schema.y1, schema.x2, schema.y2,
                schema.mainColor,
                schema.threshold,
                flatOffsets.toIntArray()
            )

            if (result != null && result.size == 2) {
                return Point(result[0], result[1])
            }
        } finally {
            if (shouldRecycle) {
                usedBitmap.recycle()
            }
        }
        return null
    }
}
