package com.coc.zkqcode.jar.code.mainbase


import com.coc.zkqcode.jar.code.clancapital.playClanCapital
import com.coc.zkqcode.jar.code.mainbase.attack.mainBaseAttack
import com.coc.zkqcode.jar.code.mainbase.attack.mainBaseRemainingAttack
import com.coc.zkqcode.jar.code.mainbase.attack.mainBaseTrainTroops
import com.coc.zkqcode.jar.code.mainbase.clan.clanGame
import com.coc.zkqcode.jar.code.mainbase.clan.clanWar
import com.coc.zkqcode.jar.code.mainbase.clan.donateToClan
import com.coc.zkqcode.jar.code.mainbase.clan.joinClan
import com.coc.zkqcode.jar.code.mainbase.clan.requestReinforcement
import com.coc.zkqcode.jar.code.mainbase.herohall.placeHeroBanners
import com.coc.zkqcode.jar.code.mainbase.herohall.upgradeGearsAndPets
import com.coc.zkqcode.jar.code.mainbase.others.checkRandomSchedule
import com.coc.zkqcode.jar.code.mainbase.others.collectClanCastleResources
import com.coc.zkqcode.jar.code.mainbase.others.mainBaseCheckTutorials
import com.coc.zkqcode.jar.code.mainbase.others.mainBaseCollectResources
import com.coc.zkqcode.jar.code.mainbase.others.mainBaseRemoveObstacles
import com.coc.zkqcode.jar.code.mainbase.others.useHelpers
import com.coc.zkqcode.jar.code.mainbase.others.useMagicItems
import com.coc.zkqcode.jar.code.mainbase.research.mainBaseResearch
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.upgradeBuildings
import com.coc.zkqcode.jar.code.universal.buildings.walls.upgradeWalls
import com.coc.zkqcode.jar.code.universal.smalltools.enterMainBase

suspend fun playMainBase(): Boolean {
    if (!enterMainBase()) return false
    if (!checkRandomSchedule()) return false
    if (!playClanCapital()) return false
    if (!clanGame()) return true
    if (!mainBaseCollectResources()) return false
    if (!mainBaseTrainTroops()) return false
    if (!mainBaseAttack()) return false
    if (!clanWar()) return false
    if (!mainBaseRemoveObstacles()) return false
    if (!joinClan()) return false
    if (!requestReinforcement()) return false
    if (!donateToClan()) return false
    if (!upgradeWalls(BaseType.Main)) return false
    if (!upgradeBuildings(BaseType.Main)) return false
    if (!mainBaseResearch()) return false
    if (!placeHeroBanners()) return false
    if (!mainBaseCheckTutorials()) return false
    if (!upgradeGearsAndPets()) return false
    if (!collectClanCastleResources()) return false
    if (!useHelpers()) return false
    if (!useMagicItems()) return false
    if (!mainBaseRemainingAttack()) return false
    return true
}