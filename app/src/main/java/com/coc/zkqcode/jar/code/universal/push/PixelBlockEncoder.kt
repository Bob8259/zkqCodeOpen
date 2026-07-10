package com.coc.zkqcode.jar.code.universal.push

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import java.io.ByteArrayOutputStream

/**
 * Encodes a screenshot bitmap into braille pixel art for push notifications.
 *
 * Pipeline: crop → grayscale → rotate 90° right → invert → braille encoding.
 */
object PixelBlockEncoder {

    // Default crop region: (left, top, right, bottom)
    private const val CROP_LEFT = 300
    private const val CROP_TOP = 244
    private const val CROP_RIGHT = 469
    private const val CROP_BOTTOM = 291

    // Braille dot positions: 2 columns × 4 rows = 8 dots per character
    private val BRAILLE_DOTS = intArrayOf(
        0x01, 0x08,  // row 0: dots 1, 8
        0x02, 0x10,  // row 1: dots 2, 16
        0x04, 0x20,  // row 2: dots 4, 32
        0x40, 0x80   // row 3: dots 64, 128
    )
    private const val BRAILLE_BASE = 0x2800

    /**
     * Crops the bitmap to the configured region, converts to
     * grayscale, inverts colors, and encodes as braille pixel art text.
     *
     * @return braille art string with newlines, or null if bitmap is too small
     *         or the image is >95% filled (mostly dark/solid).
     */
    fun encodeBitmapToBraille(bitmap: Bitmap): String? {
        // 1. Crop
        val cropW = CROP_RIGHT - CROP_LEFT
        val cropH = CROP_BOTTOM - CROP_TOP
        if (cropW <= 0 || cropH <= 0 || bitmap.width < CROP_RIGHT || bitmap.height < CROP_BOTTOM) {
            return null
        }
        val cropped = Bitmap.createBitmap(bitmap, CROP_LEFT, CROP_TOP, cropW, cropH)

        val w = cropped.width   // 169
        val h = cropped.height  // 47
        val brailleCols = (w + 1) / 2   // ceil(169/2) = 85
        val brailleRows = (h + 3) / 4   // ceil(47/4) = 12

        val pixels = IntArray(w * h)
        cropped.getPixels(pixels, 0, w, 0, 0, w, h)
        cropped.recycle()

        val sb = StringBuilder()
        var darkCount = 0
        val totalPixels = w * h

        // 2. Process each braille character block (2 columns × 4 rows)
        for (by in 0 until brailleRows) {
            for (bx in 0 until brailleCols) {
                var code = 0
                for (row in 0..3) {
                    for (col in 0..1) {
                        val px = bx * 2 + col
                        val py = by * 4 + row
                        if (px < w && py < h) {
                            val pixel = pixels[py * w + px]
                            val gray = (Color.red(pixel) * 0.299 +
                                    Color.green(pixel) * 0.587 +
                                    Color.blue(pixel) * 0.114).toInt()
                            if (gray < 128) {
                                darkCount++
                                val dotIdx = row * 2 + col
                                code = code or BRAILLE_DOTS[dotIdx]
                            }
                        }
                    }
                }
                sb.appendCodePoint(BRAILLE_BASE + code)
            }
            sb.append('\n')
        }

        // 3. If >95% of pixels are dark, the image is mostly solid — skip braille
        if (darkCount.toFloat() / totalPixels > 0.95f) {
            return null
        }

        return sb.toString().trimEnd('\n')
    }

    /**
     * Converts a Bitmap to JPEG bytes for Telegram sendPhoto.
     * No cropping — uses the full bitmap as-is.
     */
    fun bitmapToJpegBytes(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
        return stream.toByteArray()
    }
}
