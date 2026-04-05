package com.coc.zkqcode.jar.code.universal.tutorial

import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil

    suspend fun builderBaseTutorial(): Boolean {
        // 1. Upgrade Builder Base TH
        listOf(
            MyColors.UpgradeBuilderBaseTH,
            MyColors.UpgradeBarbarian,
            MyColors.BuilderMaster
        ).forEach { schema ->
            findMultiColors(schema = schema, increment = 1)?.let {
                TouchActions.tap(it.x, it.y, delayTime = 500)
            }
        }
        // 2. Builder Master - Specific coordinate taps
        findMultiColors(schema = MyColors.UpgradeStarLab, increment = 1)?.let {
            TouchActions.tap(it.x - 80, it.y + 100, delayTime = 500)
            TouchActions.tap(707, 558, delayTime = 500)
        }
        findMultiColorsUntil(schemas = listOf(MyColors.BuilderBaseWorker, MyColors.BuilderBaseWorker2), duration = 200, increment = 1)?.let {
            TouchActions.tap(it.x, it.y, delayTime = 500)
        }
        // 3. Tutorial Builder Base Barb
        findMultiColors(schema = MyColors.TutorialBuilderBaseBarb, increment = 1)?.let {
            TouchActions.tap(it.x, it.y, delayTime = 500)
            TouchActions.tap(952, 618, delayTime = 500)
            TouchActions.tap(979, 196, delayTime = 500)//use gem to speed up
        }

        // 4. Builder Base Attack
        findMultiColors(schema = MyColors.BuilderBaseAttack, increment = 1)?.let {
            TouchActions.tap(it.x, it.y, delayTime = 500)
            TouchActions.tap(938, 465, delayTime = 500)
        }

        // 5. Builder Base Deploy Barbs - Multiple taps at the same location
        if (findMultiColors(schema = MyColors.BuilderBaseDeployBarbs, increment = 1) != null) {
            repeat(3) {
                TouchActions.tap(436, 464, delayTime = 500)
            }
        }

        // 6. Back To Camp
        findMultiColors(schema = MyColors.BuilderBackToCamp, increment = 1)?.let {
            TouchActions.tap(it.x, it.y, delayTime = 500)
        }

        return false
    }
