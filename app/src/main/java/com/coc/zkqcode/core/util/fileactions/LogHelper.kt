package com.coc.zkqcode.core.util.fileactions

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.coc.zkqcode.BuildConfig
import com.coc.zkqcode.core.app.ZKQApplication
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.exit.AppExitHelper
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
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
    // Guard against planting Timber trees more than once (e.g. multiple Activity instances)
    private val timberInitialized = AtomicBoolean(false)

    fun initTimber(context: Context) {
        if (!timberInitialized.compareAndSet(false, true)) return
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            // TODO(临时开放): DEBUG 构建也写文件日志（filesDir/logs/info.log），
            //  便于 QQ 日志查询调试。用户说恢复后删除本行，恢复为只 DebugTree。
            Timber.plant(FileLoggingTree(context))
        } else {
            Timber.plant(FileLoggingTree(context))
        }
    }

    fun logAndRestart(message: String, isShow: Boolean = true): Nothing {
        // Prevent re-entrant calls; only the first caller proceeds with the restart sequence
        if (!isRestarting.compareAndSet(false, true)) {
            error("logAndRestart re-entered: $message")
        }
        Timber.tag("zkq_debug").e("CRITICAL_ERROR: $message")
        if (isShow) {
            repeat(3) {
                try {
                    ShowMessage("出现未知错误，即将尝试重启。\n注意：请检查辅助配置，确保除了部落标签和暗号以外，其他所有的输入框都不能为空。\n并且该填数字的地方就要填数字，该填文字的地方填文字，不能乱填。\n若辅助配置没问题，则请截图该错误信息向作者反馈。\n\n错误信息：\n$message")
                } catch (e: Exception) {
                    Timber.tag("zkq_debug").e("ShowMessage failed during logAndRestart: ${e.message}")
                }
                Thread.sleep(1000)
            }
        }
        AppExitHelper.restoreDefaultInputMethod(ZKQApplication.appContext)
        // Spawn a detached process via setsid to restart the app after killing it.
        // The new session ensures this child survives the parent process termination.
        RunShell.runBlockingNoOutput(
            "setsid sh -c 'sleep 2; am force-stop com.coc.zkqcode; sleep 1; am start -n com.coc.zkqcode/.MainActivity' > /dev/null 2>&1 &"
        )
        error(message)
    }

    fun showDebugInfo(message: String) {
        Timber.tag("zkq_debug").d("Debug info: $message")
    }

    /**
     * Append a botJob monitor snapshot to filesDir/logs/monitor.log.
     * Keeps the last 500 lines.
     */
    @SuppressLint("LogNotTimber")
    fun writeMonitorLog(
        context: Context,
        isBotJobNull: Boolean,
        isBotJobActive: Boolean,
        appMode: String
    ) {
        try {
            val logDir = File(context.filesDir, "logs")
            if (!logDir.exists()) logDir.mkdirs()
            val monitorFile = File(logDir, "monitor.log")

            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
            val entry =
                "$timestamp botJobNull=$isBotJobNull botJobActive=$isBotJobActive appMode=$appMode\n"

            FileOutputStream(monitorFile, true).use { fos ->
                fos.write(entry.toByteArray())
            }

            // Keep monitor.log at a reasonable size (last 500 lines)
            val lines = monitorFile.readLines()
            if (lines.size > 500) {
                monitorFile.writeText(lines.takeLast(500).joinToString("\n") + "\n")
            }
        } catch (e: Exception) {
            Log.e("LogHelper", "Failed to write monitor log", e)
        }
    }

    /**
     * Write crash details to a dedicated crash.log file in the app's private logs directory.
     * Called by the global uncaught exception handler installed in ZKQApplication.
     */
    @SuppressLint("LogNotTimber")
    fun writeCrashLog(context: Context, thread: Thread, throwable: Throwable) {
        try {
            val logDir = File(context.filesDir, "logs")
            if (!logDir.exists()) logDir.mkdirs()
            val crashFile = File(logDir, "crash.log")

            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
            val entry = buildString {
                appendLine("=== CRASH at $timestamp ===")
                appendLine("Thread: ${thread.name}")
                appendLine(throwable.stackTraceToString())
                appendLine()
            }

            FileOutputStream(crashFile, true).use { fos ->
                fos.write(entry.toByteArray())
            }

            // Keep crash.log at a reasonable size (last 200 lines)
            val lines = crashFile.readLines()
            if (lines.size > 200) {
                crashFile.writeText(lines.takeLast(200).joinToString("\n") + "\n")
            }
        } catch (e: Exception) {
            Log.e("LogHelper", "Failed to write crash log", e)
        }
    }

    class FileLoggingTree(private val context: Context) : Timber.Tree() {
        // Only run the retention cleanup once per day (logging is hot path).
        private var lastCleanupDay = ""

        @SuppressLint("LogNotTimber")
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            val logDir = File(context.filesDir, "logs")
            if (!logDir.exists()) logDir.mkdirs()

            // Per-day rolling files: info-20260803.log / error-20260803.log,
            // retained by LOG_RETENTION_DAYS instead of a line cap.
            val day = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
            val fileName = if (priority >= Log.ERROR) "error-$day.log" else "info-$day.log"
            val logFile = File(logDir, fileName)

            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
            val logEntry = "$timestamp [$tag] $message\n"

            try {
                FileOutputStream(logFile, true).use { fos ->
                    fos.write(logEntry.toByteArray())
                }
                cleanupOldLogs(logDir)
            } catch (e: Exception) {
                // Use standard Log to avoid infinite recursion if Timber fails
                Log.e("FileLoggingTree", "Error writing to $fileName", e)
            }
        }

        /** Deletes log files older than the configured retention (once per day).
         *  Retention is pushed by the ZKQ plugin; the local setting is a fallback. */
        private fun cleanupOldLogs(logDir: File) {
            val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
            if (lastCleanupDay == today) return
            lastCleanupDay = today
            val pushed = com.coc.zkqcode.jar.code.universal.ctrl.ZkqSnapshotUploader.logRetentionDays
            val days = when {
                pushed > 0 -> pushed
                else -> GlobalVars.configStates[GLOBAL_SETTINGS.LOG_RETENTION_DAYS.key]
                    ?.value?.toIntOrNull() ?: 3
            }
            if (days <= 0) return  // 0/negative = keep forever
            val cutoff = System.currentTimeMillis() - days * 86_400_000L
            logDir.listFiles()?.forEach { f ->
                val isLogFile = f.isFile && f.name.endsWith(".log") &&
                    (f.name.startsWith("info") || f.name.startsWith("error"))
                if (isLogFile && f.lastModified() < cutoff) {
                    f.delete()
                }
            }
        }
    }
}
