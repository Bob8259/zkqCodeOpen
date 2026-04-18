package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.smalltools.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.TouchActions.swipe
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.aidetection.tiledAiDetect
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.WorkerAndResearch
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun useHelpers(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.HELPER_SETTINGS.key)) return true
    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.USE_HELPERS, InGameVars.currentAccountNumber
    )

    // Guard the helper scan so each account only retries every 22 hours after a successful tap.
    if (!checkMemoryFile(storageKey, 1320)) {
        accountMessage("该账号今日已检测助手")
        return true
    }
    accountMessage("正在检测助手")
    clickRightBottom(1)
    zoomSmallMainBase()
    val workerNumber = WorkerAndResearch.detectWorkerNumber(BaseType.Main)
    val isUpgradeHelper = workerNumber.total >= 5

    // Scan the current view first, then retry once after swiping upward.
    suspend fun scanAndTapHelpers(): Boolean {
        val scan = tiledAiDetect(
            modelName = "building-detect", callerTag = "UseHelpers", classIndex = 2
        )
        for (detection in scan) {
            val box = detection.boundingBox
            TouchActions.tap(box.centerX().toInt(), box.centerY().toInt())
            val openHelperHut = findMultiColorsUntil(schemas = listOf(MyColors.BuilderHelperIcon, MyColors.ResearchHelperIcon, MyColors.ResearchHelperIcon2), duration = 200)
            if (openHelperHut != null) {
                TouchActions.tap(openHelperHut.x, openHelperHut.y)
                checkResearchHelper(isUpgradeHelper)
                writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
                clickRightBottom(1)
                return true
            }
        }
        return false
    }

    if (!scanAndTapHelpers()) {
        swipe(900, 130, 0, 720)
        scanAndTapHelpers()
    }
    return enterMainScreen()
}

private suspend fun clickLeisure() {
    TouchActions.tap(1070, 625, delayTime = 200)//空闲中
}

private suspend fun checkResearchHelper(isUpgradeHelper: Boolean) {
    repeat(15) {
        val middleGreen = findMultiColors(MyColors.MiddleGreenButton)
        if (middleGreen != null) {
            TouchActions.tap(middleGreen.x, middleGreen.y, delayTime = 200)
        }
        val innerResearchHelper = findMultiColors(MyColors.InnerResearchHelper)
        if (innerResearchHelper != null) {
            TouchActions.tap(innerResearchHelper.x, innerResearchHelper.y)
            val lockedResearchHelper = findMultiColors(MyColors.LockedResearchHelper)
            if (lockedResearchHelper != null) {
                TouchActions.tap(801, 623, delayTime = 200)
                clickLeisure()
            }
            if (isUpgradeHelper && getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_RESEARCH_HELPER.key)) {
                accountMessage("已勾选升级实验助手")
                TouchActions.tap(801, 623, delayTime = 200)
                TouchActions.tap(801, 623, delayTime = 200)
                clickLeisure()
            }
            assignHelper()
        }
        val lockedBuilderHelper = findMultiColors(MyColors.LockedBuilderHelper)
        if (lockedBuilderHelper != null && isUpgradeHelper && getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_BUILDER_APPRENTICE.key)) {
            accountMessage("已勾选升级工人助手")
            TouchActions.tap(lockedBuilderHelper.x, lockedBuilderHelper.y)
            TouchActions.tap(801, 623, delayTime = 200)
            TouchActions.tap(801, 623, delayTime = 200)
            clickLeisure()
        } else if (lockedBuilderHelper != null) {
            return
        }
        val innerBuilderHelper = findMultiColors(MyColors.InnerBuilderHelper)
        if (innerBuilderHelper != null) {
            TouchActions.tap(innerBuilderHelper.x, innerBuilderHelper.y)
            assignHelper()
            clickLeisure()
            return
        }
        delayWithMultiplier(200)
    }
}

private suspend fun assignHelper() {
    val assignHelper = findMultiColors(MyColors.AssignHelper)
    if (assignHelper != null) {
        TouchActions.tap(assignHelper.x, assignHelper.y)
        TouchActions.tap(408, 473, delayTime = 200)//Continuously assign
        TouchActions.tap(641, 536)
        clickLeisure()
    }
}
