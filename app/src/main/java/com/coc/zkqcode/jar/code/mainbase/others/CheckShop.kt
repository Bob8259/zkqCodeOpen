package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun checkShop(): Boolean {
    // If all shop reward and raid medal purchase options are disabled, skip directly
    val isClaimFreeRewards = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.CLAIM_FREE_SHOP_REWARDS.key)
    val isBuyStarOre = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.BUY_STAR_ORE_WITH_RAID_MEDAL.key)
    val isBuyClockTowerPotion = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.BUY_CLOCK_TOWER_POTION_WITH_RAID_MEDAL.key)
    val isBuyRingOfWall = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.BUY_RING_OF_WALL_WITH_RAID_MEDAL.key)
    val isBuyResearchPotion = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.BUY_RESEARCH_POTION_WITH_RAID_MEDAL.key)
    val isBuyResourcePotion = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.BUY_RESOURCE_POTION_WITH_RAID_MEDAL.key)

    if (!isClaimFreeRewards && !isBuyStarOre && !isBuyClockTowerPotion && !isBuyRingOfWall && !isBuyResearchPotion && !isBuyResourcePotion) {
        return true
    }
    zoomSmallMainBase(isForAttack = true)
    TouchActions.tap(255, 230)
    
    return enterMainScreen()
}