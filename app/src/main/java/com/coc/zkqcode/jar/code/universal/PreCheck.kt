package com.coc.zkqcode.jar.code.universal

import com.coc.zkqcode.jar.code.builderbase.precheck.claimAchievement
import com.coc.zkqcode.jar.code.universal.buildings.gearup.gearUpBuildings

suspend fun preCheck(): Boolean {
    if (!claimAchievement()) return false
    if (!gearUpBuildings()) return false

    return true
}