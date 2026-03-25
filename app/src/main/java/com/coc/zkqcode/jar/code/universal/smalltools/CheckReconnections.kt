package com.coc.zkqcode.jar.code.universal.smalltools

import android.os.Environment
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlinx.coroutines.delay


suspend fun checkReconnections(): Boolean {
    // 1. Capture the screen and cast safely

    checkPrivacy()

    // 2. Define the schemas to check against
    val homeSchemas = listOf(
        MyColors.Reconnection,
        MyColors.ReconnectionOnCloudPhone
    )
    val screenBuffer =
        ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            ?: return true
    // 3. Run the check and capture the result
    // We return the result of 'any' to determine if a reconnection event occurred.
    return homeSchemas.any { schema ->
        val match = findMultiColors(byteBuffer = screenBuffer, schema = schema)
        if (match != null) {
            // Count white pixels in the confirmation area; skip this iteration if insufficient
            val whiteCount = countWhitePixels(screenBuffer, 270, 230, 1010, 430)
            if (whiteCount <= 100) {
                return@any true // Not enough white pixels, skip this time
            }
            // Retrieve the configuration state for the specific account
            val configKey = Schema.GLOBAL_SETTINGS.AFTER_KICK_OPTION.key
            val action = GlobalVars.configStates[configKey]?.value?.toInt()
                ?: logAndRestart("Can not get $configKey")
            // 4. Implement logic based on the action value
            when (action) {
                0 -> {
                    // Action: Tap the "Reload" button
                    TouchActions.tap(379, 458)
                    TouchActions.tap(338, 511)//Tutorial "Confirm" button
                }

                1 -> {
                    // Action: Signal a need to switch accounts
                    // By returning false here, we tell the caller the check 'failed' or needs a different flow
                    return false
                }

                2 -> {
                    // Action: Wait/Idle
                    delay(1000)
                }
            }
            true // Match found and handled
        } else {
            true// No match for this schema
        }
    }


}

/**
 * Counts pixels in the given region where R, G, B are all greater than 235 (white).
 * Uses absolute ByteBuffer.get(index) so it is position-independent.
 */
private fun countWhitePixels(
    screenBuffer: ScreenCaptureManager.CaptureResult,
    x1: Int, y1: Int, x2: Int, y2: Int
): Int {
    val buf = screenBuffer.buffer
    val pixelStride = screenBuffer.pixelStride
    val rowStride = screenBuffer.rowStride
    var count = 0
    for (y in y1 until y2) {
        for (x in x1 until x2) {
            val offset = y * rowStride + x * pixelStride
            val r = buf.get(offset).toInt() and 0xFF
            val g = buf.get(offset + 1).toInt() and 0xFF
            val b = buf.get(offset + 2).toInt() and 0xFF
            if (r > 235 && g > 235 && b > 235) count++
        }
    }
    return count
}

private suspend fun checkPrivacy() {
    val point = findMultiColors(schema = MyColors.PrivacyInfo)
    if (point != null) {
        TouchActions.tap(point.x, point.y, delayTime = 200)
        reExtractGameSavings()
    }
}

suspend fun reExtractGameSavings() {
    // 1. Configuration items
    val packageName = "com.supercell.clashofclans"
    val folderName = "zkqGlobalGameSave"
    val targetSubDirs = listOf("shared_prefs")

    // 2. Environment path preparation
    val suffix = InGamesVars.currentAccountNumber
    val sdPath = Environment.getExternalStorageDirectory().path

    val gameRootDir = "$sdPath/zkqFiles/$folderName"
    val targetSaveDir = "$gameRootDir/$suffix"

    // 3. Core modification: if folder exists, delete it directly for re-extraction
    // Use [ -d ] to check if directory exists, if yes execute rm -rf
    val cleanCmd = "[ -d \"$targetSaveDir\" ] && rm -rf \"$targetSaveDir\""
    RunShell.run(cleanCmd)

    // 4. Create directory structure (need to recreate after rm)
    RunShell.run("mkdir -p \"$targetSaveDir\"")

    // 5. Execute data copy
    targetSubDirs.forEach { dir ->
        val destPath = "$targetSaveDir/$dir"
        // Create target subdirectory
        RunShell.run("mkdir -p \"$destPath\"")

        // 使用 Root 权限从 /data/data/ 复制到 SD 卡
        // 注意：这里拷贝的是内容，建议保留目录权限或结构
        val copyCmd = "cp -r /data/data/$packageName/$dir/* \"$destPath\""
        RunShell.run(copyCmd)
    }
}