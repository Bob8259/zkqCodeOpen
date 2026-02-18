package com.coc.zkqcode.jar.code.universal.tutorial

import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors

object BuilderBaseTutorial {
    suspend fun builderBaseTutorial(): Boolean {
        // 1. Upgrade Night TH
        findMultiColors(schema = MyColors.UpgradeNightTH)?.let {
            TouchActions.tap(it.x, it.y, delayTime = 500)
        }

        // 2. Builder Master - Specific coordinate taps
        if (findMultiColors(schema = MyColors.BuilderMaster) != null) {
            TouchActions.tap(615, 216, delayTime = 500)
            TouchActions.tap(706, 555, delayTime = 500)
            TouchActions.tap(685, 46, delayTime = 500)
        }

        // 3. Tutorial Night Barb
        findMultiColors(schema = MyColors.TutorialNightBarb)?.let {
            TouchActions.tap(it.x, it.y, delayTime = 500)
            TouchActions.tap(952, 618, delayTime = 500)
            TouchActions.tap(979, 196, delayTime = 500)//use gem to speed up
        }

        // 4. Night Attack
        findMultiColors(schema = MyColors.NightAttack)?.let {
            TouchActions.tap(it.x, it.y, delayTime = 500)
            TouchActions.tap(938, 465, delayTime = 500)
        }

        // 5. Night Deploy Barbs - Multiple taps at the same location
        if (findMultiColors(schema = MyColors.NightDeployBarbs) != null) {
            repeat(3) {
                TouchActions.tap(436, 464, delayTime = 500)
            }
        }

        // 6. Back To Camp
        findMultiColors(schema = MyColors.BuilderBackToCamp)?.let {
            TouchActions.tap(it.x, it.y, delayTime = 500)
        }

        return false
    }
}