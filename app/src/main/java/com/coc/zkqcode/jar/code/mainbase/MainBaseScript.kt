package com.coc.zkqcode.jar.code.mainbase


import com.coc.zkqcode.jar.code.mainbase.attack.mainBaseAttack
import com.coc.zkqcode.jar.code.mainbase.attack.mainBaseTrainTroops
import com.coc.zkqcode.jar.code.mainbase.others.mainBaseRemoveObstacles
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.mainbase.research.mainBaseResearch
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.upgradeBuildings
import com.coc.zkqcode.jar.code.universal.smalltools.enterMainBase

suspend fun playMainBase(): Boolean {
    if (!enterMainBase()) return false
    zoomSmallMainBase()
    if (!mainBaseTrainTroops()) return false
    if (!mainBaseAttack()) return false
    if (!mainBaseRemoveObstacles()) return false
    if (!upgradeBuildings(BaseType.Main)) return false
    if (!mainBaseResearch()) return false

    return true
}