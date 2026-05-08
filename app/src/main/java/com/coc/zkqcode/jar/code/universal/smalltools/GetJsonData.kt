package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.system.inputmethod.ZKQInputMethodService
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.ui.schema.Schema
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException

suspend fun getJsonData(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.SMART_DECISION.key)) return true
    if (InGameVars.currentGameVersion != GameVersion.CN) {
        TouchActions.tap(1230, 520)//settings
        TouchActions.tap(630, 610)//more settings
        TouchActions.swipe(630, 500, 630, -1500, delayAfter = 400)
        TouchActions.tap(910, 450)//copy
    } else {
        TouchActions.tap(1230, 450)//arrow
        TouchActions.tap(1210, 250)//settings
        TouchActions.tap(630, 610)//more settings
        TouchActions.swipe(630, 500, 630, -1500, delayAfter = 400)
        TouchActions.tap(900, 300)//copy
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
    } catch (e: Exception) {
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
    clickRightBottom(1, delayTime = 200)
    return enterMainScreen()
}
