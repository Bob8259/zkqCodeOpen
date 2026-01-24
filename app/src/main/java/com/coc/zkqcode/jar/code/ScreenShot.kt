package com.coc.zkqcode.jar.code

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.DataOutputStream
import java.io.InputStream
import android.util.Log
import androidx.core.graphics.get

class ScreenShot {
    private var suProcess: Process? = null
    private var dos: DataOutputStream? = null
    private var dis: InputStream? = null

    fun takeScreenshot(): Bitmap? {
        try {
            if (suProcess == null) {
                suProcess = Runtime.getRuntime().exec("su")
                dos = DataOutputStream(suProcess!!.outputStream)
                dis = suProcess!!.inputStream
            }

            // Send screenshot command
            dos?.writeBytes("screencap -p\n")
            dos?.flush()

            // Critical: decodeStream will block until the entire image stream is read
            // However, screencap -p sends the PNG data. 
            // BitmapFactory.decodeStream handles this.
            val bitmap = BitmapFactory.decodeStream(dis)

            if (bitmap == null) {
                // If decoding returns null, it means the stream data is incomplete or interfered with
                Log.e("ScreenShot", "Bitmap decoding failed")
                suProcess?.destroy()
                suProcess = null
            }
            return bitmap
        } catch (e: Exception) {
            Log.e("ScreenShot", "Error taking screenshot: ${e.message}")
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
