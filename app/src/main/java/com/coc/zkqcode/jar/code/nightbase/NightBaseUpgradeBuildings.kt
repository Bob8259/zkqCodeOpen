package com.coc.zkqcode.jar.code.nightbase

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.nightbase.upgradehelper.iterateNightBaseBuildingUpgradeList
import com.coc.zkqcode.jar.code.nightbase.upgradehelper.nightBaseFindBuildButton
import com.coc.zkqcode.jar.code.nightbase.upgradehelper.nightBaseFindNewBuildings
import com.coc.zkqcode.jar.code.universal.buildings.ALL_BUILDINGS
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
        zoomSmallNightBase()
        val worker = findMultiColorsUntil(schema = MyColors.NightBaseWorker, duration = 1000)
        if (worker != null) {
            TouchActions.tap(worker.x, worker.y)
            delayWithMultiplier(500)
            TouchActions.swipe(666, 200, 666, -1000)
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
            if (isNewBuildingDetected) {
                if (!buildAllNewBuildings()) return false
            }
            val summary = upgradableBuildingsMap.filter { it.value }.keys.joinToString("\n")
            ShowMessage("所有可升级建筑: $summary")

        } else {
            ShowMessage("未检测到夜世界工人")
        }
        return enterMainScreen()
    }

    suspend fun buildAllNewBuildings(): Boolean {
        val workerNumber = NightBaseWorkerAndResearch.detectWorkerNumber()
        while (true) {
            if (workerNumber.available == 0 || (workerNumber.available == 1 && getConfigRuntime(Schema.NIGHT_BASE_SETTINGS.NIGHT_SAVE_WORKER.key) == "1")) {
                break
            }
            if (!buildOneNewBuildings()) break
            if (!enterMainScreen()) return false
        }
        return enterMainScreen()
    }

    //For other functions, return false usually means fails to go back to main screen.
    //But for this function, false means no new buildings.
    private suspend fun buildOneNewBuildings(): Boolean {
        ShowMessage("准备建造新建筑")
        zoomSmallNightBase()
        // Swipe to adjust view for potential building locations
        TouchActions.swipe(620, 443, 620, 720, delayTime = 700)
        // Find out the position of new buildings
        if (nightBaseFindNewBuildings()) {
            // Search for the shop arrow indicator
            val shopArrow = findMultiColorsUntil(schema = MyColors.InnerShopArrow, duration = 5000)

            if (shopArrow != null) {
                // Determine building type before interacting with the UI to ensure state accuracy
                val isWall = findMultiColors(schema = MyColors.WallInShop) != null

                // Offset tap from the arrow to select the actual building icon
                TouchActions.tap(shopArrow.x - 100, shopArrow.y + 50)
                delayWithMultiplier(500)

                // Locate the confirmation button (Green Tick)
                val greenTick = nightBaseFindBuildButton(type = "Tick")
                if (greenTick != null) {
                    TouchActions.tap(greenTick.x, greenTick.y)

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
        TouchActions.pinchOut(centerX - 300, centerY, centerX + 300, centerY, centerX, centerY)
        delayWithMultiplier(800)

        // Tap again to focus or confirm
        TouchActions.tap(centerX, centerY)

        // Define the color schema for the batch build arrow
        val batchBuildWallsArrowColor: ColorSchema = ColorSchema.parse(
            centerX - 400, centerY - 300, centerX + 400, centerY + 300, "2DEC98", "4|0|2CEB97,8|0|2CEC98,11|0|2CEB98,15|0|2DEC98,0|10|2DEB96,4|10|2CEC97,8|10|2DEC97,11|10|2CEB98,15|10|2CEA97", 0, 0.9
        )

        // Locate the arrow element within the specified duration
        val batchBuildWallsArrow = findMultiColorsUntil(schema = batchBuildWallsArrowColor, duration = 2000)

        if (batchBuildWallsArrow != null) {
            val arrowX = batchBuildWallsArrow.x
            val arrowY = batchBuildWallsArrow.y
            val dx = arrowX - centerX
            val dy = arrowY - centerY

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