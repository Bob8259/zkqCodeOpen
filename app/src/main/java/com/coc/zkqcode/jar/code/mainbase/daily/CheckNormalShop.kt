package com.coc.zkqcode.jar.code.mainbase.daily

import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun checkNormalShop(): Boolean {
    // Time guard: only check shop once per 1 day (1440 minutes)
    val memoryKey = StorageKeys.withAccountNumber(StorageKeys.CHECK_SHOP, InGameVars.currentAccountNumber)
    if (!checkMemoryFile(memoryKey, 1440)) {
        accountMessage("商店检查冷却中，跳过")
        return true
    }

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
        checkShopLoop()
        TouchActions.tap(200, 240)
        TouchActions.swipe(750, 300, 750, 6000, delayAfter = 300)
        checkShopLoop()
        TouchActions.tap(200, 300)
        TouchActions.swipe(750, 300, 750, 6000, delayAfter = 300)
        checkShopLoop()
    }
    // Record current time so we don't check again within 2 days
    writeMemory(memoryKey, (System.currentTimeMillis() / 60_000).toString())
    return enterMainScreen()
}

private suspend fun checkShopLoop() {
    for (i in 0..4) {
        if (buyItems()) break
        TouchActions.swipe(750, 650, 750, 200)
    }
}

private suspend fun buyItems(): Boolean {
    if (findMultiColors(MyColors.GemInShop) != null) {
        TouchActions.tap(490, 270)
        return true
    }
    if (findMultiColors(MyColors.RaidMedalInShop) != null) {
        val raidMedalItems = listOf(
            Schema.MAIN_BASE_SETTINGS.BUY_GOLD_ORE_WITH_RAID_MEDAL.key to MyColors.GoldOre,
            Schema.MAIN_BASE_SETTINGS.BUY_PURPLE_ORE_WITH_RAID_MEDAL.key to MyColors.PurpleOre,
            Schema.MAIN_BASE_SETTINGS.BUY_CLOCK_TOWER_POTION_WITH_RAID_MEDAL.key to MyColors.ClockTowerPotionInShop,
            Schema.MAIN_BASE_SETTINGS.BUY_RING_OF_WALL_WITH_RAID_MEDAL.key to MyColors.RingOfWall,
            Schema.MAIN_BASE_SETTINGS.BUY_RESEARCH_POTION_WITH_RAID_MEDAL.key to MyColors.ResearchPotionInShop,
            Schema.MAIN_BASE_SETTINGS.BUY_RESOURCE_POTION_WITH_RAID_MEDAL.key to MyColors.ResourcePotionInShop,
        )
        for ((configKey, itemSchema) in raidMedalItems) {
            if (!getBooleanConfigRuntime(configKey)) continue
            val item = findMultiColors(itemSchema) ?: continue
            val insufficientSchema = ColorSchema.rescope(
                MyColors.InsufficientMedal, item.x - 50, item.y + 40, item.x + 50, item.y + 150
            )
            if (findMultiColors(insufficientSchema) != null) {
                accountMessage("${itemSchema.name}突袭币不足")
            } else {
                accountMessage("尝试购买${itemSchema.name}")
                TouchActions.tap(item.x, item.y)
                if (findMultiColors(MyColors.ConfirmBuyWithRaidMedal) != null) {
                    TouchActions.tap(690, 460)
                }
            }
        }
    }
    return false
}