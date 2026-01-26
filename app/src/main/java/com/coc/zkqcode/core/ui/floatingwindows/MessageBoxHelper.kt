package com.coc.zkqcode.utils.floatingwindows

import android.content.Context
import android.content.Intent
import android.os.Build

object MessageBoxHelper {
    fun showFloatingMessage(
        context: Context,
        text: String,
        x: Int? = null,
        y: Int? = null,
        fontSize: Float = 9f,
        duration: Long = 2500L
    ) {
        // 3. Get screen dimensions
        val displayMetrics = context.resources.displayMetrics

        // 4. Use the Elvis operator (?:). If x is null, use widthPixels.
        val finalX = x ?: displayMetrics.widthPixels
        val finalY = y ?: displayMetrics.heightPixels

        val intent = Intent(context, MessageBoxService::class.java).apply {
            putExtra("text", text)
            putExtra("x", finalX) // Pass the calculated value
            putExtra("y", finalY) // Pass the calculated value
            putExtra("fontSize", fontSize)
            putExtra("duration", duration)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
}
