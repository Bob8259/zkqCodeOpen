package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseResearchColors {
    val TestLevel: ColorSchema
}

object MainBaseResearchColors : IMainBaseResearchColors {
    override val TestLevel: ColorSchema = ColorSchema.parse(
        196, 578, 250, 624, "3F3F3F", "3|0|3F3F3F,7|0|FFFFFF,11|0|3F3F3F,14|0|3F3F3F,0|8|333233,3|8|323232,7|8|FFFFFF,11|8|303232,14|8|323232", 0, 0.9, "1级"
    )
}
