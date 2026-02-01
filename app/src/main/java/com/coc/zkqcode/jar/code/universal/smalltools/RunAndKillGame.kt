package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.jar.code.universal.InGamesVars

suspend fun runGame() {
    when (InGamesVars.currentGamePackage) {
        0 -> {//国服
            runApp("com.tencent.tmgp.supercell.clashofclans")
        }
        1 -> {//国际服
            runApp("com.supercell.clashofclans")
        }
        2 -> {//私服
            runApp("com.supercell.clashofclans1")
        }
    }
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
            killApp("com.supercell.clashofclans1")
        }
    }
}
