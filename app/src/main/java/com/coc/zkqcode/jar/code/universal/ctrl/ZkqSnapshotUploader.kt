package com.coc.zkqcode.jar.code.universal.ctrl

/**
 * Snapshot relay (channel A): Deprecated.
 * Device status snapshot reporting has been unified into [ZkqWsClient] over WebSocket.
 */
object ZkqSnapshotUploader {

    fun isEnabled(): Boolean = ZkqWsClient.isEnabled()

    fun hasValidServerUrl(): Boolean = ZkqWsClient.hasValidServerUrl()

    @Suppress("RedundantSuspendModifier")
    suspend fun run() {
        // No-op: all snapshot and query handling is unified into ZkqWsClient WebSocket channel
    }
}
