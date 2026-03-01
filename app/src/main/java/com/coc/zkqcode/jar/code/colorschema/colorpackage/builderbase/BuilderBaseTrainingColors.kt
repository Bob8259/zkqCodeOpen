package com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IBuilderBaseTrainingColors {
    val RedCleanButton: ColorSchema
    val TrainNightWitch: ColorSchema
}

object BuilderBaseTrainingColors : IBuilderBaseTrainingColors {
    override val RedCleanButton: ColorSchema = ColorSchema.parse(
        973, 348, 1156, 425, "8684FF", "33|-5|8785FF,75|-5|8785FF,117|-1|8785FF,54|7|FFFFFF,50|16|FFFFFF,57|16|FFFFFF,107|34|221EF7,53|46|221EF6,3|27|221EF7", 0, 0.9, "清除夜世界部队"
    )
    override val TrainNightWitch: ColorSchema = ColorSchema.parse(
        540, 547, 635, 644, "605667", "10|3|0D0D0D,18|15|34303E,1|20|252430,-9|2|5A515F,-23|-10|121212,-24|-19|2A2628,-8|-25|222120,12|-17|FAE238,-15|-13|F9DF35", 0, 0.9, "训练暗夜女巫"
    )
}
