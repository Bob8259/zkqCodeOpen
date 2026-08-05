@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IGameUpdateColors {
    val CNGameUpdate: ColorSchema
    val CNGameUpdate2: ColorSchema
    val CNGameUpdating: ColorSchema
}

object GameUpdateColors : IGameUpdateColors {
    // CN game update available indicator
    override val CNGameUpdate = ColorSchema.parse(
        150, 436, 1113, 674, "60DFB8", "42|0|60DFB8,215|-1|62DFBA,269|1|5EDDB6,115|-146|E5A4DE,239|-138|E39EDC,398|-114|BC28AC,457|-4|68E2C0,667|1|5EDDB6,685|30|23AA61", 0, 0.97, "国服更新"
    )

    // CN game update available indicator (alternate layout)
    override val CNGameUpdate2 = ColorSchema.parse(
        149, 438, 1120, 675, "78E9D0", "-70|-101|BC2AA9,-50|-123|989999,-50|-100|353738,-72|-124|E3A0DC,-50|41|21B76E,435|-1|78E9D0,435|47|23AA61,206|-119|989999,206|-99|353738", 0, 0.9, "国服更新2"
    )

    // CN game updating in progress indicator
    override val CNGameUpdating = ColorSchema.parse(
        150, 436, 1113, 674, "E4A2DE", "5|3|E39FDC,0|24|BC2AA9,2|30|BB2AA9,188|166|929292,247|168|8F8F8F,464|139|CACACA,535|133|D2D2D2,720|131|D3D3D3,703|171|898989", 0, 0.97, "国服更新中"
    )
}
