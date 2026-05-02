package com.coc.zkqcode.core.system.daemon

import android.content.Context
import com.coc.zkqcode.core.util.basic.RunShell
import java.io.File

object ServerManager {

    fun startServer(context: Context): Boolean {
        return try {
            val serverFile = File(context.filesDir, "server.apk")
            context.assets.open("server.apk").use { input ->
                serverFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            // Kill any stale ShellServer processes to free port 6839 before starting a new one
            RunShell.runBlockingNoOutput("pkill -f com.coc.zkqserver.ShellServer")
            // Start the server in a detached session so it survives parent process termination
            RunShell.runBlockingNoOutput("setsid sh -c 'export CLASSPATH=${serverFile.absolutePath}; exec app_process /system/bin com.coc.zkqserver.ShellServer' > /dev/null 2>&1 &")
            // Use nohup for enhanced fault tolerance
            RunShell.runBlockingNoOutput("nohup sh -c 'export CLASSPATH=${serverFile.absolutePath}; exec app_process /system/bin com.coc.zkqserver.ShellServer' > /dev/null 2>&1 &")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
