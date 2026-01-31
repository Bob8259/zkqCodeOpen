package com.coc.zkqcode.jar.code.universal.smalltools

import android.os.Environment
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.basic.findMultiColors
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.delay


suspend fun checkReconnections(): Boolean {
    // 1. Capture the screen and cast safely

    checkPrivacy()

    // 2. Define the schemas to check against
    val homeSchemas = listOf(
        MyColors.Reconnection
    )
    val screenBuffer =
        ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            ?: return true
    // 3. Run the check and capture the result
    // We return the result of 'any' to determine if a reconnection event occurred.
    return homeSchemas.any { schema ->
        val match = findMultiColors(byteBuffer = screenBuffer, schema = schema)
        if (match != null) {
            // Retrieve the configuration state for the specific account
            val configKey = Schema.GLOBAL_SETTINGS.AFTER_KICK_OPTION.key
            val action = GlobalVars.configStates[configKey]?.value?.toInt()
                ?: logAndStop("Can not get $configKey")
            // 4. Implement logic based on the action value
            when (action) {
                0 -> {
                    // Action: Tap the "Reload" button
                    TouchActions.tap(379, 458)
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

private suspend fun checkPrivacy() {
    val point = findMultiColors(schema = MyColors.PrivacyInfo)
    if (point != null) {
        TouchActions.tap(point.x, point.y)
        delayWithMultiplier(200)
        reExtractGameSavings()
    }
}

private suspend fun reExtractGameSavings() {
    // 1. 硬编码配置项
    val packageName = "com.supercell.clashofclans"
    val folderName = "zkqGlobalGameSave"
    val targetSubDirs = listOf("shared_prefs")

    // 2. 环境路径准备
    val suffix = InGamesVars.currentAccountNumber
    val sdPath = Environment.getExternalStorageDirectory().path

    val gameRootDir = "$sdPath/zkqFiles/$folderName"
    val targetSaveDir = "$gameRootDir/$suffix"

    // 3. 检查存档是否已存在，若存在则直接跳过
    val checkExistCmd = "[ -d \"$targetSaveDir/shared_prefs\" ]"
    if (Shell.cmd(checkExistCmd).exec().isSuccess) {
        return
    }

    // 4. 静默创建目录结构
    RunShell.run("mkdir -p \"$gameRootDir\"")
    RunShell.run("mkdir -p \"$targetSaveDir\"")

    // 5. 执行数据拷贝
    targetSubDirs.forEach { dir ->
        val destPath = "$targetSaveDir/$dir"
        // 创建目标子目录
        RunShell.run("mkdir -p \"$destPath\"")
        // 使用 Root 权限从 /data/data/ 复制到 SD 卡
        val copyCmd = "cp -r /data/data/$packageName/$dir/* \"$destPath\""
        RunShell.run(copyCmd)
    }
}