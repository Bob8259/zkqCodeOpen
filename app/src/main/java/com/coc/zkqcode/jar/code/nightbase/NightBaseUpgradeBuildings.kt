package com.coc.zkqcode.jar.code.nightbase

import android.graphics.Bitmap
import kotlin.math.sqrt
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.bugreporter.BugReporter
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.yolo.YoloDetector
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.nightbase.upgradehelper.FindBuildPosition
import com.coc.zkqcode.jar.code.nightbase.upgradehelper.UpgradeExistingBuildings
import com.coc.zkqcode.jar.code.nightbase.upgradehelper.iterateNightBaseBuildingUpgradeList
import com.coc.zkqcode.jar.code.nightbase.upgradehelper.nightBaseFindBuildButton
import com.coc.zkqcode.jar.code.nightbase.upgradehelper.nightBaseFindNewBuildings
import com.coc.zkqcode.jar.code.universal.buildings.ALL_BUILDINGS
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.killGame
import com.coc.zkqcode.jar.ui.schema.Schema

object NightBaseUpgradeBuildings {
    private val upgradableBuildingsMap = ALL_BUILDINGS.associateWith { false }.toMutableMap()

    suspend fun upgradeBuildings(): Boolean {
        upgradableBuildingsMap.keys.forEach { upgradableBuildingsMap[it] = false }
        var isNewBuildingDetected = false
        zoomSmallNightBase(true)
        clickRightBottom()
        if (checkContinueBuild()) {
            val worker =
                findMultiColorsUntil(schemas = listOf(MyColors.NightBaseWorker), duration = 1000)
            if (worker != null) {
                TouchActions.tap(worker.x, worker.y)
                delayWithMultiplier(500)
                iterateNightBaseBuildingUpgradeList { result ->
                    val buildings = result.buildings
                    if (buildings.isEmpty()) {
                        ShowMessage("未检测到建筑")
                    } else {
                        buildings.forEach { building ->
                            if (upgradableBuildingsMap.containsKey(building.name)) {
                                upgradableBuildingsMap[building.name] = true
                            }
                            if (building.name.startsWith("新")) {
                                isNewBuildingDetected = true
                            }
                        }
                        val info = buildings.joinToString("\n")
                        ShowMessage("检测到 ${buildings.size} 个建筑:\n$info")
                    }
                    isNewBuildingDetected
                }
                val upgradableList = upgradableBuildingsMap.filter { it.value }.keys.toList()
                val summary = upgradableList.joinToString("\n")
                ShowMessage("所有可升级建筑: $summary")
                if (isNewBuildingDetected) {
                    if (!buildAllNewBuildings()) return false
                }
                if (!UpgradeExistingBuildings.upgradeAllExistingBuildings(upgradableList)) return false
            }
        }
        return enterMainScreen()
    }

    suspend fun buildAllNewBuildings(): Boolean {
        val startTime = System.currentTimeMillis()
        while (true) {
            val elapsedTime = System.currentTimeMillis() - startTime
            val remainingMinutes = (600_000 - elapsedTime) / 60_000.0
            if (elapsedTime > 600_000) {
                break
            }
            ShowMessage("建造中，剩余${"%.2f".format(remainingMinutes)}分钟后强制退出")
            zoomSmallNightBase(isForBuild = true)
            if (!checkContinueBuild()) {
                break
            }
            if (!buildOneNewBuildings()) break
            if (!enterMainScreen()) return false
        }
        return enterMainScreen()
    }

    suspend fun checkContinueBuild(): Boolean {
        val workerNumber = NightBaseWorkerAndResearch.detectWorkerNumber()
        ShowMessage("夜世界工人数量：${workerNumber.available}/${workerNumber.total}")
        return !(workerNumber.available == 0 || (workerNumber.available == 1 && getConfigRuntime(
            Schema.NIGHT_BASE_SETTINGS.NIGHT_SAVE_WORKER.key
        ) == "1"))
    }

    //For other functions, return false usually means fails to go back to main screen.
    //But for this function, false means no new buildings.
    private suspend fun buildOneNewBuildings(): Boolean {
        ShowMessage("准备建造新建筑")

        // Find out the position of new buildings
        if (nightBaseFindNewBuildings()) {
            // Search for the shop arrow indicator
            val shopArrow =
                findMultiColorsUntil(schemas = listOf(MyColors.InnerShopArrow), duration = 5000)

            if (shopArrow != null) {
                // Determine building type before interacting with the UI to ensure state accuracy
                val isWall = findMultiColors(schema = MyColors.WallInShop) != null
                TouchActions.tap(shopArrow.x - 50, shopArrow.y + 50)
                delayWithMultiplier(500)
                // Locate the confirmation button (Green Tick)
                val greenTick = nightBaseFindBuildButton(type = "Tick")
                if (greenTick != null) {
                    if (isWall) {
                        delayWithMultiplier(100)
                        ShowMessage("点击绿色按钮：${greenTick.x}, ${greenTick.y}")
                        TouchActions.tap(greenTick.x, greenTick.y, isJitter = false)
                        // If the building was identified as a wall, trigger the batch building logic
                        tryToBatchBuildWalls(greenTick.x, greenTick.y)
                    } else {
                        // For non-wall buildings, try to click the green tick multiple times if it's still there
                        for (i in 1..5) {
                            val currentTick =
                                nightBaseFindBuildButton(duration = 1000, type = "Tick")
                            if (currentTick != null) {
                                delayWithMultiplier(100)
                                ShowMessage("点击第 $i 次绿色按钮：${currentTick.x}, ${currentTick.y}")
                                TouchActions.tap(currentTick.x, currentTick.y, isJitter = false)
                                delayWithMultiplier(200)
                            } else {
                                break
                            }
                        }
                    }
                    clickRightBottom()
                    return true
                } else {
                    ShowMessage("未找到绿色按钮，错误截图已保存到/sdcard/zkqFiles/bugReporter\n请将截图反馈给作者")
                    BugReporter.takeScreenshot("Green_Tick_Not_Found")
                    val redCross = nightBaseFindBuildButton(type = "Cross")
                    if (redCross != null) {
                        ShowMessage("建造失败，尝试寻找空位")
                        FindBuildPosition.tryToFindBuildPosition(redCross.x, redCross.y)
                    } else {
                        ShowMessage("未找到红色叉，错误截图已保存到/sdcard/zkqFiles/bugReporter\n请将截图反馈给作者")
                        BugReporter.takeScreenshot("Red_Cross_Not_Found")
                        killGame()
                        clickRightBottom()
                        return false
                    }
                }
            }
            return false
        } else {
            return false
        }
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
            ?: logAndStop("in NightBaseUpgradeBuildings, screen capture failed.")
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