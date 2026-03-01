package com.coc.zkqcode.jar.code.universal.buildings.upgrade

import android.graphics.Bitmap
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.yolo.YoloDetector
import com.coc.zkqcode.jar.code.builderbase.others.BuilderBaseWorkerAndResearch
import com.coc.zkqcode.jar.code.builderbase.others.zoomSmallBuilderBase
import com.coc.zkqcode.jar.code.builderbase.upgrade.builderBaseFindBuildButton
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.MainBaseWorkerAndResearch
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.mainbase.upgrade.mainBaseFindBuildButton
import com.coc.zkqcode.jar.code.universal.buildings.ALL_BUILDINGS
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlin.math.sqrt


private val upgradableBuildingsMap = ALL_BUILDINGS.associateWith { false }.toMutableMap()

enum class BaseType {
    Builder, Main
}

suspend fun builderBaseUpgradeBuildings(currentBase: BaseType): Boolean {
    upgradableBuildingsMap.keys.forEach { upgradableBuildingsMap[it] = false }
    var isNewBuildingDetected = false
    clickRightBottom(1)
    if (checkContinueBuild(currentBase)) {
        val worker = when (currentBase) {
            BaseType.Builder -> findMultiColorsUntil(schemas = listOf(MyColors.BuilderBaseWorker), duration = 1000)

            BaseType.Main -> findMultiColorsUntil(schemas = listOf(MyColors.MainBaseWorker), duration = 1000)
        }
        if (worker != null) {
            TouchActions.tap(worker.x, worker.y, delayTime = 500)
            iterateBuilderBaseBuildingUpgradeList(onDetect = { result ->
                val buildings = result.buildings
                if (buildings.isEmpty()) {
                    ShowMessage("未检测到可升级建筑")
                } else {
                    buildings.forEach { building ->
                        if (upgradableBuildingsMap.containsKey(building.name)) {
                            upgradableBuildingsMap[building.name] = true
                        }
                        if (building.name.startsWith("新")) {
                            isNewBuildingDetected = true
                        }
                    }
                    val info = buildings.chunked(4).joinToString("\n") { chunk ->
                        chunk.joinToString(" ") { it.name }
                    }
                    ShowMessage("检测到 ${buildings.size} 个建筑:\n$info")
                }
                isNewBuildingDetected
            })
            val upgradableList = upgradableBuildingsMap.filter { it.value }.keys.toList()
            val summary = upgradableList.joinToString("\n")
            ShowMessage("所有可升级建筑: $summary")
            if (isNewBuildingDetected) {
                if (!buildAllNewBuildings(currentBase)) return false
                builderBaseUpgradeBuildings(currentBase)
            } else {
                if (!UpgradeExistingBuildings.upgradeAllExistingBuildings(upgradableList, currentBase)) return false
            }

        }
    }
    return enterMainScreen()
}

suspend fun buildAllNewBuildings(currentBase: BaseType): Boolean {
    val startTime = System.currentTimeMillis()
    // 15分钟对应的毫秒数是 900,000
    val timeoutMillis = 900_000L
    while (true) {
        val elapsedTime = System.currentTimeMillis() - startTime
        val remainingMinutes = (timeoutMillis - elapsedTime) / 60_000.0
        if (elapsedTime > timeoutMillis) {
            break
        }
        ShowMessage("建造中，剩余${"%.2f".format(remainingMinutes)}分钟后强制退出")
        if (!checkContinueBuild(currentBase)) {
            break
        }
        if (!buildOneNewBuildings(currentBase)) break
        if (!enterMainScreen()) return false
    }
    return enterMainScreen()
}

suspend fun checkContinueBuild(currentBase: BaseType): Boolean {
    // 1. Determine base-specific data sources and config keys
    val isBuilder = currentBase == BaseType.Builder

    val workerNumber = if (isBuilder) {
        BuilderBaseWorkerAndResearch.detectWorkerNumber()
    } else {
        MainBaseWorkerAndResearch.detectWorkerNumber()
    }
    val configKey = if (isBuilder) {
        Schema.BUILDER_BASE_SETTINGS.NIGHT_SAVE_WORKER.key
    } else {
        Schema.MAIN_BASE_SETTINGS.SAVE_WORKER.key
    }
    val baseName = if (isBuilder) "夜世界" else "主世界"
    // 2. Log the worker status (Keep original Chinese strings)
    ShowMessage("${baseName}工人数量：${workerNumber.available}/${workerNumber.total}")
    // 3. Evaluate the exit condition:
    // No workers available OR exactly one worker available while "Save Worker" config is enabled.
    val isNoWorkerAvailable = workerNumber.available == 0
    val isSavingLastWorker = workerNumber.available == 1 && getBooleanConfigRuntime(configKey)
    return !(isNoWorkerAvailable || isSavingLastWorker)
}

//For other functions, return false usually means fails to go back to main screen.
//But for this function, false means no new buildings.
private suspend fun buildOneNewBuildings(currentBase: BaseType): Boolean {
    ShowMessage("准备建造新建筑")
    if (currentBase == BaseType.Builder) zoomSmallBuilderBase(isForBuild = true)
    else if (currentBase == BaseType.Main) zoomSmallMainBase(isForBuild = true)
    // 1. Identify the position of new buildings; return early if not found
    if (!builderBaseFindNewBuildings(currentBase)) return false

    // 2. Locate the shop arrow indicator
    val shopArrow = findMultiColorsUntil(schemas = listOf(MyColors.InnerShopArrow), duration = 5000) ?: return false

    // 3. Determine building type (Wall vs. Others) before UI state changes
    val isWall = findMultiColors(schema = MyColors.WallInShop) != null
    TouchActions.tap(shopArrow.x - 50, shopArrow.y + 50, delayTime = 1500)

    // 4. Locate the confirmation button (Green Tick)
    var targetTick = if (currentBase == BaseType.Main) mainBaseFindBuildButton(type = "Tick") else builderBaseFindBuildButton(type = "Tick")

    // 5. If initial tick is missing, attempt to find a new position via the Red Cross
    if (targetTick == null) {
        ShowMessage("建造失败，尝试寻找空位")
        targetTick = FindBuildPosition.tryToFindBuildPosition()
    }

    // 6. Execute the building logic if a valid tick position is identified
    if (targetTick != null) {
        if (isWall) {
            // Handle wall batch building
            TouchActions.tap(targetTick.x, targetTick.y, delayTime = 100)
            tryToBatchBuildWalls(targetTick.x, targetTick.y)
        } else {
            // Handle standard building with retry logic
            for (i in 1..5) {
                val currentTick = if (currentBase == BaseType.Main) mainBaseFindBuildButton(type = "Tick", duration = 500) else builderBaseFindBuildButton(type = "Tick", duration = 500)
                if (currentTick != null) {
                    ShowMessage("点击第 $i 次绿色按钮：${currentTick.x}, ${currentTick.y}")
                    TouchActions.tap(currentTick.x, currentTick.y)
                } else {
                    // Cleanup if tick disappears
                    builderBaseFindBuildButton(type = "Cross")?.let { cross ->
                        TouchActions.tap(cross.x, cross.y)
                    }
                    break // Exit loop if button is no longer found
                }
            }
        }
        clickRightBottom(1)
        return true
    }

    return false
}

private suspend fun tryToBatchBuildWalls(x: Int, y: Int) {
    val centerX = x - 20
    val centerY = y + 45
    // Initial interaction to trigger wall building UI
    TouchActions.tap(centerX, centerY)
    delayWithMultiplier(500)

    // Zoom out to reveal more of the map/UI
    TouchActions.pinchOut(centerX - 90, centerY, centerX + 90, centerY, centerX, centerY)
    delayWithMultiplier(800)

    // Tap again to focus or confirm
    TouchActions.tap(centerX, centerY)
    TouchActions.swipe(280, 480, 280, 320, delayTime = 600)
    // Locate the arrow element using YOLO detector
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap ?: logAndStop("in BuilderBaseUpgradeBuildings, screen capture failed.")
    val detections = YoloDetector.detect(screenBuffer, modelType = "walls-detect")
    val batchBuildWallsArrow = detections.maxByOrNull { it.score }

    if (batchBuildWallsArrow != null) {
        val arrowX = batchBuildWallsArrow.boundingBox.centerX().toInt()
        val arrowY = batchBuildWallsArrow.boundingBox.centerY().toInt()
        ShowMessage("批量建造箭头：$arrowX, $arrowY")
        val targetCenterY = centerY - 150
        val dx = arrowX - centerX
        val dy = arrowY - targetCenterY
        val distance = sqrt((dx * dx + dy * dy).toDouble())

        if (distance > 0) {
            // Calculate trajectory based on the vector from center to the detected arrow
            val targetOffset = 5000
            val endX = (arrowX + (dx / distance) * targetOffset).toInt()
            val endY = (arrowY + (dy / distance) * targetOffset).toInt()

            TouchActions.swipe(arrowX, arrowY, endX, endY)
        }
    }
}
