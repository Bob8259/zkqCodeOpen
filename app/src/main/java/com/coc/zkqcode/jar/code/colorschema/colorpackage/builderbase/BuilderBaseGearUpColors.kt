@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IBuilderBaseGearUpColors {
    val UpgradeListGearUpIcon: ColorSchema
    val BuildingGearUpButton: ColorSchema
}

object BuilderBaseGearUpColors : IBuilderBaseGearUpColors {
    override val UpgradeListGearUpIcon = ColorSchema.parse(
        0, 0, 0, 0, "E2E1E3", "-5|0|ACA09B,-5|-6|D5D4D8,1|-7|7E7D7E,3|-11|D4D0D3,8|-11|B9B2B4,7|-14|B8B5BA,11|-11|C1BEC2,11|-7|CCCACD,9|-2|988E87", 0, 0.9, "改装标志"
    )
    override val BuildingGearUpButton = ColorSchema.parse(
        180, 500, 1180, 635, "AB9C99", "1|-12|D7D4D8,23|-28|BCB9BE,33|-21|C4C2C6,31|-2|8B827D,21|4|7F7C81,5|4|81746D,1|-6|DAD8DB,13|-15|7E7D7E,22|-17|A3978E", 0, 0.9, "改装建筑按钮"
    )
}
