@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.clancapital

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IClanCapitalTutorialColors {
    val CapitalOldMan: ColorSchema
}

object ClanCapitalTutorialColors : IClanCapitalTutorialColors {
    override val CapitalOldMan = ColorSchema.parse(
        110, 397, 256, 692, "17406F", "29|0|887D7D,58|0|858289,87|0|8F8E95,116|0|323D61,0|147|7C7F83,29|147|9DA3AE,58|147|9FA6B1,87|147|A7B0BE,116|147|0F2749", 0, 0.9, "都城老头"
    )
}
