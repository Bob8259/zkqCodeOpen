@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseResourcesColors {
    val MainBaseCollectElixir1: ColorSchema
    val MainBaseCollectElixir2: ColorSchema
    val MainBaseCollectGold1: ColorSchema
    val MainBaseCollectGold2: ColorSchema
}

object MainBaseResourcesColors : IMainBaseResourcesColors {
    // Main base elixir collection color variants
    override val MainBaseCollectElixir1 = ColorSchema.parse(
        115, 85, 1130, 610, "D55BE1", "-4|-3|CD40D8,0|-4|D750E7,3|-7|E138F0,5|-2|E061ED,0|1|D458DD,-3|1|C74BCC,-12|-5|AAD5CF,-10|-10|B8DBD7,11|4|89C3BB", 0, 0.92
    )
    override val MainBaseCollectElixir2 = ColorSchema.parse(
        115, 85, 1130, 610, "D254E1", "0|-2|D55BE4,-2|-8|DF3DEE,2|-5|FFFEFF,4|-3|D840ED,-2|-1|D458DE,-3|-5|D65FE9,-13|-13|B8DCD7,10|-11|B6DBD6,10|0|91C8C0", 0, 0.92
    )

    // Main base gold collection color variants
    override val MainBaseCollectGold1 = ColorSchema.parse(
        115, 85, 1130, 610, "49ECFF", "-3|0|4AE4FF,0|-2|4AF6FF,4|0|18E3FF,3|5|06C1FF,0|5|03BCF5,-4|4|48DAFF,12|12|7FBEB5,13|-7|B9DCD7,-9|-7|B9DCD7", 0, 0.92
    )
    override val MainBaseCollectGold2 = ColorSchema.parse(
        115, 85, 1130, 610, "0DD0FF", "1|2|09C8FF,-3|0|4AE3FF,-3|-3|49EFFF,2|-3|1AE9FF,3|0|16DCFF,-3|4|03BAF2,-13|-8|B9DCD7,10|-9|B9DCD7,10|9|83C0B7", 0, 0.92
    )
}
