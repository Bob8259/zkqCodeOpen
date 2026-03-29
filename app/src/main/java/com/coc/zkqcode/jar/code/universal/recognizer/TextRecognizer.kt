package com.coc.zkqcode.jar.code.universal.recognizer

import android.graphics.Bitmap
import android.graphics.Rect
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.waitForPlay
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.graphics.createBitmap
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.cancellation.CancellationException

data class RecognizedText(
    val text: String,
    val position: Rect?
)

object TextRecognizer {

    suspend fun recognize(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        useChinese: Boolean = true,
        threshold: Int = 140,
        saveImage: Boolean = false,
        applyPreprocess: Boolean = true,
        invertBinarization: Boolean = true
    ): List<RecognizedText> {
        val screenBuffer = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
            ?: logAndRestart("in TextRecognizer, screen capture failed.")

        val width = endX - startX
        val height = endY - startY

        if (width <= 0 || height <= 0) {
            logAndRestart("Invalid crop area: width=$width, height=$height")
        }

        waitForPlay()

        try {
            if (startX + width <= screenBuffer.width && startY + height <= screenBuffer.height) {
                // 1. Crop the original region
                val croppedBitmap = Bitmap.createBitmap(screenBuffer, startX, startY, width, height)

                // 2. [Core Optimization] Apply preprocessing (optional)
                val bitmapToRecognize = if (applyPreprocess) {
                    preprocess(croppedBitmap, threshold = threshold, invertBinarization = invertBinarization)
                } else {
                    croppedBitmap
                }

                // 2.1 Save the image if requested
                if (saveImage) {
                    ScreenCaptureManager.getContext()?.let { context ->
                        try {
                            val file = File(context.filesDir, "test.png")
                            FileOutputStream(file).use { out ->
                                bitmapToRecognize.compress(Bitmap.CompressFormat.PNG, 100, out)
                            }
                            showDebugInfo("Image saved to: ${file.absolutePath}")
                        } catch (e: Exception) {
                            showDebugInfo("Failed to save image: ${e.message}")
                        }
                    }
                }

                // 3. Recognize the image
                return recognizeTextSync(bitmapToRecognize, useChinese)
            } else {
                return emptyList()
            }
        } catch (e: CancellationException) {
            // Important: if it's a cancellation exception, must rethrow it for coroutine system to handle
            throw e
        } catch (e: Exception) {
            // Handle actual business errors here (e.g., out of memory, Bitmap creation failure, etc.)
            logAndRestart("Error during cropping or recognition: ${e.message}")
        }
    }

    /**
     * Image preprocessing: grayscale + binarization
     * Eliminates background interference, making it easier for ML Kit to recognize text contours
     */
    internal fun preprocess(src: Bitmap, threshold: Int, invertBinarization: Boolean): Bitmap {
        val width = src.width
        val height = src.height
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val color = pixels[i]
            val r = (color shr 16) and 0xFF
            val g = (color shr 8) and 0xFF
            val b = color and 0xFF

            // Grayscale conversion formula
            val gray = (r * 0.299 + g * 0.587 + b * 0.114).toInt()

            // Binarization: swap foreground/background when invertBinarization is true
            pixels[i] = if (gray > threshold) {
                if (invertBinarization) -0x1000000 else -0x1
            } else {
                if (invertBinarization) -0x1 else -0x1000000
            }
        }

        val out = createBitmap(width, height)
        out.setPixels(pixels, 0, width, 0, 0, width, height)
        return out
    }

    private suspend fun recognizeTextSync(bitmap: Bitmap, useChinese: Boolean): List<RecognizedText> =
        withContext(Dispatchers.IO) {
            val options = if (useChinese) {
                ChineseTextRecognizerOptions.Builder().build()
            } else {
                TextRecognizerOptions.DEFAULT_OPTIONS
            }
            val recognizer = TextRecognition.getClient(options)

            // The InputImage here receives our processed binarized Bitmap
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