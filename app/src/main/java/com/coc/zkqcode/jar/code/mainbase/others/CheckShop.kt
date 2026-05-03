package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun checkShop(): Boolean {
    // If all shop reward and raid medal purchase options are disabled, skip directly
    val isClaimFreeRewards = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.CLAIM_FREE_SHOP_REWARDS.key)
    val isBuyGoldOre = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.BUY_GOLD_ORE_WITH_RAID_MEDAL.key)
    val isBuyPurpleOre = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.BUY_PURPLE_ORE_WITH_RAID_MEDAL.key)
    val isBuyClockTowerPotion = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.BUY_CLOCK_TOWER_POTION_WITH_RAID_MEDAL.key)
    val isBuyRingOfWall = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.BUY_RING_OF_WALL_WITH_RAID_MEDAL.key)
    val isBuyResearchPotion = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.BUY_RESEARCH_POTION_WITH_RAID_MEDAL.key)
    val isBuyResourcePotion = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.BUY_RESOURCE_POTION_WITH_RAID_MEDAL.key)

    if (!isClaimFreeRewards && !isBuyGoldOre && !isBuyPurpleOre && !isBuyClockTowerPotion && !isBuyRingOfWall && !isBuyResearchPotion && !isBuyResourcePotion) {
        return true
    }
    zoomSmallMainBase(isForAttack = true)
    TouchActions.tap(255, 230)
    if (findMultiColorsUntil(listOf(MyColors.Merchant), 5000) != null) {
        accountMessage("已进入商店")

    }
    return enterMainScreen()
}

private suspend fun buyItems() {
    if (findMultiColors(MyColors.GemInShop) != null) {
        TouchActions.tap(490, 270)
    }
    if (findMultiColors(MyColors.RaidMedalInShop) != null) {
        val goldOre=findMultiColors(MyColors.GoldOre)
        if (goldOre!=null){
            
        }
    }
}