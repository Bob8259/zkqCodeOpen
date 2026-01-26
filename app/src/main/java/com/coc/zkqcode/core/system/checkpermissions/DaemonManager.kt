package com.coc.zkqcode.core.system.checkpermissions

import android.content.Context
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.delay
import java.io.File

object DaemonManager {
    suspend fun setupAndRunDaemon(context: Context) {
        val scriptPath = "/data/local/tmp/zkq_daemon.sh"
        val pkg = context.packageName

        val scriptContent = listOf(
            "#!/system/bin/sh",
            "",
            "while true; do",
            "    if ! pidof $pkg > /dev/null; then",
            "        am start -n $pkg/.MainActivity >>/dev/null 2>&1",
            "    fi",
            "    sleep 10",
            "done"
        )

        try {
            // 1. Create the script directory if it doesn't exist
            val scriptFile = File(scriptPath)
            val scriptDir = scriptFile.parentFile
            if (scriptDir != null && !scriptDir.exists()) {
                Shell.cmd("mkdir -p ${scriptDir.absolutePath}").exec()
                Shell.cmd("chmod 777 ${scriptDir.absolutePath}").exec()
            }

            // 2. Clear existing content and write the script lines
            Shell.cmd("echo \"\" > $scriptPath").exec()
            for (line in scriptContent) {
                // Wrap content in single quotes to prevent shell parsing of special characters
                Shell.cmd("echo '${line}' >> $scriptPath").exec()
            }

            // 3. Grant execution permissions
            Shell.cmd("chmod 755 $scriptPath").exec()

            // 4. Identify and kill existing daemon processes
            // We search for the script path in the process list, excluding the 'grep' command itself
            val existingPids = Shell.cmd("ps -ef | grep '$scriptPath' | grep -v grep")
                .exec().out.mapNotNull { line ->
                    // Extract the PID (usually the second column in ps -ef)
                    line.trim().split("\\s+".toRegex()).getOrNull(1)
                }

            if (existingPids.isNotEmpty()) {
                for (pid in existingPids) {
                    Shell.cmd("kill -9 $pid").exec()
                }
                // Small delay to ensure the system releases the process resources
                delay(300)
            }

            // 5. Start the daemon process in the background
            val cmdStart = "nohup sh $scriptPath > /dev/null 2>&1 &"
            Shell.cmd(cmdStart).exec()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}