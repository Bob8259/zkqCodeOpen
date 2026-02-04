package com.coc.zkqcode.jar.code.universal.recognizer

import android.graphics.Bitmap
import android.graphics.Rect
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Data class representing recognized text and its position.
 */
data class RecognizedText(
    val text: String,
    val position: Rect?
)

/**
 * Object for universal text recognition tasks within the application.
 */
object TextRecognizer {

    /**
     * Captures the screen, crops the specified area, and recognizes text within it.
     *
     * @param startX Starting X coordinate of the crop area.
     * @param startY Starting Y coordinate of the crop area.
     * @param endX Ending X coordinate of the crop area.
     * @param endY Ending Y coordinate of the crop area.
     * @param useChinese Whether to use the Chinese text recognition model. Defaults to true.
     * @return A list of [RecognizedText] found in the area.
     */
    suspend fun recognize(startX: Int, startY: Int, endX: Int, endY: Int, useChinese: Boolean = true): List<RecognizedText> {
        val screenBuffer = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
            ?: logAndStop("in TextRecognizer, screen capture failed.")

        val width = endX - startX
        val height = endY - startY

        if (width <= 0 || height <= 0) {
            logAndStop("Invalid crop area: width=$width, height=$height")
        }
        while (!GlobalVars.isPlaying.value) {
            delay(1000)//the user paused the script, then we should also stop
        }
        try {
            // Ensure crop area is within bitmap bounds
            if (startX + width <= screenBuffer.width && startY + height <= screenBuffer.height) {
                val croppedBitmap = Bitmap.createBitmap(screenBuffer, startX, startY, width, height)

                // Perform recognition synchronously in a background thread
                return recognizeTextSync(croppedBitmap, useChinese)
            } else {
                return emptyList()
            }
        } catch (e: Exception) {
            logAndStop("Error during cropping or recognition: ${e.message}")
        }
    }

    /**
     * Performs text recognition on a bitmap synchronously.
     */
    private suspend fun recognizeTextSync(bitmap: Bitmap, useChinese: Boolean): List<RecognizedText> = withContext(Dispatchers.IO) {
        val options = if (useChinese) {
            ChineseTextRecognizerOptions.Builder().build()
        } else {
            TextRecognizerOptions.DEFAULT_OPTIONS
        }
        val recognizer = TextRecognition.getClient(options)
        val image = InputImage.fromBitmap(bitmap, 0)

        try {
            val visionText = Tasks.await(recognizer.process(image))
            val result = mutableListOf<RecognizedText>()
            for (block in visionText.textBlocks) {
                for (line in block.lines) {
                    result.add(RecognizedText(line.text, line.boundingBox))
                }
            }
            result
        } catch (e: Exception) {
            showDebugInfo("UniversalTextRecognizer recognition failed: ${e.message}")
            emptyList()
        }
    }
}
