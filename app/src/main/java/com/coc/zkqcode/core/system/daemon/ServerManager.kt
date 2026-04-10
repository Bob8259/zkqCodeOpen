package com.coc.zkqcode.core.system.daemon

import android.content.Context
import com.topjohnwu.superuser.Shell
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
            Shell.cmd("pkill -f com.coc.zkqserver.ShellServer").exec()
            // Start the server in a detached session so it survives parent process termination
            Shell.cmd("setsid sh -c 'export CLASSPATH=${serverFile.absolutePath}; exec app_process /system/bin com.coc.zkqserver.ShellServer' > /dev/null 2>&1 &")
                .exec()
            // Use nohup for enhanced fault tolerance
            Shell.cmd("nohup sh -c 'export CLASSPATH=${serverFile.absolutePath}; exec app_process /system/bin com.coc.zkqserver.ShellServer' > /dev/null 2>&1 &")
                .exec()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
