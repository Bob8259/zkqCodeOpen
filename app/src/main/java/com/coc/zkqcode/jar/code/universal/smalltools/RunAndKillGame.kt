package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.InGamesVars

suspend fun runGame() {
    when (InGamesVars.currentGamePackage) {
        0 -> {//国服
            RunShell.runNoOutput("am start -n com.tencent.tmgp.supercell.clashofclans/com.supercell.titan.tencent.GameAppTencent")
        }
        1 -> {//国际服
            RunShell.runNoOutput("am start -n com.supercell.clashofclans/com.supercell.titan.GameApp")
        }
        2 -> {//私服
            RunShell.runNoOutput("am start -n com.supercell.clashofclans2/com.atrasis.main.GameMain")
        }
    }
    delayWithMultiplier(5000)
}

suspend fun killGame() {
    when (InGamesVars.currentGamePackage) {
        0 -> {//国服
            killApp("com.tencent.tmgp.supercell.clashofclans")
        }
        1 -> {//国际服
            killApp("com.supercell.clashofclans")
        }
        2 -> {//私服
            killApp("com.supercell.clashofclans2")
        }
    }
    delayWithMultiplier(1000)
}
