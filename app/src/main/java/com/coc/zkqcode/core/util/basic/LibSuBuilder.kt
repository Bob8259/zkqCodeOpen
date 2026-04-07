package com.coc.zkqcode.core.util.basic

import com.topjohnwu.superuser.Shell

object LibSuBuilder {
    @Volatile
    private var initialized = false

    /**
     * Configure libsu before the first shell is created.
     */
    fun ensureInitialized() {
        if (initialized) return
        synchronized(this) {
            if (initialized) return
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                    .setFlags(Shell.FLAG_MOUNT_MASTER)
            )
            initialized = true
        }
    }
}
