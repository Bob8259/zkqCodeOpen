package com.coc.zkqcode.jar.code.universal.tutorial

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors

object BuilderBaseTutorial {
    suspend fun builderBaseTutorial(): Boolean {
        // 1. Upgrade Night TH
        findMultiColors(schema = MyColors.UpgradeNightTH)?.let {
            TouchActions.tap(it.x, it.y)
            delayWithMultiplier(500)
        }

        // 2. Builder Master - Specific coordinate taps
        if (findMultiColors(schema = MyColors.BuilderMaster) != null) {
            TouchActions.tap(615, 216)
            delayWithMultiplier(500)
            TouchActions.tap(706, 555)
            delayWithMultiplier(500)
            TouchActions.tap(685, 46)
            delayWithMultiplier(500)
        }

        // 3. Tutorial Night Barb
        findMultiColors(schema = MyColors.TutorialNightBarb)?.let {
            TouchActions.tap(it.x, it.y)
            delayWithMultiplier(500)
            TouchActions.tap(952, 618)
            delayWithMultiplier(500)
            TouchActions.tap(979, 196)//use gem to speed up
            delayWithMultiplier(500)
        }

        // 4. Night Attack
        findMultiColors(schema = MyColors.NightAttack)?.let {
            TouchActions.tap(it.x, it.y)
            delayWithMultiplier(500)
            TouchActions.tap(938, 465)
            delayWithMultiplier(500)
        }

        // 5. Night Deploy Barbs - Multiple taps at the same location
        if (findMultiColors(schema = MyColors.NightDeployBarbs) != null) {
            repeat(3) {
                TouchActions.tap(436, 464)
                delayWithMultiplier(500)
            }
        }

        // 6. Back To Camp
        findMultiColors(schema = MyColors.NightBackToCamp)?.let {
            TouchActions.tap(it.x, it.y)
            delayWithMultiplier(500)
        }

        return false
    }
}