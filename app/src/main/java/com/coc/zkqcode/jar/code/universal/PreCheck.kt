package com.coc.zkqcode.jar.code.universal

import com.coc.zkqcode.jar.code.builderbase.precheck.claimAchievement
import com.coc.zkqcode.jar.code.universal.buildings.gearup.gearUpBuildings
import com.coc.zkqcode.jar.code.universal.smalltools.getJsonData

suspend fun preCheck(): Boolean {
    if (!getJsonData()) return false
    if (!claimAchievement()) return false
    if (!gearUpBuildings()) return false
    return true
}