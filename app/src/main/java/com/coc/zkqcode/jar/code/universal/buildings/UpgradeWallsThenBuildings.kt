package com.coc.zkqcode.jar.code.universal.buildings

import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.upgradeBuildings
import com.coc.zkqcode.jar.code.universal.buildings.walls.upgradeWalls

suspend fun upgradeWallsThenBuildings(currentBase: BaseType): Boolean {
    InGameVars.wallUpgradeNoWorkerBase = null
    return try {
        if (!upgradeWalls(currentBase)) return false
        if (InGameVars.wallUpgradeNoWorkerBase == currentBase) return true
        upgradeBuildings(currentBase)
    } finally {
        InGameVars.wallUpgradeNoWorkerBase = null
    }
}
