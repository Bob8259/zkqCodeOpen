package com.coc.zkqcode.utils.checkpermissions

import android.content.Context
import android.util.Log
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
            // 1. 创建脚本目录
            val scriptFile = File(scriptPath)
            val scriptDir = scriptFile.parentFile
            if (scriptDir != null && !scriptDir.exists()) {
                Shell.cmd("mkdir -p ${scriptDir.absolutePath}").exec()
                Shell.cmd("chmod 777 ${scriptDir.absolutePath}").exec()
            }

            // 2. 清空并写入脚本内容
            Shell.cmd("echo \"\" > $scriptPath").exec()
            for (line in scriptContent) {
                // 使用单引号包裹内容，防止 shell 解析特殊字符
                Shell.cmd("echo '${line}' >> $scriptPath").exec()
            }

            // 3. 赋予执行权限
            Shell.cmd("chmod 755 $scriptPath").exec()

            // 4. 检查进程是否已在运行 (通过两次采样确认)
            val firstPids = Shell.cmd("ps -ef | grep '$scriptPath' | grep -v grep")
                .exec().out.mapNotNull { line ->
                    line.split("\\s+".toRegex()).getOrNull(1)
                }.toMutableList()

            delay(500) // 采样间隔

            val secondPids = Shell.cmd("ps -ef | grep '$scriptPath' | grep -v grep")
                .exec().out.mapNotNull { line ->
                    line.split("\\s+".toRegex()).getOrNull(1)
                }.toMutableList()

            // 如果两次有重复的PID说明进程持续存在
            if (firstPids.intersect(secondPids.toSet()).isNotEmpty()) {
                Log.d("DaemonManager", "守护进程已在运行")
                return
            }

            // 5. 启动守护进程
            val cmdStart = "nohup sh $scriptPath > /dev/null 2>&1 &"
            Shell.cmd(cmdStart).exec()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
