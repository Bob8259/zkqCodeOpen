package com.coc.zkqcode.jar.code.universal.smalltools.lifecycle

import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.smalltools.data.getStaticConfig

suspend fun runGame() {
    // Launch component string is defined in GameVersion enum, keeping version-specific details centralized
    val command = "am start -n ${InGameVars.currentGameVersion.launchComponent}"
    showDebugInfo("Run game command: $command")
    RunShell.runNoOutput(command)
    delayWithMultiplier(3000)
}

suspend fun killApp(packageName: String) {
    val command = "am force-stop $packageName"
    showDebugInfo("Kill game command: $command")
    RunShell.runNoOutput(command)
}

suspend fun killGame() {
    // If disable_restart_game is enabled, skip killing the game
    if (getStaticConfig("disable_restart_game") == "1") {
        accountMessage("已禁用重启游戏")
    } else {
        // Package name is defined in GameVersion enum, keeping version-specific details centralized
        accountMessage("准备关闭游戏")
        killApp(InGameVars.currentGameVersion.packageName)
    }
    delayWithMultiplier(500)
}
