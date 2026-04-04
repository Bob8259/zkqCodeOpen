package com.coc.zkqcode.core.ui.floatingwindows

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.coc.zkqcode.R

object NotificationHelper {
    private const val CHANNEL_ID = "floating_service_channel"
    private const val CHANNEL_NAME = "紫孔雀服务"

    fun createNotification(context: Context): Notification {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
                )
                manager.createNotificationChannel(channel)
            }
        }

        // Decode the drawable as a full-color large icon for the notification
        val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.main_icon)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("紫孔雀")
            .setContentText("紫孔雀正在运行")
            .setSmallIcon(R.drawable.main_icon)
            .setLargeIcon(largeIcon)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}