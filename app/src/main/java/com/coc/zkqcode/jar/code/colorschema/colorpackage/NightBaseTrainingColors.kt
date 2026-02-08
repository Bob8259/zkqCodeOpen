package com.coc.zkqcode.jar.code.colorschema.colorpackage

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface INightBaseTrainingColors {
    val RedCleanButton: ColorSchema
    val TrainNightWitch: ColorSchema
}

object NightBaseTrainingColors : INightBaseTrainingColors {
    override val RedCleanButton: ColorSchema = ColorSchema.parse(
        973,
        348,
        1156,
        425,
        "8684FF-101010",
        "33|-5|8785FF-101010,75|-5|8785FF-101010,117|-1|8785FF-101010,54|7|FFFFFF-101010,50|16|FFFFFF-101010,57|16|FFFFFF-101010,107|34|221EF7-101010,53|46|221EF6-101010,3|27|221EF7-101010",
        0,
        0.9,
        "清除夜世界部队"
    )
    override val TrainNightWitch: ColorSchema = ColorSchema.parse(
        540,
        547,
        635,
        644,
        "605667-101010",
        "10|3|0D0D0D-101010,18|15|34303E-101010,1|20|252430-101010,-9|2|5A515F-101010,-23|-10|121212-101010,-24|-19|2A2628-101010,-8|-25|222120-101010,12|-17|FAE238-101010,-15|-13|F9DF35-101010",
        0,
        0.9,
        "训练暗夜女巫"
    )
}
