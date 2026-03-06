package com.coc.zkqcode.jar.code.universal.tutorial

import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors

    suspend fun builderBaseTutorial(): Boolean {
        // 1. Upgrade Night TH
        listOf(
            MyColors.UpgradeBuilderBaseTH,
            MyColors.UpgradeBarbarian,
            MyColors.BuilderMaster
        ).forEach { schema ->
            findMultiColors(schema = schema)?.let {
                TouchActions.tap(it.x, it.y, delayTime = 500)
            }
        }
        // 2. Builder Master - Specific coordinate taps
        findMultiColors(schema = MyColors.UpgradeStarLab)?.let {
            TouchActions.tap(it.x - 80, it.y + 100, delayTime = 500)
            TouchActions.tap(707, 558, delayTime = 500)
        }
        findMultiColors(schema = MyColors.BuilderBaseWorker)?.let {
            TouchActions.tap(it.x, it.y, delayTime = 500)
        }
        // 3. Tutorial Night Barb
        findMultiColors(schema = MyColors.TutorialBuilderBaseBarb)?.let {
            TouchActions.tap(it.x, it.y, delayTime = 500)
            TouchActions.tap(952, 618, delayTime = 500)
            TouchActions.tap(979, 196, delayTime = 500)//use gem to speed up
        }

        // 4. Night Attack
        findMultiColors(schema = MyColors.BuilderBaseAttack)?.let {
            TouchActions.tap(it.x, it.y, delayTime = 500)
            TouchActions.tap(938, 465, delayTime = 500)
        }

        // 5. Night Deploy Barbs - Multiple taps at the same location
        if (findMultiColors(schema = MyColors.BuilderBaseDeployBarbs) != null) {
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
