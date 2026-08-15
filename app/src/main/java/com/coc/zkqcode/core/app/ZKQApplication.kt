package com.coc.zkqcode.core.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.coc.zkqcode.core.util.fileactions.LogHelper
import com.topjohnwu.superuser.Shell
import timber.log.Timber

class ZKQApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
        // Initialize Timber as early as possible so logging is available before any crash
        LogHelper.initTimber()
        // Install a global uncaught exception handler to log all crashes
        installGlobalExceptionHandler()
        // Initialize libsu as soon as the process starts so every component
        // shares the same builder configuration before any shell is created.
        ensureLibSuInitialized()
        // Load native library early in the Application lifecycle so it doesn't
        // block the Activity UI thread and cause a white screen on slow devices.
        System.loadLibrary("rust_logic")
    }

    private fun installGlobalExceptionHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            // Log to Timber (writes to error.log via FileLoggingTree in release)
            Timber.tag("zkq_crash").e(throwable, "Uncaught exception on thread: ${thread.name}")
            // Write dedicated crash.log with full stack trace
            LogHelper.writeCrashLog(this, thread, throwable)
            // Delegate to the default Android handler (shows crash dialog / kills process)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun ensureLibSuInitialized() {
        if (libSuInitialized) return
        synchronized(this) {
            if (libSuInitialized) return
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                    .setFlags(Shell.FLAG_MOUNT_MASTER)
            )
            libSuInitialized = true
        }
    }

    companion object {
        @Volatile
        private var libSuInitialized = false

        /**
         * Application-level context available throughout the app lifecycle.
         * Set in onCreate() before any other initialization.
         */
        @SuppressLint("StaticFieldLeak")
        lateinit var appContext: Context
            private set
    }
}
