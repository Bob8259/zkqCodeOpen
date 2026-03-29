package com.coc.zkqcode.core.ui.floatingwindows

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper

object MessageBoxHelper {
    fun showFloatingMessage(
        context: Context,
        text: String,
        x: Int? = null,
        y: Int? = null,
        fontSize: Float = 9f,
        duration: Long = 2500L
    ) {
        val displayMetrics = context.resources.displayMetrics
        val finalX = x ?: displayMetrics.widthPixels
        val finalY = y ?: displayMetrics.heightPixels

        // Fast path: deliver directly via SharedFlow when the service is already running
        if (MessageBoxService.isRunning.get()) {
            MessageBoxService.messageFlow.tryEmit(
                MessageData(
                    text = text,
                    x = finalX,
                    y = finalY,
                    fontSize = fontSize,
                    duration = duration
                )
            )
            return
        }

        // Cold start: use Intent to bootstrap the foreground service
        val intent = Intent(context, MessageBoxService::class.java).apply {
            putExtra("text", text)
            putExtra("x", finalX)
            putExtra("y", finalY)
            putExtra("fontSize", fontSize)
            putExtra("duration", duration)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    // Show the ad overlay via MessageBoxService's second ComposeView window
    fun showAdOverlay(context: Context, adItems: List<AdItem>, durationSeconds: Int) {
        val payload = AdOverlayData(items = adItems, durationSeconds = durationSeconds)
        if (MessageBoxService.isRunning.get()) {
            MessageBoxService.adFlow.tryEmit(payload)
            return
        }

        // Cold start: boot the service first, then deliver ad data after it initializes
        val intent = Intent(context, MessageBoxService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
        Handler(Looper.getMainLooper()).postDelayed({
            MessageBoxService.adFlow.tryEmit(payload)
        }, 300L)
    }

    // Dismiss the ad overlay
    fun dismissAdOverlay() {
        MessageBoxService.adFlow.tryEmit(null)
    }
}
