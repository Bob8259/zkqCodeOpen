package com.coc.zkqcode.jar.ui.pages.single

import com.coc.zkqcode.core.util.basic.RunShell

/**
 * Forces an NTP time sync using Android's native mechanism via root shell.
 * Tries multiple NTP servers as fallbacks for reliability across regions.
 * Uses the provided showMsg callback for UI feedback.
 */
fun autoSyncSystemTime(showMsg: (String) -> Unit) {
    try {
        // Enable automatic time
        RunShell.runBlockingNoOutput("settings put global auto_time 1", checkIsPlaying = false)

        // Try NTP servers in order until one succeeds
        val ntpServers = listOf(
            "ntp.aliyun.com",
            "time.asia.apple.com",
            "pool.ntp.org",
            "time.google.com"
        )

        for (server in ntpServers) {
            RunShell.runBlockingNoOutput("settings put global ntp_server $server", checkIsPlaying = false)
            val result = RunShell.runBlocking("cmd connectivity force-ntp-sync", checkIsPlaying = false)
            // Some Android versions return non-zero exit code even on successful sync.
            // Check output for "success" keyword or treat no-error output as success.
            val output = (result.out + result.err).joinToString(" ").lowercase()
            if (result.isSuccess || output.contains("success") || !output.contains("fail")) {
                showMsg("时间同步成功 ($server)")
                return
            }
        }

        showMsg("时间同步失败: 所有NTP服务器均不可用")
    } catch (e: Exception) {
        showMsg("时间同步失败: ${e.message}")
    }
}
