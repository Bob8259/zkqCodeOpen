package com.coc.zkqcode.jar.code.universal.buildings.gearup

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.iterateBuilderBaseBuildingUpgradeList
import com.coc.zkqcode.jar.code.universal.buildings.tapGoldBar
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.checkContinueBuild
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.enterBuilderBase
import com.coc.zkqcode.jar.code.universal.smalltools.enterMainBase
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun gearUpBuildings(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.GEAR_UP_SETTINGS.key)) return true
    if (!enterBuilderBase(true)) return true
    if (!upgradeGearUpBuildings()) return false
    return enterBuilderBase(false)
}

private suspend fun upgradeGearUpBuildings(): Boolean {
    accountMessage("开始查找可改装建筑")
    clickRightBottom(1)

    if (!checkContinueBuild(BaseType.Builder)) {
        return enterMainScreen()
    }
    if (!enterMainBase()) return true

    val worker = findMultiColorsUntil(
        schemas = listOf(MyColors.MainBaseWorker, MyColors.MainBaseWorker2, MyColors.MainBaseWorker3),
        duration = 1000
    )

    if (worker == null) {
        accountMessage("未找到工人")
        return enterMainScreen()
    }

    TouchActions.tap(worker.x, worker.y)

    iterateBuilderBaseBuildingUpgradeList(
        excludeNewBuildings = false,
        excludeGearUpBuildings = false,
        onDetect = { result ->
            val buildings = result.buildings
            if (buildings.isEmpty()) {
                accountMessage("未检测到可改装建筑")
                false
            } else {
                val gearUpBuilding = buildings.find { it.name.startsWith("改装") }
                if (gearUpBuilding != null) {
                    accountMessage("检测到改装建筑: ${gearUpBuilding.name}，立即点击")
                    TouchActions.tap(gearUpBuilding.x + 20, gearUpBuilding.y + 5)
                    tapGoldBar()
                    delayWithMultiplier(300)
                    val gearUpButton = findMultiColorsUntil(
                        schemas = listOf(MyColors.BuildingGearUpButton),
                        duration = 1000
                    )
                    if (gearUpButton != null) {
                        TouchActions.tap(gearUpButton.x, gearUpButton.y)
                        TouchActions.tap(680, 550)
                        clickRightBottom(1)
                    }
                    true
                } else {
                    false
                }
            }
        }
    )

    return enterMainScreen()
}
