package com.coc.zkqcode.jar.code

import android.graphics.Bitmap
import android.graphics.Point
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.utils.screencapture.ScreenCaptureManager
import com.coc.zkqcode.zkqnative.NativeTools

class FindMultiColors {

    /**
     * Finds the first occurrence of a multi-color schema in the bitmap using native code for performance.
     * @param bitmap The screenshot to search in. If null, a new screenshot will be taken using ByteBuffer for speed.
     * @param schema The color schema to look for.
     * @return The Point where the main color was found, or null if not found.
     */
    suspend fun findMultiColors(bitmap: Bitmap? = null, schema: ColorSchema): Point? {
        val resultAny = if (bitmap == null) {
            ScreenCaptureManager.capture(asBitmap = false)
        } else {
            null
        }

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
            val offsetsArray = flatOffsets.toIntArray()

            if (resultAny is ScreenCaptureManager.CaptureResult) {
                val buf = resultAny.buffer
                val w = resultAny.width
                val h = resultAny.height
                val stride = resultAny.rowStride

                val result = NativeTools.nativeFindMultiColorsRaw(
                    buf,
                    w, h, stride,
                    schema.x1, schema.y1, schema.x2, schema.y2,
                    schema.mainColor,
                    schema.threshold,
                    offsetsArray
                )

                if (result != null && result.size == 2) {
                    return Point(result[0], result[1])
                }

            } else {
                // Fallback or explicit Bitmap provided
                // Use the original native function for Bitmap
                val useBmp = bitmap ?: (resultAny as? Bitmap)
                if (useBmp != null) {
                   val result = NativeTools.nativeFindMultiColors(
                        useBmp,
                        schema.x1, schema.y1, schema.x2, schema.y2,
                        schema.mainColor,
                        schema.threshold,
                        offsetsArray
                    )
                     if (result != null && result.size == 2) {
                        return Point(result[0], result[1])
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // No explicit recycle needed for ByteBuffer as it is GC'd (direct buffer).
        // If we created a Bitmap from capture(true) (which we don't anymore by default), we would need recycle.
        
        return null
    }
}
