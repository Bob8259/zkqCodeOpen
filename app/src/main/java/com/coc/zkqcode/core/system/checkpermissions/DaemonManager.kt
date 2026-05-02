package com.coc.zkqcode.core.system.checkpermissions

import android.content.Context
import com.coc.zkqcode.core.util.basic.RunShell
import kotlinx.coroutines.delay
import java.io.File


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
            RunShell.runNoOutput("mkdir -p ${scriptDir.absolutePath}", checkIsPlaying = false)
            RunShell.runNoOutput("chmod 777 ${scriptDir.absolutePath}", checkIsPlaying = false)
        }

        // 2. Clear existing content and write the script lines
        RunShell.runNoOutput("echo \"\" > $scriptPath", checkIsPlaying = false)
        for (line in scriptContent) {
            // Wrap content in single quotes to prevent shell parsing of special characters
            RunShell.runNoOutput("echo '${line}' >> $scriptPath", checkIsPlaying = false)
        }

        // 3. Grant execution permissions
        RunShell.runNoOutput("chmod 755 $scriptPath", checkIsPlaying = false)

        // 4. Identify and kill existing daemon processes
        // We search for the script path in the process list, excluding the 'grep' command itself
        val existingPids = RunShell.run("ps -ef | grep '$scriptPath' | grep -v grep", checkIsPlaying = false)
            .mapNotNull { line ->
                // Extract the PID (usually the second column in ps -ef)
                line.trim().split("\\s+".toRegex()).getOrNull(1)
            }

        if (existingPids.isNotEmpty()) {
            for (pid in existingPids) {
                RunShell.runNoOutput("kill -9 $pid", checkIsPlaying = false)
            }
            // Small delay to ensure the system releases the process resources
            delay(300)
        }

        // 5. Start the daemon process in the background
        // Use nohup and setsid for enhanced fault tolerance
        val cmdStart = "nohup sh $scriptPath > /dev/null 2>&1 &"
        RunShell.runNoOutput(cmdStart, checkIsPlaying = false)
        val cmdStartSetsid = "setsid sh -c 'sh $scriptPath' > /dev/null 2>&1 &"
        RunShell.runNoOutput(cmdStartSetsid, checkIsPlaying = false)

    } catch (e: Exception) {
        e.printStackTrace()
    }
}
