package com.coc.zkqcode.jar.ui.pages.single

import com.topjohnwu.superuser.Shell

/**
 * Forces an NTP time sync using Android's native mechanism via root shell.
 * Tries multiple NTP servers as fallbacks for reliability across regions.
 * Uses the provided showMsg callback for UI feedback.
 */
fun autoSyncSystemTime(showMsg: (String) -> Unit) {
    try {
        // Enable automatic time
        Shell.cmd("settings put global auto_time 1").exec()

        // Try NTP servers in order until one succeeds
        val ntpServers = listOf(
            "ntp.aliyun.com",
            "time.asia.apple.com",
            "pool.ntp.org",
            "time.google.com"
        )

        for (server in ntpServers) {
            Shell.cmd("settings put global ntp_server $server").exec()
            val result = Shell.cmd("cmd connectivity force-ntp-sync").exec()
            if (result.isSuccess) {
                showMsg("时间同步成功 ($server)")
                return
            }
        }

        showMsg("时间同步失败: 所有NTP服务器均不可用")
    } catch (e: Exception) {
        showMsg("时间同步失败: ${e.message}")
    }
}
