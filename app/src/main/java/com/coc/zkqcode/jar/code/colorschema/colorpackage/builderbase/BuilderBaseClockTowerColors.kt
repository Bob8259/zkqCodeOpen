@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IBuilderBaseClockTowerColors {
    val ClockTower: ColorSchema
}

object BuilderBaseClockTowerColors : IBuilderBaseClockTowerColors {
    // Clock tower button used to trigger the free boost in builder base
    override val ClockTower = ColorSchema.parse(
        221, 491, 1078, 633, "FFFBFF", "5|0|FFF1FF,10|0|0EB0D7,15|0|FFF0FF,20|0|FFFFFF,0|13|FFF4FF,5|13|FFFCFF,10|13|131314,15|13|FEEFFE,20|13|FFF8FF", 0, 0.9, "免费提速"
    )
}
