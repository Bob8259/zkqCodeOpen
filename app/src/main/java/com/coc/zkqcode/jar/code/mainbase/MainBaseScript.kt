package com.coc.zkqcode.jar.code.mainbase


import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.mainbase.attack.mainBaseAttack
import com.coc.zkqcode.jar.code.mainbase.attack.mainBaseTrainTroops
import com.coc.zkqcode.jar.code.mainbase.clan.joinClan
import com.coc.zkqcode.jar.code.mainbase.clan.donateToClan
import com.coc.zkqcode.jar.code.mainbase.clan.requestReinforcements
import com.coc.zkqcode.jar.code.mainbase.herohall.placeHeroBanners
import com.coc.zkqcode.jar.code.mainbase.others.mainBaseRemoveObstacles
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.mainbase.research.mainBaseResearch
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.upgradeBuildings
import com.coc.zkqcode.jar.code.universal.buildings.walls.upgradeWalls
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.smalltools.enterMainBase

suspend fun playMainBase(): Boolean {
    if (!enterMainBase()) return false

    zoomSmallMainBase()
    if (!mainBaseTrainTroops()) return false
    if (!mainBaseAttack()) return false
    if (!mainBaseRemoveObstacles()) return false
    if (!joinClan()) return false
    if (!requestReinforcements()) return false
    if (!donateToClan()) return false
    if (!upgradeWalls(BaseType.Main)) return false
    if (!upgradeBuildings(BaseType.Main)) return false
    if (!mainBaseResearch()) return false
    if (!placeHeroBanners()) return false
//    if (!upgradeGears()) return false
    return true
}