package com.coc.zkqcode.utils.floatingwindows

import android.content.Context
import android.content.Intent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

object MessageBoxHelper {
    fun showFloatingMessage(
        context: Context,
        text: String,
        x: Int = 1280,
        y: Int = 720,
        fontSize: Float = 15f,
        duration: Long = 2000L
    ) {
        val intent = Intent(context, MessageBoxService::class.java).apply {
            putExtra("text", text)
            putExtra("x", x)
            putExtra("y", y)
            putExtra("fontSize", fontSize)
            putExtra("duration", duration)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
}
