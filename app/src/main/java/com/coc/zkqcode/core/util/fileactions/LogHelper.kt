package com.coc.zkqcode.core.util.fileactions

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.coc.zkqcode.BuildConfig
import com.topjohnwu.superuser.Shell
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LogHelper {
    fun initTimber(context: Context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(FileLoggingTree(context))
        }
    }

    fun logAndRestart(message: String): Nothing {
        Timber.tag("zkq_debug").e("CRITICAL_ERROR: $message")
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

            val timestamp =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
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
