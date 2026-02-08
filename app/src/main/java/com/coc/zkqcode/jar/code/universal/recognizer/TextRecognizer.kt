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
import androidx.core.graphics.createBitmap
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
        useChinese: Boolean = true
    ): List<RecognizedText> {
        val screenBuffer = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
            ?: logAndStop("in TextRecognizer, screen capture failed.")

        val width = endX - startX
        val height = endY - startY

        if (width <= 0 || height <= 0) {
            logAndStop("Invalid crop area: width=$width, height=$height")
        }

        while (!GlobalVars.isPlaying.value) {
            delay(1000)
        }

        try {
            if (startX + width <= screenBuffer.width && startY + height <= screenBuffer.height) {
                // 1. 裁剪原始区域
                val croppedBitmap = Bitmap.createBitmap(screenBuffer, startX, startY, width, height)

                // 2. 【核心优化】应用预处理：灰度化 + 二值化
                // 阈值 140 是根据你 Python 测试效果定的
                val processedBitmap = preprocess(croppedBitmap, threshold = 140)

                // 3. 识别处理后的图片
                return recognizeTextSync(processedBitmap, useChinese)
            } else {
                return emptyList()
            }
        } catch (e: CancellationException) {
            // 重点：如果是取消异常，必须重新抛出，让协程系统处理
            throw e
        } catch (e: Exception) {
            // 这里才处理真正的业务错误（如内存溢出、Bitmap 创建失败等）
            logAndStop("Error during cropping or recognition: ${e.message}")
        }
    }

    /**
     * 图像预处理：灰度化 + 二值化
     * 消除背景干扰，让 ML Kit 更容易识别文字轮廓
     */
    private fun preprocess(src: Bitmap, threshold: Int): Bitmap {
        val width = src.width
        val height = src.height
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val color = pixels[i]
            val r = (color shr 16) and 0xFF
            val g = (color shr 8) and 0xFF
            val b = color and 0xFF

            // 灰度化公式
            val gray = (r * 0.299 + g * 0.587 + b * 0.114).toInt()

            // 二值化：白色背景 0xFFFFFFFF, 黑色文字 0xFF000000
            pixels[i] = if (gray > threshold) -0x1 else -0x1000000
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

            // 这里的 InputImage 接收的是我们处理过的二值化 Bitmap
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