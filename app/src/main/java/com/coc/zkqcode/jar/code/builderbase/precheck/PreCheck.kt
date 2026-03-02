package com.coc.zkqcode.jar.code.mainbase.precheck

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema


    suspend fun claimAchievement(): Boolean {
        val isClaimAchievement = getBooleanConfigRuntime(
            Schema.MAIN_BASE_SETTINGS.CLAIM_ACHIEVEMENT_GEMS.key
        )
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
        TouchActions.tap(51, 45, delayTime = 2000)
        val point = findMultiColorsUntil(schemas = listOf(MyColors.ClaimAchievement), duration = 200)
        if (point != null)
            TouchActions.tap(point.x, point.y)
    }
