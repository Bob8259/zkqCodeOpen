package com.coc.zkqcode.core.app

import android.app.Application
import com.topjohnwu.superuser.Shell

class ZKQApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize libsu as soon as the process starts so every component
        // shares the same builder configuration before any shell is created.
        ensureLibSuInitialized()
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
    }
}
