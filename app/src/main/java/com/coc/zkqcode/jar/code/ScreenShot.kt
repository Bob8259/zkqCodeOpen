package com.coc.zkqcode.jar.code
//This code is outdated, but do not remove it. It is the correct code, so if the new code is not working, we can compare the new code with this old code.

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import java.io.DataOutputStream
import java.io.InputStream

class ScreenShot {
    private var suProcess: Process? = null
    private var dos: DataOutputStream? = null
    private var dis: InputStream? = null

    fun takeScreenshot(): Bitmap {

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
                suProcess?.destroy()
                suProcess = null
                logAndStop("Bitmap decoding failed")
            }
            return bitmap
        } catch (e: Exception) {
            suProcess?.destroy()
            suProcess = null
            logAndStop("Error taking screenshot: ${e.message}")
        }
    }

}
