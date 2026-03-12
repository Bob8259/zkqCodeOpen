package com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IBuilderBaseResearchColors {
    val BuilderResearchElixir: ColorSchema
    val BuilderResearchInsufficientResources: ColorSchema
    val BuilderResearchBackArrow: ColorSchema
}

object BuilderBaseResearchColors : IBuilderBaseResearchColors {
    override val BuilderResearchElixir = ColorSchema.parse(
        0, 0, 1280, 720, "FE3093", "5|-5|FF1FAA,9|-5|FF4BC6,9|-2|FF6ADC,9|0|FF6FDB,5|2|FF1B8F,4|3|FF1E80,4|6|F62271,8|6|F52473,8|5|FE217B", 0, 0.9, "夜世界研究圣�?
    )
    override val BuilderResearchInsufficientResources = ColorSchema.parse(
        0, 0, 1280, 720, "727BFF", "0|1|727BFF,0|2|727BFF,0|3|727BFF", 0, 0.97, "资源不足"
    )
    override val BuilderResearchBackArrow = ColorSchema.parse(
        213, 35, 295, 69, "73EECB", "17|0|E9E9E9,33|0|FFFFFF,49|0|FFFFFF,66|0|73EECB,0|17|2DC980,17|17|2DC980,40|11|FFFEF9,68|15|2CCD84,66|17|2DC980", 0, 0.9, "夜世界研究返回箭�?
    )
}
