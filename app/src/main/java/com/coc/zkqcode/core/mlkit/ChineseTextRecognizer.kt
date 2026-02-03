package com.coc.zkqcode.core.mlkit

import android.graphics.Bitmap
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions

object ChineseTextRecognizer {

    /**
     * Recognizes Chinese characters and digits from the given [bitmap].
     * Prints detected text and its position to the console.
     */
    fun recognizeChineseText(bitmap: Bitmap) {
        // 1. 设置识别器，指定使用中文选项
        val recognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())

        // 2. 准备输入图像
        val image = InputImage.fromBitmap(bitmap, 0)

        // 3. 执行识别
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // 如果你想按行或按块处理（更精准地定位数字）
                for (block in visionText.textBlocks) {
                    showDebugInfo("--- 文本块 ---")
                    showDebugInfo("内容: ${block.text}")
                    showDebugInfo("位置: ${block.boundingBox}")

                    for (line in block.lines) {
                        showDebugInfo("发现文本行: ${line.text}")
                        showDebugInfo("行位置: ${line.boundingBox}")
                    }
                }
            }
            .addOnFailureListener { e ->
                // 识别失败
                showDebugInfo("识别失败: ${e.message}")
                e.printStackTrace()
            }
    }
}
