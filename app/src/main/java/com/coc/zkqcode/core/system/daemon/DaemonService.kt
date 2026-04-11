package com.coc.zkqcode.core.system.daemon

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import com.coc.zkqcode.MainActivity
import com.coc.zkqcode.core.ui.floatingwindows.NotificationHelper
import com.coc.zkqcode.core.util.basic.RunShell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

// Used for auto-start handling after device boot.
class DaemonService : Service() {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val configPath = "${Environment.getExternalStorageDirectory().path}/zkqFiles/zkq_config.json"

    override fun onCreate() {
        super.onCreate()
        val notification: Notification = NotificationHelper.createNotification(this)
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            // Read the config directly through libsu so boot handling does not depend on the websocket server.
            if (readAutoStartValue() == 1) {
                val launchIntent = Intent(this@DaemonService, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(launchIntent)
            }
            stopSelf()
        }
        return START_NOT_STICKY
    }

    private suspend fun readAutoStartValue(): Int {
        return try {
            val configContent = RunShell.run(
                """if [ -f "$configPath" ]; then cat "$configPath"; fi""",
                checkIsPlaying = false
            ).joinToString("\n").trim()

            if (configContent.isBlank()) {
                0
            } else {
                JSONObject(configContent).optInt("auto_start", 0)
            }
        } catch (_: Exception) {
            0
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
