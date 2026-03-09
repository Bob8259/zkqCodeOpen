@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseResearchLevelColors {
    // Level number color schemas used to detect which upgrade level a research item is at.
    // Add a new property for each level that needs to be detected.
    val RESEARCH_LEVEL_1: ColorSchema
}

object MainBaseResearchLevelColors : IMainBaseResearchLevelColors {
    // Detects a level-1 badge: white text on a light background
    override val RESEARCH_LEVEL_1: ColorSchema =
        ColorSchema.parse(
            195, 350, 1090, 650,
            "FFFFFF",
            "0|1|FFFFFF,0|2|FFFFFF,0|3|FFFFFF,0|4|FFFFFF,0|5|FFFFFF,0|6|FFFFFF,0|7|FFFFFF,0|8|FFFFFF,0|9|F4F4F4",
            0, 0.92
        )
}
