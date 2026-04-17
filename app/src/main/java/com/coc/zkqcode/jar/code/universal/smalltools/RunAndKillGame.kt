package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.InGameVars

suspend fun runGame() {
    // Launch component string is defined in GameVersion enum, keeping version-specific details centralized
    RunShell.runNoOutput("am start -n ${InGameVars.currentGameVersion.launchComponent}")
    delayWithMultiplier(3000)
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
