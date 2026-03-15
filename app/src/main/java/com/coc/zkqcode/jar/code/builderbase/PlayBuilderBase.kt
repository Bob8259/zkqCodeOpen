package com.coc.zkqcode.jar.code.builderbase

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.builderbase.attack.builderBaseAttack
import com.coc.zkqcode.jar.code.builderbase.attack.builderBaseTrainWithConditions
import com.coc.zkqcode.jar.code.builderbase.others.clickOttosOutPost
import com.coc.zkqcode.jar.code.builderbase.precheck.claimAchievement
import com.coc.zkqcode.jar.code.builderbase.research.builderBaseResearch
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.builderbase.resources.collectBuilderBaseResources
import com.coc.zkqcode.jar.code.builderbase.others.builderBaseRemoveObstacles
import com.coc.zkqcode.jar.code.builderbase.others.zoomSmallBuilderBase
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.upgradeBuildings
import com.coc.zkqcode.jar.code.universal.smalltools.enterBuilderBase
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun playBuilderBase(): Boolean {
    if (!claimAchievement()) return false
    val noBuilderBase = getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.NO_BUILDER_BASE.key)
    if (noBuilderBase) return true//if no builder base, then directly return.
    if (!enterBuilderBase(true)) return true
    zoomSmallBuilderBase()
    if (!collectBuilderBaseResources()) return false
    if (!enterBuilderBase(false)) return true
    if (!clickOttosOutPost()) return false
    if (!builderBaseRemoveObstacles()) return false
    if (!upgradeBuildings(BaseType.Builder)) return false
    if (!builderBaseResearch()) return false
    if (!builderBaseTrainWithConditions()) return false
    if (!builderBaseAttack()) return false
    return true
}