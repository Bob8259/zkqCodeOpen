package com.coc.zkqcode.jar.code.colorschema

import android.graphics.Color


class ColorSchema(
    val x1: Int, val y1: Int, val x2: Int, val y2: Int, // 已转换为 RGB
    val mainColor: Int, // 由相似度转换而来
    val threshold: Int, val offsets: MutableList<OffsetPoint?>?, val direction: Int
) {
    class OffsetPoint(val dx: Int, val dy: Int, val color: Int)
    companion object {
        /**
         * 核心解析方法
         */
        fun parse(
            x1: Int, y1: Int, x2: Int, y2: Int,
            mainColorStr: String, offsetStr: String?,
            dir: Int, similarity: Double
        ): ColorSchema {
            // 1. 处理主颜色 (BGR -> RGB, 忽略横杠)

            val mainColor = parseBgrToRgb(mainColorStr)

            // 2. 将相似度转换为色差阈值 (0.9 相似度 = 255 * 0.1 = 25 阈值)
            val threshold = (255 * (1.0 - similarity)).toInt()

            // 3. 解析偏移点字符串
            val offsets: MutableList<OffsetPoint?> = ArrayList<OffsetPoint?>()
            if (offsetStr != null && !offsetStr.isEmpty()) {
                val points =
                    offsetStr.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (p in points) {
                    val parts =
                        p.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (parts.size >= 3) {
                        val dx = parts[0].toInt()
                        val dy = parts[1].toInt()
                        val color = parseBgrToRgb(parts[2]) // 偏移点也是 BGR 且忽略横杠
                        offsets.add(OffsetPoint(dx, dy, color))
                    }
                }
            }

            return ColorSchema(x1, y1, x2, y2, mainColor, threshold, offsets, dir)
        }

        /**
         * 辅助工具：处理 "D97700-101010" 格式，并从 BGR 转为 RGB
         */
        private fun parseBgrToRgb(colorStr: String): Int {
            // 忽略横杠后面的内容
            var colorStr = colorStr
            if (colorStr.contains("-")) {
                colorStr =
                    colorStr.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            }


            // 解析 16 进制字符串 (例如 "D97700")
            val bgr = colorStr.toInt(16)


            // 提取 B, G, R 分量 (假设输入是 0xBBGGRR)
            val b = (bgr shr 16) and 0xFF
            val g = (bgr shr 8) and 0xFF
            val r = bgr and 0xFF


            // 组合成 Android 可用的 RGB (0xFFRRGGBB)
            return Color.rgb(r, g, b)
        }
    }
}