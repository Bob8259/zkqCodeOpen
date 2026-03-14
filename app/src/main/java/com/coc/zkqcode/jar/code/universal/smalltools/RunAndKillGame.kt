package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.InGamesVars

suspend fun runGame() {
    // Launch component string is defined in GameVersion enum, keeping version-specific details centralised
    RunShell.runNoOutput("am start -n ${InGamesVars.currentGameVersion.launchComponent}")
    delayWithMultiplier(3000)
}

suspend fun killGame() {
    // Package name is defined in GameVersion enum, keeping version-specific details centralised
    killApp(InGamesVars.currentGameVersion.packageName)
    delayWithMultiplier(1000)
}
