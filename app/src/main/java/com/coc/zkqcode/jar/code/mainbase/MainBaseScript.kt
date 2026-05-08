package com.coc.zkqcode.jar.code.mainbase


import com.coc.zkqcode.jar.code.clancapital.playClanCapital
import com.coc.zkqcode.jar.code.mainbase.attack.mainBaseAttack
import com.coc.zkqcode.jar.code.mainbase.attack.mainBaseRemainingAttack
import com.coc.zkqcode.jar.code.mainbase.attack.mainBaseTrainTroops
import com.coc.zkqcode.jar.code.mainbase.clan.clanLeague
import com.coc.zkqcode.jar.code.universal.clangame.clanGame
import com.coc.zkqcode.jar.code.mainbase.clan.clanWar
import com.coc.zkqcode.jar.code.mainbase.clan.donateToClan
import com.coc.zkqcode.jar.code.mainbase.clan.joinClan
import com.coc.zkqcode.jar.code.mainbase.clan.requestReinforcement
import com.coc.zkqcode.jar.code.mainbase.herohall.placeHeroBanners
import com.coc.zkqcode.jar.code.mainbase.herohall.upgradeGearsAndPets
import com.coc.zkqcode.jar.code.mainbase.daily.checkLeagueShop
import com.coc.zkqcode.jar.code.mainbase.others.checkRandomSchedule
import com.coc.zkqcode.jar.code.mainbase.daily.checkNormalShop
import com.coc.zkqcode.jar.code.mainbase.daily.collectClanCastleResources
import com.coc.zkqcode.jar.code.mainbase.daily.mainBaseCheckTutorials
import com.coc.zkqcode.jar.code.mainbase.daily.mainBaseCollectResources
import com.coc.zkqcode.jar.code.mainbase.daily.mainBaseRemoveObstacles
import com.coc.zkqcode.jar.code.mainbase.daily.useHelpers
import com.coc.zkqcode.jar.code.mainbase.daily.useMagicItems
import com.coc.zkqcode.jar.code.mainbase.research.mainBaseResearch
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.upgradeWallsThenBuildings
import com.coc.zkqcode.jar.code.universal.clangame.collectClanGameReward
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.enterMainBase

suspend fun playMainBase(): Boolean {
    if (!enterMainBase()) return false
    if (!checkRandomSchedule()) return false
    if (!playClanCapital()) return false
    if (!clanGame(BaseType.Main)) return false
    if (!mainBaseCollectResources()) return false
    if (!mainBaseTrainTroops()) return false
    if (!mainBaseAttack()) return false
    if (!collectClanGameReward()) return false
    if (!checkNormalShop()) return false
    if (!checkLeagueShop()) return false
    if (!clanWar()) return false
    if (!clanLeague()) return false
    if (!mainBaseRemoveObstacles()) return false
    if (!joinClan()) return false
    if (!requestReinforcement()) return false
    if (!donateToClan()) return false
    if (!upgradeWallsThenBuildings(BaseType.Main)) return false
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