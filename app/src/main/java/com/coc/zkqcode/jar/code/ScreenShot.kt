package com.coc.zkqcode.jar.code

import android.graphics.BitmapFactory
import android.graphics.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.DataOutputStream
import java.io.InputStream
import androidx.core.graphics.get

class Test {
    private var suProcess: Process? = null
    private var dos: DataOutputStream? = null
    private var dis: InputStream? = null

    fun testcode() {

        startColorDetectionLoop()
    }

    fun startColorDetectionLoop() {
        CoroutineScope(Dispatchers.IO).launch {

            while (true) {
                // Record total start time
                val startTime = System.currentTimeMillis()

                val color = getPixelColorFromPng(1, 1)

                val endTime = System.currentTimeMillis()
                val duration = endTime - startTime

                if (color != null) {

                } else {

                }

                // Keep 1s delay for log observation; can be changed to delay(10) for speed
                delay(1000)
            }
        }
    }

    private fun getPixelColorFromPng(x: Int, y: Int): IntArray? {
        try {
            if (suProcess == null) {
                suProcess = Runtime.getRuntime().exec("su")
                dos = DataOutputStream(suProcess!!.outputStream)
                dis = suProcess!!.inputStream

            }

            // Send screenshot command
            dos?.writeBytes("screencap -p")
            dos?.flush()

            // Critical: decodeStream will block until the entire image stream is read
            val bitmap = BitmapFactory.decodeStream(dis)

            if (bitmap != null) {
                if (x < bitmap.width && y < bitmap.height) {
                    val pixel = bitmap[x, y]

                    val result = intArrayOf(
                        Color.red(pixel),
                        Color.green(pixel),
                        Color.blue(pixel),
                        Color.alpha(pixel)
                    )

                    bitmap.recycle() // Must recycle
                    return result
                }
                bitmap.recycle()
            } else {
                // If decoding returns null, it means the stream data is incomplete or interfered with
                suProcess?.destroy()
                suProcess = null
            }
        } catch (e: Exception) {

            suProcess?.destroy()
            suProcess = null
        }
        return null
    }

    fun destroy() {
        suProcess?.destroy()
        suProcess = null
    }
}
