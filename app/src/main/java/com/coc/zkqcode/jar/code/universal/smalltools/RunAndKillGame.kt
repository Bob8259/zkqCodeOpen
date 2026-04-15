package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.jar.code.universal.AccountMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.InGameVars

suspend fun runGame() {
    // Launch component string is defined in GameVersion enum, keeping version-specific details centralized
    RunShell.runNoOutput("am start -n ${InGameVars.currentGameVersion.launchComponent}")
    delayWithMultiplier(3000)
}

suspend fun killGame() {
    // Package name is defined in GameVersion enum, keeping version-specific details centralized
    AccountMessage("准备关闭游戏")
    killApp(InGameVars.currentGameVersion.packageName)
    delayWithMultiplier(500)
}
