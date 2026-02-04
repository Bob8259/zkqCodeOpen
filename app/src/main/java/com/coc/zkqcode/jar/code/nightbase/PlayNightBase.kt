package com.coc.zkqcode.jar.code.nightbase

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.universal.EnterTargetBase
import com.coc.zkqcode.jar.code.universal.precheck.PreCheck
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun playNightBase(): Boolean {
    if (!PreCheck.claimAchievement()) return false
    val noNightBase = getConfigRuntime(
        Schema.NIGHT_BASE_SETTINGS.NO_BUILDER_BASE.key
    ) == "1"
    if (noNightBase) return true//if no night base, then directly return.

    if (!EnterTargetBase().enterNightBase(true)) {
        ShowMessage("未解锁夜世界")
        return true
    } //check if night base is unlocked or not.
    else {
        ShowMessage("进入夜世界成功")
    }
    collectNightBaseResources()
    nightBaseRemoveObstacles()
    return true
}