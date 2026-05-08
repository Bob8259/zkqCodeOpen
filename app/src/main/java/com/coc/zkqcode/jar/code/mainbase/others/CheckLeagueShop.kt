package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
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

// Data-driven list of league shop items to purchase
private data class LeagueShopItem(
    val configKey: String,
    val colorSchemas: List<ColorSchema>,
)

private val leagueShopItems = listOf(
    LeagueShopItem(
        configKey = Schema.MAIN_BASE_SETTINGS.BUY_BUILDER_POTION_WITH_LEAGUE_MEDAL.key,
        colorSchemas = listOf(MyColors.LeagueShopBuilderPotion1, MyColors.LeagueShopBuilderPotion2),
    ),
    LeagueShopItem(
        configKey = Schema.MAIN_BASE_SETTINGS.BUY_RESEARCH_POTION_WITH_LEAGUE_MEDAL.key,
        colorSchemas = listOf(MyColors.LeagueShopResearchPotion1, MyColors.LeagueShopResearchPotion2),
    ),
)

suspend fun checkLeagueShop(): Boolean {
    // Time guard: only check league shop once per 3 days (4320 minutes)
    val memoryKey = StorageKeys.withAccountNumber(StorageKeys.CHECK_LEAGUE_SHOP, InGameVars.currentAccountNumber)
    if (!checkMemoryFile(memoryKey, 4320)) {
        accountMessage("联赛商店检查冷却中，跳过")
        return true
    }

    if (leagueShopItems.none { getBooleanConfigRuntime(it.configKey) } || InGameVars.townHallLevel < 3) return true
    val shopButton = findMultiColors(MyColors.ShopButton) ?: return enterMainScreen()
    TouchActions.tap(shopButton.x, shopButton.y)
    if (enterLeagueShopPage()) {
        leagueShopItems.forEach { buyLeagueShopItem(it) }
    }
    // Record current time so we don't check again within 3 days
    writeMemory(memoryKey, (System.currentTimeMillis() / 60_000).toString())
    return enterMainScreen()
}

// Find the item in the shop, tap it, and confirm the purchase
private suspend fun buyLeagueShopItem(item: LeagueShopItem) {
    if (!getBooleanConfigRuntime(item.configKey)) return
    val potion = findMultiColorsUntil(item.colorSchemas, 200) ?: return
    TouchActions.tap(potion.x, potion.y)
    val confirmButton = findMultiColors(MyColors.ClanLeagueConfirmBuy) ?: return
    TouchActions.tap(confirmButton.x, confirmButton.y)
}

private suspend fun enterLeagueShopPage(): Boolean {
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < 5000) {
        val oldVersion = findMultiColors(MyColors.OldVersion)
        if (oldVersion != null) {
            TouchActions.tap(oldVersion.x, oldVersion.y)
        }
        val leaguePageButton = findMultiColorsUntil(listOf(MyColors.LeaguePageButton, MyColors.LeaguePageButton2), 50)
        if (leaguePageButton != null) {
            TouchActions.tap(leaguePageButton.x, leaguePageButton.y)
            TouchActions.swipe(1000, 470, 200, 470, delayAfter = 300)
            return true
        }
        delayWithMultiplier(200)
    }
    return false
}
