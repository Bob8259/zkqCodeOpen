package com.coc.zkqcode.jar.code.builderbase.others

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.builderbase.research.builderBaseResearch
import com.coc.zkqcode.jar.code.universal.precheck.PreCheck
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.builderbase.resources.collectBuilderBaseResources
import com.coc.zkqcode.jar.code.builderbase.resources.builderBaseRemoveObstacles
import com.coc.zkqcode.jar.code.builderbase.upgrade.builderBaseUpgradeBuildings
import com.coc.zkqcode.jar.code.universal.enterBuilderBase
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun playBuilderBase(): Boolean {
    if (!PreCheck.claimAchievement()) return false
    val noBuilderBase = getBooleanConfigRuntime(
        Schema.BUILDER_BASE_SETTINGS.NO_BUILDER_BASE.key
    )
    if (noBuilderBase) return true//if no builder base, then directly return.

    if (!enterBuilderBase(true)) {
        ShowMessage("未解锁夜世界")
        return true
    } //check if builder base is unlocked or not.
    else {
        ShowMessage("进入夜世界成功")
    }
    if (!collectBuilderBaseResources()) return false
    if (!clickOttosOutPost()) return false
    if (!builderBaseRemoveObstacles()) return false
    if (!builderBaseUpgradeBuildings()) return false
    if (!builderBaseResearch()) return false
    return true
}