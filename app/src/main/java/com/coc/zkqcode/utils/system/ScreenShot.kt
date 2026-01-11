package com.coc.zkqcode.utils.system

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.DataOutputStream
import java.io.InputStream

object ScreenShot {
    private var suProcess: Process? = null
    private var dos: DataOutputStream? = null
    private var dis: InputStream? = null

    fun getBitmap(): Bitmap? {
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
            val bitmap = BitmapFactory.decodeStream(dis)

            if (bitmap != null) {
                return bitmap
            } else {
                // If decoding returns null, it means the stream data is incomplete or interfered with
                destroy()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            destroy()
        }
        return null
    }

    private fun destroy() {
        try {
            suProcess?.destroy()
        } catch (e: Exception) {
            // Ignore
        }
        suProcess = null
        dos = null
        dis = null
    }
}