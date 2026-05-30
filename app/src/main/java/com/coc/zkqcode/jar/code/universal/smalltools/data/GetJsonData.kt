package com.coc.zkqcode.jar.code.universal.smalltools.data

import com.coc.zkqcode.core.system.inputmethod.ZKQInputMethodService
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.input.setZKQInputMethod
import com.coc.zkqcode.jar.ui.schema.Schema
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException

suspend fun getJsonData(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.SMART_DECISION.key)) return true
    accountMessage("准备读取账号数据，请稍后")
    if (InGameVars.currentGameVersion != GameVersion.CN) {
        TouchActions.tap(1230, 520)//settings
        TouchActions.tap(630, 610)//more settings
        TouchActions.swipe(630, 500, 630, -1500, delayAfter = 400)
        TouchActions.tap(910, 450)//copy
    } else {
        TouchActions.tap(1230, 450)//arrow
        val settings = findMultiColorsUntil(listOf(MyColors.GameSettings), 1000)
        if (settings != null) {
            TouchActions.tap(settings.x, settings.y)
            TouchActions.tap(630, 610)//more settings
            TouchActions.swipe(630, 500, 630, -1500, delayAfter = 400)
            TouchActions.tap(900, 300)//copy
        }
    }

    // Wait for clipboard to be populated after copy action
    delayWithMultiplier(500)

    // Read clipboard content via IME service (only IME has clipboard access from background)
    val imeService = ZKQInputMethodService.instance
    if (imeService == null) {
        // IME not bound — try to rebind and retry
        setZKQInputMethod()
    }
    val clipboardText = (ZKQInputMethodService.instance ?: logAndRestart("ZKQInputMethodService instance is null after rebind attempt")).readClipboard()
    if (clipboardText.isNullOrBlank()) {
        // Clipboard is empty — skip updating townHallLevel
        accountMessage("剪贴板为空，跳过大本营等级检测")
        clickRightBottom(1)
        return enterMainScreen()
    }

    // Parse JSON and extract first building's lvl as townHallLevel
    val clipTrimmed = clipboardText.trim()
    if (!clipTrimmed.startsWith("{")) {
        accountMessage("剪贴板内容不是JSON对象，跳过大本营等级检测")
        clickRightBottom(1)
        return enterMainScreen()
    }
    val jsonObject = try {
        Gson().fromJson(clipTrimmed, JsonObject::class.java)
    } catch (e: JsonSyntaxException) {
        accountMessage("JSON解析失败: ${e.message}，跳过大本营等级检测")
        clickRightBottom(1)
        return enterMainScreen()
    }
    if (jsonObject == null) {
        accountMessage("剪贴板内容解析为null，跳过大本营等级检测")
        clickRightBottom(1)
        return enterMainScreen()
    }
    val buildings = jsonObject.getAsJsonArray("buildings")
    if (buildings == null || buildings.size() == 0) {
        accountMessage("JSON中未找到buildings数据，跳过大本营等级检测")
        clickRightBottom(1)
        return enterMainScreen()
    }
    // Townhall is identified by data == 1000001
    val townHallBuilding = buildings.firstOrNull {
        it.asJsonObject.get("data").asInt == 1000001
    }
    if (townHallBuilding == null) {
        accountMessage("JSON中未找到大本营(1000001)数据，跳过大本营等级检测")
        clickRightBottom(1)
        return enterMainScreen()
    }
    val firstBuildingLvl = try {
        townHallBuilding.asJsonObject.get("lvl").asInt
    } catch (_: Exception) {
        accountMessage("无法从大本营数据中读取lvl字段，跳过大本营等级检测")
        clickRightBottom(1)
        return enterMainScreen()
    }
    val targetLevel = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.SKIP_TH_LEVEL.key).toIntOrNull() ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.SKIP_TH_LEVEL.displayName} 必须是数字，请检查配置")
    accountMessage("大本营等级: $firstBuildingLvl\n设定目标等级：$targetLevel")
    if (firstBuildingLvl >= targetLevel) {
        return false//force switch account
    }
    InGameVars.townHallLevel = firstBuildingLvl

    // Parse clock tower cooldown from boosts (may not exist)
    InGameVars.clockTowerCooldown = try {
        val boosts = jsonObject.getAsJsonObject("boosts")
        boosts?.get("clocktower_cooldown")?.asInt ?: 0
    } catch (_: Exception) {
        0
    }

    // Determine whether the main base still has upgradable or unbuilt walls (data == 1000010 in "buildings").
    // Walls are considered complete (false) only when the built wall count equals the town hall's wall quantity
    // and every wall is already at the town hall's max wall level.
    InGameVars.mainBaseWallsNeedUpgrade = try {
        val (wallQuantity, maxWallLevel) = wallRequirementForTownHall(firstBuildingLvl)
        val wallEntries = buildings.filter { it.asJsonObject.get("data").asInt == 1000010 }
        val builtWallCount = wallEntries.sumOf { it.asJsonObject.get("cnt")?.asInt ?: 1 }
        val allAtMaxLevel = wallEntries.all { it.asJsonObject.get("lvl").asInt >= maxWallLevel }
        val isComplete = builtWallCount >= wallQuantity && allAtMaxLevel
        // Town halls with no walls (quantity 0) are always complete.
        wallQuantity > 0 && !isComplete
    } catch (_: Exception) {
        false
    }

    // Determine whether the builder base still has upgradable or unbuilt walls (data == 1000033 in "buildings2").
    InGameVars.builderBaseWallsNeedUpgrade = try {
        val buildings2 = jsonObject.getAsJsonArray("buildings2")
        if (buildings2 != null && buildings2.size() > 0) {
            // Builder hall is identified by data == 1000046 in "buildings2"
            val builderHallLevel = buildings2.firstOrNull {
                it.asJsonObject.get("data").asInt == 1000046
            }?.asJsonObject?.get("lvl")?.asInt ?: 0
            val (wallQuantity, maxWallLevel) = wallRequirementForBuilderHall(builderHallLevel)
            val wallEntries = buildings2.filter { it.asJsonObject.get("data").asInt == 1000033 }
            val builtWallCount = wallEntries.sumOf { it.asJsonObject.get("cnt")?.asInt ?: 1 }
            val allAtMaxLevel = wallEntries.all { it.asJsonObject.get("lvl").asInt >= maxWallLevel }
            val isComplete = builtWallCount >= wallQuantity && allAtMaxLevel
            wallQuantity > 0 && !isComplete
        } else {
            false
        }
    } catch (_: Exception) {
        false
    }

    clickRightBottom(1, delayTime = 200)
    return enterMainScreen()
}

/**
 * Wall quantity (number of walls) and max wall level for a given town hall level.
 * Returns Pair(wallQuantity, maxWallLevel). Unknown levels fall back to (0, 0).
 */
private fun wallRequirementForTownHall(townHallLevel: Int): Pair<Int, Int> = when (townHallLevel) {
    1 -> 0 to 0
    2 -> 25 to 2
    3 -> 50 to 3
    4 -> 75 to 4
    5 -> 100 to 5
    6 -> 125 to 6
    7 -> 175 to 7
    8 -> 225 to 8
    9 -> 250 to 10
    10 -> 275 to 11
    11 -> 300 to 12
    12 -> 300 to 13
    13 -> 300 to 14
    14 -> 325 to 15
    15 -> 325 to 16
    16 -> 325 to 17
    17 -> 325 to 18
    18 -> 325 to 19
    else -> 0 to 0
}

/**
 * Wall quantity (number of walls) and max wall level for a given builder hall level.
 * Returns Pair(wallQuantity, maxWallLevel). Unknown levels fall back to (0, 0).
 */
private fun wallRequirementForBuilderHall(builderHallLevel: Int): Pair<Int, Int> = when (builderHallLevel) {
    1 -> 10 to 1
    2 -> 20 to 1
    3 -> 50 to 3
    4 -> 75 to 4
    5 -> 100 to 5
    6 -> 120 to 6
    7 -> 140 to 7
    8 -> 160 to 8
    9 -> 180 to 9
    10 -> 180 to 10
    else -> 0 to 0
}
