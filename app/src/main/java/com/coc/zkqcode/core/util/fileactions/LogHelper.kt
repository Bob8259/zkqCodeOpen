package com.coc.zkqcode.core.util.fileactions

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.coc.zkqcode.BuildConfig
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.topjohnwu.superuser.Shell
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

object LogHelper {
    // Re-entry guard to prevent recursive calls (e.g. ShowMessage -> logAndRestart -> ShowMessage)
    private val isRestarting = AtomicBoolean(false)

    fun initTimber(context: Context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(FileLoggingTree(context))
        }
    }

    fun logAndRestart(message: String): Nothing {
        // Prevent re-entrant calls; only the first caller proceeds with the restart sequence
        if (!isRestarting.compareAndSet(false, true)) {
            error("logAndRestart re-entered: $message")
        }
        Timber.tag("zkq_debug").e("CRITICAL_ERROR: $message")
        repeat(10) {
            try {
                ShowMessage("出现未知错误，即将尝试重启。\n注意：请检查辅助配置，确保除了部落标签和暗号以外，其他所有的输入框都不能为空。\n并且该填数字的地方就要填数字，该填文字的地方填文字，不能乱填。\n若辅助配置没问题，则请截图该错误信息向作者反馈。\n\n错误信息：\n$message")
            } catch (e: Exception) {
                Timber.tag("zkq_debug").e("ShowMessage failed during logAndRestart: ${e.message}")
            }
            Thread.sleep(1000)
        }
        // Spawn a detached process via setsid to restart the app after killing it.
        // The new session ensures this child survives the parent process termination.
        Shell.cmd(
            "setsid sh -c 'sleep 2; am force-stop com.coc.zkqcode; sleep 1; am start -n com.coc.zkqcode/.MainActivity' > /dev/null 2>&1 &"
        ).exec()
        error(message)
    }

    fun showDebugInfo(message: String) {
        Timber.tag("zkq_debug").d("Debug info: $message")
    }

    class FileLoggingTree(private val context: Context) : Timber.Tree() {
        @SuppressLint("LogNotTimber")
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            val logDir = File(context.filesDir, "logs")
            if (!logDir.exists()) logDir.mkdirs()

            val fileName = if (priority >= Log.ERROR) "error.log" else "info.log"
            val logFile = File(logDir, fileName)

            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
            val logEntry = "$timestamp [$tag] $message\n"

            try {
                // Append log entry
                FileOutputStream(logFile, true).use { fos ->
                    fos.write(logEntry.toByteArray())
                }

                // Maintain 100 lines limit for each file
                synchronized(this) {
                    val lines = logFile.readLines()
                    if (lines.size > 100) {
                        val trimmedLines = lines.takeLast(100)
                        logFile.writeText(trimmedLines.joinToString("\n") + "\n")
                    }
                }
            } catch (e: Exception) {
                // Use standard Log to avoid infinite recursion if Timber fails
                Log.e("FileLoggingTree", "Error writing to $fileName", e)
            }
        }
    }
}
