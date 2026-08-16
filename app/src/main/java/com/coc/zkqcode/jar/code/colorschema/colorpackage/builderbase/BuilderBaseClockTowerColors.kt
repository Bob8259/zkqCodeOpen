@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IBuilderBaseClockTowerColors {
    val ClockTower: ColorSchema
    val ClockTower2: ColorSchema
    val ClockTowerPotionOutside: ColorSchema
    val ClockTowerPotionInner: ColorSchema
}

object BuilderBaseClockTowerColors : IBuilderBaseClockTowerColors {
    // Clock tower button used to trigger the free boost in builder base
    override val ClockTower = ColorSchema.parse(
        221, 491, 1078, 633, "FFFBFF", "5|0|FFF1FF,10|0|0EB0D7,15|0|FFF0FF,20|0|FFFFFF,0|13|FFF4FF,5|13|FFFCFF,10|13|131314,15|13|FEEFFE,20|13|FFF8FF", 0, 0.9, "免费提速"
    )

    // Alternative clock tower button appearance for the free boost in builder base
    override val ClockTower2 = ColorSchema.parse(
        147, 491, 1131, 632, "0DB1D6", "9|3|FFFFFF,12|9|FAE0F3,9|20|E5C2D6,4|22|FFF1FF,-3|21|FFF9FF,-8|13|FFF4FF,-1|11|97767E,-4|18|937095,13|22|FFF2FF", 0, 0.9, "免费提速2"
    )

    // Clock tower potion indicator outside the dialog
    override val ClockTowerPotionOutside = ColorSchema.parse(
        169, 494, 1146, 634, "F6BAD8", "-8|15|F9C4DE,-17|0|FFD5E5,-2|-7|FFE3F5,11|-4|82E0FC,18|0|416083,29|3|609DC9,-11|-12|FCD5EB,-22|-1|FFDDE8,-1|9|F4B5D6", 0, 0.9, "钟楼药水外部标志"
    )

    // Clock tower potion indicator inside the dialog
    override val ClockTowerPotionInner = ColorSchema.parse(
        514, 398, 759, 512, "E5C286", "28|7|E5C286,38|19|E5C286,83|40|FFD2E4,93|32|FFE5F7,103|33|82E0FD,111|41|7BD8F6,99|43|403533,79|49|FFDDE7,30|51|CF932F", 0, 0.9, "钟楼药水内部"
    )
}
