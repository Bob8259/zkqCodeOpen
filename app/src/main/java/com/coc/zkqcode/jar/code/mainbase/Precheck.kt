package com.coc.zkqcode.jar.code.mainbase


import com.coc.zkqcode.core.util.basic.findMultiColors
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema

class Precheck {
    suspend fun claimAchievement(currentAccountNumber: Int): Boolean {
        val isClaimAchievement = getConfigRuntime(
            currentAccountNumber,
            Schema.MAIN_BASE_SETTINGS.CLAIM_ACHIEVEMENT_GEMS.key
        ) == "1"
        if (isClaimAchievement) {
            val point = findMultiColors(schema = MyColors.Achievement)
            if (point != null) {
                claimAchievementHelper()
            }
        }
        return true
    }
    private suspend fun claimAchievementHelper(){
        TouchActions.tap(51,45)

    }
}
