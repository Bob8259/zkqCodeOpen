package com.coc.zkqcode.core.system.daemon

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.coc.zkqcode.MainActivity
import com.coc.zkqcode.core.data.websocket.ServerActions
import com.coc.zkqcode.core.ui.floatingwindows.NotificationHelper
import com.coc.zkqcode.core.data.websocket.ServerConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.Socket
//Used for start on deivce boot
class DaemonService : Service() {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var serverActions: ServerActions? = null

    override fun onCreate() {
        super.onCreate()
        val notification: Notification = NotificationHelper.createNotification(this)
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            // 1. Initialize Shell
            println("start daemon service")
            
            // 2. Ensure Server is Running
            // Try to start server multiple times if needed, or ensuring it's up
            ServerManager.startServer(this@DaemonService)

            // 3. Wait for Server Port to be Open
            if (waitForServerPort(6839, 15000)) {
                 println("Server port is open, connecting...")
                 
                 // 4. Connect to WebSocket & Read Config
                val serverConnection = ServerConnection("ws://localhost:6839/zkq")
                serverActions = ServerActions(serverConnection) {
                    // Callback when config is loaded
                    val configJson = serverActions?.configJson
                    if (configJson != null) {
                        val autoStart = if (configJson.has("auto_start")) {
                            try {
                                configJson.get("auto_start").asInt
                            } catch (_: Exception) {
                                0
                            }
                        } else {
                            0 
                        }

                        if (autoStart == 1) {
                             val i = Intent(this@DaemonService, MainActivity::class.java)
                             i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                             startActivity(i)
                        }
                    }
                    stopSelf() // Work done
                }
            } else {
                println("Server failed to start within timeout.")
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private suspend fun waitForServerPort(port: Int, timeoutMs: Long): Boolean {
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress("localhost", port), 200)
                socket.close()
                return true
            } catch (_: Exception) {
                // Ignore and retry
                delay(500)
            }
        }
        return false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
