package com.coc.zkqcode.jar.code.universal.buildings.gearup

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.iterateBuilderBaseBuildingUpgradeList
import com.coc.zkqcode.jar.code.universal.buildings.tapGoldBar
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.checkContinueBuild
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.enterBuilderBase
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.enterMainBase
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun gearUpBuildings(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.GEAR_UP_SETTINGS.key)) return true

    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.UPGRADE_GEARS, InGameVars.currentAccountNumber
    )
    if (!checkMemoryFile(storageKey, 2880)) {
        accountMessage("2天内已执行过改装建筑检查，跳过本次扫描")
        return true
    }

    val result = performGearUpBuildings()

    return result
}

private suspend fun performGearUpBuildings(): Boolean {
    if (!enterBuilderBase(true)) return false
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
        schemas = MyColors.MainBaseWorkerSchemas, duration = 1000
    )

    if (worker == null) {
        accountMessage("未找到工人")
        return enterMainScreen()
    }

    TouchActions.tap(worker.x, worker.y)

    iterateBuilderBaseBuildingUpgradeList(
        baseType = BaseType.Main, excludeNewBuildings = false, excludeGearUpBuildings = false, onDetect = { result ->
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
                        schemas = listOf(MyColors.BuildingGearUpButton), duration = 1000
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
        })
    //If workers are available, then we should record the time regardless of whether a building that can be geared up is found or not.
    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.UPGRADE_GEARS, InGameVars.currentAccountNumber
    )
    writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
    return enterMainScreen()
}
