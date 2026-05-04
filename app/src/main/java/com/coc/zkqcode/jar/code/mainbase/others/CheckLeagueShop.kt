package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun checkLeagueShop(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.BUY_BUILDER_POTION_WITH_LEAGUE_MEDAL.key) && !getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.BUY_RESEARCH_POTION_WITH_LEAGUE_MEDAL.key)) return true
    val shopButton = findMultiColors(MyColors.ShopButton)
    if (shopButton != null) {
        TouchActions.tap(shopButton.x, shopButton.y)
        if (enterLeagueShopPage()) {
            delayWithMultiplier(100000)
        }

    }
    return enterMainScreen()
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
            TouchActions.swipe(1195, 470, 100, 470, delayAfter = 300)
            return true
        }
        delayWithMultiplier(200)
    }
    return false
}