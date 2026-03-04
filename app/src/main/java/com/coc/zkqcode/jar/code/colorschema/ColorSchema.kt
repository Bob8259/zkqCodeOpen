package com.coc.zkqcode.jar.code.colorschema

import android.graphics.Color


class ColorSchema(
    val x1: Int, val y1: Int, val x2: Int, val y2: Int, // Converted to RGB
    val mainColor: Int, // Converted from similarity
    val threshold: Int, val offsets: MutableList<OffsetPoint?>?, val direction: Int,
    val name: String? = null
) {
    class OffsetPoint(val dx: Int, val dy: Int, val color: Int)
    companion object {
        /**
         * Returns a copy of [schema] with new bounds but the same color/threshold/offsets/direction/name.
         */
        fun rescope(schema: ColorSchema, x1: Int, y1: Int, x2: Int, y2: Int, direction: Int = schema.direction): ColorSchema {
            return ColorSchema(x1, y1, x2, y2, schema.mainColor, schema.threshold, schema.offsets, direction, schema.name)
        }

        /**
         * Core parsing method
         */
        fun parse(
            x1: Int, y1: Int, x2: Int, y2: Int,
            mainColorStr: String, offsetStr: String?,
            dir: Int, similarity: Double,
            name: String? = null
        ): ColorSchema {
            // 1. Process main color (BGR -> RGB, ignore dash)

            val mainColor = parseBgrToRgb(mainColorStr)

            // 2. Convert similarity to color difference threshold (0.9 similarity = 255 * 0.1 = 25 threshold)
            val threshold = (255 * (1.0 - similarity)).toInt()

            // 3. Parse offset points string
            val offsets: MutableList<OffsetPoint?> = ArrayList()
            if (!offsetStr.isNullOrEmpty()) {
                val points =
                    offsetStr.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (p in points) {
                    val parts =
                        p.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (parts.size >= 3) {
                        val dx = parts[0].toInt()
                        val dy = parts[1].toInt()
                        val color = parseBgrToRgb(parts[2]) // Offset points are also BGR and ignore dash
                        offsets.add(OffsetPoint(dx, dy, color))
                    }
                }
            }

            return ColorSchema(x1, y1, x2, y2, mainColor, threshold, offsets, dir, name)
        }

        /**
         * Helper tool: process "D97700-101010" format and convert from BGR to RGB
         */
        private fun parseBgrToRgb(colorStr: String): Int {
            // Ignore content after dash
            var colorStr = colorStr
            if (colorStr.contains("-")) {
                colorStr =
                    colorStr.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            }


            // Parse hex string (e.g., "D97700")
            val bgr = colorStr.toInt(16)


            // Extract B, G, R components (assuming input is 0xBBGGRR)
            val b = (bgr shr 16) and 0xFF
            val g = (bgr shr 8) and 0xFF
            val r = bgr and 0xFF


            // Combine into Android-compatible RGB (0xFFRRGGBB)
            return Color.rgb(r, g, b)
        }
    }
}