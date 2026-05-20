@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IGameUpdateColors {
    val CNGameUpdate: ColorSchema
    val CNGameUpdating: ColorSchema
}

object GameUpdateColors : IGameUpdateColors {
    // CN game update available indicator
    override val CNGameUpdate = ColorSchema.parse(
        150, 436, 1113, 674, "60DFB8", "42|0|60DFB8,215|-1|62DFBA,269|1|5EDDB6,115|-146|E5A4DE,239|-138|E39EDC,398|-114|BC28AC,457|-4|68E2C0,667|1|5EDDB6,685|30|23AA61", 0, 0.97, "国服更新"
    )

    // CN game updating in progress indicator
    override val CNGameUpdating = ColorSchema.parse(
        150, 436, 1113, 674, "E4A2DE", "5|3|E39FDC,0|24|BC2AA9,2|30|BB2AA9,188|166|929292,247|168|8F8F8F,464|139|CACACA,535|133|D2D2D2,720|131|D3D3D3,703|171|898989", 0, 0.97, "国服更新中"
    )
}
