package com.coc.zkqcode.jar.code.universal.ctrl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Background daemon for AstrBot plugin communication.
 * Runs independently of the script execution lifecycle:
 * keeps status snapshot reporting and WebSocket queries active
 * whenever the app/floating window is open and configured.
 */
object ZkqDaemon {
    private val started = AtomicBoolean(false)
    private val daemonScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun start() {
        if (!started.compareAndSet(false, true)) return
        daemonScope.launch { ZkqWsClient.run() }
    }
}
