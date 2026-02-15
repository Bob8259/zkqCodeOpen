package com.coc.zkqcode.jar.code.builderbase

import android.graphics.Bitmap
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.yolo.YoloDetector
import com.coc.zkqcode.jar.code.builderbase.upgradehelper.FindBuildPosition
import com.coc.zkqcode.jar.code.builderbase.upgradehelper.UpgradeExistingBuildings
import com.coc.zkqcode.jar.code.builderbase.upgradehelper.builderBaseFindBuildButton
import com.coc.zkqcode.jar.code.builderbase.upgradehelper.builderBaseFindNewBuildings
import com.coc.zkqcode.jar.code.builderbase.upgradehelper.iterateBuilderBaseBuildingUpgradeList
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.buildings.ALL_BUILDINGS
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlin.math.sqrt

object BuilderBaseUpgradeBuildings {
    private val upgradableBuildingsMap = ALL_BUILDINGS.associateWith { false }.toMutableMap()

    suspend fun upgradeBuildings(): Boolean {
        upgradableBuildingsMap.keys.forEach { upgradableBuildingsMap[it] = false }
        var isNewBuildingDetected = false
        clickRightBottom()
        if (checkContinueBuild()) {
            val worker =
                findMultiColorsUntil(schemas = listOf(MyColors.BuilderBaseWorker), duration = 1000)
            if (worker != null) {
                TouchActions.tap(worker.x, worker.y)
                delayWithMultiplier(500)
                iterateBuilderBaseBuildingUpgradeList { result ->
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
                        val info = buildings.chunked(4)
                            .joinToString("\n") { chunk ->
                                chunk.joinToString(" ") { it.name }
                            }
                        ShowMessage("检测到 ${buildings.size} 个建筑:\n$info")
                    }
                    isNewBuildingDetected
                }
                val upgradableList = upgradableBuildingsMap.filter { it.value }.keys.toList()
                val summary = upgradableList.joinToString("\n")
                ShowMessage("所有可升级建筑: $summary")
                if (isNewBuildingDetected) {
                    if (!buildAllNewBuildings()) return false
                    upgradeBuildings()
                } else {
                    if (!UpgradeExistingBuildings.upgradeAllExistingBuildings(upgradableList)) return false
                }

            }
        }
        return enterMainScreen()
    }

    suspend fun buildAllNewBuildings(): Boolean {
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
            if (!checkContinueBuild()) {
                break
            }
            if (!buildOneNewBuildings()) break
            if (!enterMainScreen()) return false
        }
        return enterMainScreen()
    }

    suspend fun checkContinueBuild(): Boolean {
        val workerNumber = BuilderBaseWorkerAndResearch.detectWorkerNumber()
        ShowMessage("夜世界工人数量：${workerNumber.available}/${workerNumber.total}")
        return !(workerNumber.available == 0 || (workerNumber.available == 1 && getConfigRuntime(
            Schema.BUILDER_BASE_SETTINGS.NIGHT_SAVE_WORKER.key
        ) == "1"))
    }

    //For other functions, return false usually means fails to go back to main screen.
    //But for this function, false means no new buildings.
    private suspend fun buildOneNewBuildings(): Boolean {
        ShowMessage("准备建造新建筑")
        zoomSmallBuilderBase(isForBuild = true)
        // 1. Identify the position of new buildings; return early if not found
        if (!builderBaseFindNewBuildings()) return false

        // 2. Locate the shop arrow indicator
        val shopArrow =
            findMultiColorsUntil(schemas = listOf(MyColors.InnerShopArrow), duration = 5000)
                ?: return false

        // 3. Determine building type (Wall vs. Others) before UI state changes
        val isWall = findMultiColors(schema = MyColors.WallInShop) != null
        TouchActions.tap(shopArrow.x - 50, shopArrow.y + 50)
        delayWithMultiplier(1500)

        // 4. Locate the confirmation button (Green Tick)
        var targetTick = builderBaseFindBuildButton(type = "Tick")

        // 5. If initial tick is missing, attempt to find a new position via the Red Cross
        if (targetTick == null) {
            ShowMessage("建造失败，尝试寻找空位")
            targetTick = FindBuildPosition.tryToFindBuildPosition()
        }

        // 6. Execute the building logic if a valid tick position is identified
        if (targetTick != null) {
            if (isWall) {
                // Handle wall batch building
                delayWithMultiplier(100)
                ShowMessage("点击绿色按钮：${targetTick.x}, ${targetTick.y}")
                TouchActions.tap(targetTick.x, targetTick.y)
                tryToBatchBuildWalls(targetTick.x, targetTick.y)
            } else {
                // Handle standard building with retry logic
                for (i in 1..5) {
                    val currentTick = builderBaseFindBuildButton(duration = 500, type = "Tick")
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
            clickRightBottom()
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
        val screenBuffer = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
            ?: logAndStop("in BuilderBaseUpgradeBuildings, screen capture failed.")
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
}