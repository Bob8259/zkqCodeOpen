package com.coc.zkqcode.jar.code.nightbase

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.nightbase.upgradehelper.UpgradeExistingBuildings
import com.coc.zkqcode.jar.code.nightbase.upgradehelper.iterateNightBaseBuildingUpgradeList
import com.coc.zkqcode.jar.code.nightbase.upgradehelper.nightBaseFindBuildButton
import com.coc.zkqcode.jar.code.nightbase.upgradehelper.nightBaseFindNewBuildings
import com.coc.zkqcode.jar.code.universal.buildings.ALL_BUILDINGS
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.recognizer.RecognizeResources
import com.coc.zkqcode.jar.code.universal.recognizer.Resources
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.killGame
import com.coc.zkqcode.jar.ui.schema.Schema

object NightBaseUpgradeBuildings {
    private val upgradableBuildingsMap = ALL_BUILDINGS.associateWith { false }.toMutableMap()

    //    private var resources: Resources? = null
    suspend fun upgradeBuildings(): Boolean {
        upgradableBuildingsMap.keys.forEach { upgradableBuildingsMap[it] = false }
        var isNewBuildingDetected = false
        zoomSmallNightBase(true)
        clickRightBottom()
//        resources = RecognizeResources.recognizeMyResources()
        if (checkContinueBuild()) {
            val worker = findMultiColorsUntil(schemas = listOf(MyColors.NightBaseWorker), duration = 1000)
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
        return !(workerNumber.available == 0 || (workerNumber.available == 1 && getConfigRuntime(Schema.NIGHT_BASE_SETTINGS.NIGHT_SAVE_WORKER.key) == "1"))
    }

    //For other functions, return false usually means fails to go back to main screen.
    //But for this function, false means no new buildings.
    private suspend fun buildOneNewBuildings(): Boolean {
        ShowMessage("准备建造新建筑")

        // Find out the position of new buildings
        if (nightBaseFindNewBuildings()) {
            // Search for the shop arrow indicator
            val shopArrow = findMultiColorsUntil(schemas = listOf(MyColors.InnerShopArrow), duration = 5000)

            if (shopArrow != null) {
                // Determine building type before interacting with the UI to ensure state accuracy
                val isWall = findMultiColors(schema = MyColors.WallInShop) != null
                TouchActions.tap(shopArrow.x - 50, shopArrow.y + 50)
                delayWithMultiplier(500)
                // Locate the confirmation button (Green Tick)
                val greenTick = nightBaseFindBuildButton(type = "Tick")
                if (greenTick != null) {
                    delayWithMultiplier(100)
                    ShowMessage("点击绿色按钮：${greenTick.x}, ${greenTick.y}")
                    TouchActions.tap(greenTick.x, greenTick.y, isJitter = false)

                    // If the building was identified as a wall, trigger the batch building logic
                    if (isWall) {
                        tryToBatchBuildWalls(greenTick.x, greenTick.y)
                    }
                    return true
                } else {
                    val redCross = nightBaseFindBuildButton(type = "Cross")
                    if (redCross != null) {
                        ShowMessage("建造失败，尝试取消")
                        TouchActions.tap(redCross.x, redCross.y)
                    } else {
                        ShowMessage("取消失败，尝试重启游戏")
                        killGame()
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
        TouchActions.pinchOut(centerX - 100, centerY, centerX + 100, centerY, centerX, centerY)
        delayWithMultiplier(800)

        // Tap again to focus or confirm
        TouchActions.tap(centerX, centerY)
        TouchActions.swipe(280, 480, 280, 280, delayTime = 600)
        // Define color schemas for the batch build arrow
        val batchBuildWallsArrowColors: List<ColorSchema> = listOf(
            ColorSchema.parse(
                centerX - 400, centerY - 500, centerX + 400, centerY + 100, "7DFDC9", "3|0|7BFDC8,6|0|7CFDC7,9|0|7DFDC9,12|0|7DFDC8,0|8|7DFCC8,3|8|7CFCC8,6|8|7CFCC8,9|8|7CFCC8,12|8|7CFCC8", 0, 0.94
            ), ColorSchema.parse(
                centerX - 400, centerY - 500, centerX + 400, centerY + 100, "3CC180", "2|0|3EBF80,4|0|3FBF80,6|0|41BD7E,8|0|44B97C,0|6|26D68A,2|6|27D589,4|6|28D588,6|6|29D488,8|6|2BCF86", 0, 0.94
            ), ColorSchema.parse(
                centerX - 400, centerY - 500, centerX + 400, centerY + 100, "39996A", "2|0|39996A,4|0|39996A,5|0|39996A,7|0|39996B,0|5|3B9B6B,2|5|3B9B6A,4|5|3B9B6A,5|5|3B9B6A,7|5|3B9B6A", 0, 0.94
            ), ColorSchema.parse(
                centerX - 400, centerY - 500, centerX + 400, centerY + 100, "68B393", "3|0|68B293,5|0|67B192,7|0|66AF90,10|0|66AD8F,0|5|69B595,3|5|68B393,5|5|68B293,7|5|67B192,10|5|67AF90", 0, 0.94
            ), ColorSchema.parse(
                centerX - 400, centerY - 500, centerX + 400, centerY + 100, "629F83", "3|0|63A083,6|0|63A084,9|0|64A084,12|0|63A084,0|5|63A084,3|5|63A084,6|5|63A084,9|5|63A084,12|5|63A084", 0, 0.94
            )
        )

        // Locate the arrow element within the specified duration
        val batchBuildWallsArrow = findMultiColorsUntil(schemas = batchBuildWallsArrowColors, duration = 2000)

        if (batchBuildWallsArrow != null) {
            ShowMessage("批量建造箭头：${batchBuildWallsArrow.x}, ${batchBuildWallsArrow.y}")
            val arrowX = batchBuildWallsArrow.x
            val arrowY = batchBuildWallsArrow.y
            val dx = arrowX - centerX
            val dy = arrowY - centerY - 200

            // Ensure dx is not zero to prevent division by zero when calculating slope
            if (dx != 0) {
                // Calculate trajectory based on the vector from center to the detected arrow
                val targetOffset = 5000
                val endX = arrowX + targetOffset
                val endY = arrowY + (targetOffset * dy / dx)

                TouchActions.swipe(arrowX, arrowY, endX, endY)
            } else if (dy != 0) {
                // Vertical swipe fallback if dx is 0 but dy is not
                val endY = arrowY + (if (dy > 0) 5000 else -5000)
                TouchActions.swipe(arrowX, arrowY, arrowX, endY)
            }
        }
    }
}