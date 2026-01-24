package com.coc.zkqcode.jar.code

import android.graphics.Bitmap
import android.graphics.Point
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import kotlin.math.abs

class FindMultiColors {
    private val screenShot = ScreenShot()

    fun findMultiColors(bitmap: Bitmap? = null, schema: ColorSchema): Point? {
        val usedBitmap = bitmap ?: screenShot.takeScreenshot() ?: return null
        val shouldRecycle = bitmap == null

        try {
            val width = usedBitmap.width
            val height = usedBitmap.height

            // 1. 将 Bitmap 像素一次性加载到 IntArray 中 (性能提升的关键)
            val pixels = IntArray(width * height)
            usedBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

            val x1 = schema.x1.coerceAtLeast(0)
            val y1 = schema.y1.coerceAtLeast(0)
            val x2 = schema.x2.coerceAtMost(width - 1)
            val y2 = schema.y2.coerceAtMost(height - 1)

            val mainColor = schema.mainColor
            val threshold = schema.threshold
            val offsets = schema.offsets ?: emptyList()

            // 2. 预解压主颜色的 RGB
            val mr = (mainColor shr 16) and 0xFF
            val mg = (mainColor shr 8) and 0xFF
            val mb = mainColor and 0xFF

            // 3. 开始遍历数组
            for (y in y1..y2) {
                val rowOffset = y * width // 预计算行偏移
                for (x in x1..x2) {
                    val pixel = pixels[rowOffset + x]

                    // 4. 使用位运算快速匹配主色
                    if (fastColorMatch(pixel, mr, mg, mb, threshold)) {

                        var allOffsetsMatch = true
                        for (offset in offsets) {
                            if (offset == null) continue

                            val targetX = x + offset.dx
                            val targetY = y + offset.dy

                            // 边界检查
                            if (targetX !in 0..<width || targetY < 0 || targetY >= height) {
                                allOffsetsMatch = false
                                break
                            }

                            // 5. 同样从数组中直接读取偏移像素
                            val offsetPixel = pixels[targetY * width + targetX]
                            val or = (offset.color shr 16) and 0xFF
                            val og = (offset.color shr 8) and 0xFF
                            val ob = offset.color and 0xFF

                            if (!fastColorMatch(offsetPixel, or, og, ob, threshold)) {
                                allOffsetsMatch = false
                                break
                            }
                        }

                        if (allOffsetsMatch) return Point(x, y)
                    }
                }
            }
        } finally {
            if (shouldRecycle) usedBitmap.recycle()
        }
        return null
    }

    /**
     * 高性能颜色比对：直接传入拆解好的 RGB
     */
    private fun fastColorMatch(pixel: Int, r2: Int, g2: Int, b2: Int, threshold: Int): Boolean {
        val r1 = (pixel shr 16) and 0xFF
        val g1 = (pixel shr 8) and 0xFF
        val b1 = pixel and 0xFF

        return abs(r1 - r2) <= threshold &&
                abs(g1 - g2) <= threshold &&
                abs(b1 - b2) <= threshold
    }
}