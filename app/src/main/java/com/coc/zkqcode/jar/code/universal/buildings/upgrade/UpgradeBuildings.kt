package com.coc.zkqcode.jar.code.universal.buildings.upgrade

import android.graphics.Bitmap
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.universal.yolo.YoloDetector
import com.coc.zkqcode.jar.code.builderbase.others.zoomSmallBuilderBase
import com.coc.zkqcode.jar.code.builderbase.upgrade.builderBaseFindBuildButton
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.mainbase.upgrade.mainBaseFindBuildButton
import com.coc.zkqcode.jar.code.universal.buildings.ALL_BUILDINGS
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.WorkerAndResearch
import com.coc.zkqcode.jar.code.universal.buildings.iterateBuilderBaseBuildingUpgradeList
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlin.math.sqrt


private val upgradableBuildingsMap = ALL_BUILDINGS.associateWith { false }.toMutableMap()
private var zoomOrNot: Boolean = true

enum class BuildButtonType {
    Tick, Cross
}

suspend fun upgradeBuildings(currentBase: BaseType): Boolean {
    if (currentBase == BaseType.Builder) {
        if (!getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_BUILD_SETTING.key)) return true
    } else if (currentBase == BaseType.Main) {
        if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.BUILD_SETTING.key)) return true
    }
    zoomOrNot = true
    upgradableBuildingsMap.keys.forEach { upgradableBuildingsMap[it] = false }
    var isNewBuildingDetected = false
    clickRightBottom(1)
    if (checkContinueBuild(currentBase)) {
        val worker = when (currentBase) {
            BaseType.Builder -> findMultiColorsUntil(schemas = listOf(MyColors.BuilderBaseWorker, MyColors.BuilderBaseWorker2), duration = 1000)
            BaseType.Main -> findMultiColorsUntil(schemas = listOf(MyColors.MainBaseWorker, MyColors.MainBaseWorker2, MyColors.MainBaseWorker3), duration = 1000)
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
                upgradeBuildings(currentBase)
            } else {
                if (upgradeAllExistingBuildings(upgradableList, currentBase) == UpgradeResult.Failure) return false
            }
        }
    }
    return enterMainScreen()
}

suspend fun buildAllNewBuildings(currentBase: BaseType): Boolean {
    val startTime = System.currentTimeMillis()
    // 15 minutes in milliseconds is 900,000
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

suspend fun checkContinueBuild(currentBase: BaseType, isWallUpgrade: Boolean = false): Boolean {
    // 1. Determine base-specific data sources and config keys
    val isBuilder = currentBase == BaseType.Builder
    val workerNumber = WorkerAndResearch.detectWorkerNumber(currentBase)
    val configKey = if (isBuilder) {
        Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_SAVE_WORKER.key
    } else {
        Schema.MAIN_BASE_SETTINGS.SAVE_WORKER.key
    }
    val baseName = if (isBuilder) "夜世界" else "主世界"
    // 2. Log the worker status (Keep original Chinese strings for display)
    ShowMessage("${baseName}工人数量：${workerNumber.available}/${workerNumber.total}")
    // 3. Evaluate the exit condition:
    // No workers available OR exactly one worker available while "Save Worker" config is enabled.
    // When isWallUpgrade is true, skip the save-worker check since the saved worker is reserved for walls.
    val isNoWorkerAvailable = workerNumber.available == 0
    val isSavingLastWorker = !isWallUpgrade && workerNumber.available == 1 && getBooleanConfigRuntime(configKey)
    return !(isNoWorkerAvailable || isSavingLastWorker)
}

//For other functions, return false usually means fails to go back to main screen.
//But for this function, false means no new buildings.
private suspend fun buildOneNewBuildings(currentBase: BaseType): Boolean {
    ShowMessage("准备建造新建筑")
    clickRightBottom(1)
    if (zoomOrNot) {
        if (currentBase == BaseType.Builder) zoomSmallBuilderBase(isForBuild = true)
        else if (currentBase == BaseType.Main) zoomSmallMainBase(isForBuild = true)
        zoomOrNot = false
    }
    // 1. Identify the position of new buildings; return early if not found
    if (!builderBaseFindNewBuildings(currentBase)) return false

    // 2. Locate the shop arrow indicator
    val shopArrow = findMultiColorsUntil(schemas = listOf(MyColors.InnerShopArrow), duration = 5000) ?: return false

    // 3. Determine building type (Wall vs. Others) before UI state changes
    val isWall = findMultiColorsUntil(schemas = listOf(MyColors.BuilderBaseWallInShop, MyColors.MainBaseWallInShop), duration = 100) != null
    TouchActions.tap(shopArrow.x - 50, shopArrow.y + 50, delayTime = 1500)

    // 4. Locate the confirmation button (Green Tick)
    var targetTick = if (currentBase == BaseType.Main) mainBaseFindBuildButton(type = BuildButtonType.Tick) else builderBaseFindBuildButton(type = BuildButtonType.Tick)

    // 5. If initial tick is missing, attempt to find a new position via the Red Cross
    if (targetTick == null) {
        ShowMessage("建造失败，尝试寻找空位")
        targetTick = tryToFindBuildPosition(currentBase)
    }

    // 6. Execute the building logic if a valid tick position is identified
    if (targetTick != null) {
        if (isWall) {
            // Handle wall batch building
            TouchActions.tap(targetTick.x, targetTick.y, delayTime = 100)
            tryToBatchBuildWalls(targetTick.x, targetTick.y, currentBase)
            zoomOrNot = true// after building walls, zoom the map for the next build.
        } else {
            // Handle standard building with retry logic
            for (i in 1..5) {
                val currentTick = if (currentBase == BaseType.Main) mainBaseFindBuildButton(type = BuildButtonType.Tick, duration = 500) else builderBaseFindBuildButton(type = BuildButtonType.Tick, duration = 500)
                if (currentTick != null) {
                    ShowMessage("点击第 $i 次绿色按钮：${currentTick.x}, ${currentTick.y}")
                    if (i > 1) zoomOrNot = true
                    TouchActions.tap(currentTick.x, currentTick.y, delayTime = 500)
                    if (currentBase == BaseType.Main && getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.INSTANT_UPGRADE.key)) {
                        val gemCost = detectInstantBuildCost()
                        val costThreshold = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.INSTANT_UPGRADE_THRESHOLD.key).toIntOrNull()
                            ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.INSTANT_UPGRADE_THRESHOLD.displayName} 必须是数字，请检查配置")
                        ShowMessage("检测到的宝石消耗数量: $gemCost\n设置的宝石消耗限额: $costThreshold")
                        if (gemCost != null && gemCost <= costThreshold) {
                            val upgradeGemIcon = findMultiColorsUntil(
                                schemas = listOf(MyColors.UpgradeGemIcon, MyColors.UpgradeGemIcon2, MyColors.UpgradeGemIcon3), duration = 500
                            )
                            if (upgradeGemIcon != null) {
                                TouchActions.tap(upgradeGemIcon.x, upgradeGemIcon.y, delayTime = 500)
                                TouchActions.tap(638, 456, delayTime = 500)
                            }
                        }
                    }
                } else {
                    clickRightBottom(1)
                    delayWithMultiplier(500)
                    // Cleanup if tick disappears
                    builderBaseFindBuildButton(type = BuildButtonType.Cross)?.let { cross ->
                        TouchActions.tap(cross.x, cross.y)
                    }
                    mainBaseFindBuildButton(type = BuildButtonType.Cross)?.let { cross ->
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

private suspend fun tryToBatchBuildWalls(x: Int, y: Int, currentBase: BaseType) {

    val centerX = if (currentBase == BaseType.Builder) x - 20 else x - 15
    val centerY = if (currentBase == BaseType.Builder) y + 45 else y + 30

    // Initial interaction to trigger wall building UI
    clickRightBottom(1)
    delayWithMultiplier(500)

    // Zoom out to reveal more of the map/UI
    TouchActions.pinchOut(centerX - 90, centerY, centerX + 90, centerY, centerX, centerY)
    delayWithMultiplier(800)

    // Tap again to focus or confirm
    TouchActions.tap(centerX, centerY)
    val shouldSwipe = centerY >= 335
    if (shouldSwipe) {
        // Swipe up to avoid overlapping.
        TouchActions.swipe(280, 480, 280, 320, delayTime = 600)//Swiped for 160 pixels here, so in the trajectory calculate, the offset for y should be 150
    }
    // Locate the arrow element using YOLO detector
    delayWithMultiplier(200)
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap ?: logAndRestart("in BuilderBaseUpgradeBuildings, screen capture failed.")
    // Early return if model weights fail to load
    if (!YoloDetector.loadWeights("walls-detect")) return
    try {
        val detections = YoloDetector.detect(screenBuffer, clearWeightsAfter = false)
        val batchBuildWallsArrow = detections.maxByOrNull { it.score }
        if (batchBuildWallsArrow != null) {
            val arrowX = batchBuildWallsArrow.boundingBox.centerX().toInt()
            val arrowY = batchBuildWallsArrow.boundingBox.centerY().toInt()
            ShowMessage("批量建造箭头：$arrowX, $arrowY")
            val targetCenterY = if (shouldSwipe) centerY - 150 else centerY
            val dx = arrowX - centerX
            val dy = arrowY - targetCenterY
            val distance = sqrt((dx * dx + dy * dy).toDouble())

            if (distance > 0) {
                // Calculate trajectory based on the vector from center to the detected arrow
                val targetOffset = 3000
                val endX = (arrowX + (dx / distance) * targetOffset).toInt()
                val endY = (arrowY + (dy / distance) * targetOffset).toInt()

                TouchActions.swipe(arrowX, arrowY, endX, endY, delayTime = 300)
            }
        }
    } finally {
        // Always clear model weights after detection to free memory
        YoloDetector.clearWeights()
    }
}
