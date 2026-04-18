package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlinx.coroutines.delay

/**
 * Reads the device CPU temperature from the kernel thermal zone sysfs interface.
 * Returns temperature in °C, or -1f if reading fails.
 */
suspend fun getDeviceTemperature(): Float {
    val raw = RunShell.runAndGetFirst(
        "cat /sys/class/thermal/thermal_zone0/temp",
        checkIsPlaying = false
    )
    val value = raw.trim().toFloatOrNull() ?: return -1f
    // Some devices report in degrees directly (< 200), others in millidegrees (>= 1000)
    return if (value >= 1000) value / 1000f else value
}

/**
 * Checks device temperature against user-configured thresholds.
 * If temperature exceeds the pause threshold:
 * 1. Kills the game
 * 2. Presses home button
 * 3. Loops every 5 seconds, showing current temperature via accountMessage
 * 4. When temperature drops to or below the resume threshold, calls enterMainScreen and returns
 *
 * Does nothing if temperature protection is disabled in settings.
 */
suspend fun checkAndCoolDown() {
    val enabled = getStaticConfig(Schema.GLOBAL_SETTINGS.TEMP_PROTECTION.key) == "1"
    if (!enabled) return

    val pauseTemp = getStaticConfig(Schema.GLOBAL_SETTINGS.TEMP_PAUSE_THRESHOLD.key)
        .toFloatOrNull() ?: logAndRestart("${Schema.GLOBAL_SETTINGS.TEMP_PAUSE_THRESHOLD.displayName} 必须是数字，请检查配置")
    val resumeTemp = getStaticConfig(Schema.GLOBAL_SETTINGS.TEMP_RESUME_THRESHOLD.key)
        .toFloatOrNull() ?: logAndRestart("${Schema.GLOBAL_SETTINGS.TEMP_RESUME_THRESHOLD.displayName} 必须是数字，请检查配置")

    val currentTemp = getDeviceTemperature()
    if (currentTemp < 0f || currentTemp < pauseTemp) return

    // Temperature too high — initiate cooldown
    accountMessage("设备温度过高: ${currentTemp}°C，暂停运行")
    killGame()
    RunShell.runNoOutput("input keyevent 3") // Press home button
    delay(2000)

    // Wait loop: check every 5 seconds until cooled down
    while (true) {
        delay(5000)
        val temp = getDeviceTemperature()
        if (temp < 0f) {
            accountMessage("无法读取温度，继续运行")
            break
        }
        if (temp <= resumeTemp) {
            accountMessage("设备已降温: ${temp}°C，恢复运行")
            break
        }
        accountMessage("等待降温中: ${temp}°C (目标: ≤${resumeTemp}°C)")
    }

    // Re-enter the game after cooldown
    enterMainScreen(true)
}
