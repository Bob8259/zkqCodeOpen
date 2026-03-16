package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.InGamesVars

suspend fun runGame() {
    // Launch component string is defined in GameVersion enum, keeping version-specific details centralized
    RunShell.runNoOutput("am start -n ${InGamesVars.currentGameVersion.launchComponent}")
    delayWithMultiplier(3000)
}

suspend fun killGame() {
    // Package name is defined in GameVersion enum, keeping version-specific details centralized
    ShowMessage("准备关闭游戏")
    killApp(InGamesVars.currentGameVersion.packageName)
    delayWithMultiplier(500)
}
