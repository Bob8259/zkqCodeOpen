package com.coc.zkqcode.core.system.daemon

import android.content.Context
import com.coc.zkqcode.core.data.database.GlobalVars
import com.topjohnwu.superuser.Shell
import java.io.File

object ServerManager {

    fun startServer(context: Context): Boolean {
        return try {
            val serverFile = File(context.filesDir, "server.apk")
            if (!serverFile.exists()) {
                context.assets.open("server.apk").use { input ->
                    serverFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            GlobalVars.serverPath = serverFile.absolutePath

            // Execute the shell command to start the server
            // 使用 setsid 创建新会话，彻底脱离控制终端
            Shell.cmd("setsid CLASSPATH=${GlobalVars.serverPath} app_process /system/bin com.coc.zkqserver.ShellServer > /dev/null 2>&1 &")
                .exec()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
