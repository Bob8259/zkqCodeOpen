package com.coc.zkqcode.jar.code.mainbase


import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.basic.findMultiColors
import com.coc.zkqcode.core.util.basic.findMultiColorsUntil
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.EnterTargetBase
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema

class PreCheck {
    suspend fun doAllPreChecks(): Boolean {
        if (!claimAchievement()) return false
        val noNightBase = getConfigRuntime(
            InGamesVars.currentAccountNumber,
            Schema.NIGHT_BASE_SETTINGS.NO_BUILDER_BASE.key
        ) == "1"
        if (!noNightBase) {
            EnterTargetBase().enterNightBase(true)//check if night base is unlocked or not.
        }
        return true
    }

    suspend fun claimAchievement(): Boolean {
        val isClaimAchievement = getConfigRuntime(
            InGamesVars.currentAccountNumber,
            Schema.MAIN_BASE_SETTINGS.CLAIM_ACHIEVEMENT_GEMS.key
        ) == "1"
        if (isClaimAchievement) {
            val point = findMultiColors(schema = MyColors.Achievement)
            if (point != null) {
                claimAchievementHelper()
                if (!enterMainScreen()) return false
            }
        }
        return true
    }

    private suspend fun claimAchievementHelper() {
        TouchActions.tap(51, 45)
        delayWithMultiplier(200)
        val point = findMultiColorsUntil(schema = MyColors.ClaimAchievement, duration = 3000)
        if (point != null)
            TouchActions.tap(point.x, point.y)

    }
}
