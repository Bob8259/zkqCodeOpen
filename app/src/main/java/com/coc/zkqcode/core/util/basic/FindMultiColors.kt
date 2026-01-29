package com.coc.zkqcode.core.util.basic

import android.graphics.Bitmap
import android.graphics.Point
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.nativehelper.RustTools
import kotlinx.coroutines.delay


/**
 * Finds the first occurrence of a multi-color schema in the bitmap using native code for performance.
 * @param bitmap The screenshot to search in. If null, a new screenshot will be taken using ByteBuffer for speed.
 * @param schema The color schema to look for.
 * @return The Point where the main color was found, or null if not found.
 */
suspend fun findMultiColors(
    bitmap: Bitmap? = null,
    byteBuffer: ScreenCaptureManager.CaptureResult? = null,
    schema: ColorSchema
): Point? {
    while (!GlobalVars.isPlaying.value) {
        delay(1000)//the user paused the script, then we should also stop
    }

    val resultAny = when {
        bitmap != null -> null
        byteBuffer != null -> byteBuffer
        else -> ScreenCaptureManager.capture(asBitmap = false)
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

            val result = RustTools.findMultiColorsRaw(
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
                val result = RustTools.findMultiColors(
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

