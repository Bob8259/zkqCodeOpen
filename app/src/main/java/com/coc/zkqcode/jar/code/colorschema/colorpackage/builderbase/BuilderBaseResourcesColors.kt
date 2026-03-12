package com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IBuilderBaseResourcesColors {
    val CannotCollectExilerCart: ColorSchema
    val CollectExilerCart: ColorSchema
    val BuilderBaseCollectGold1: ColorSchema
    val BuilderBaseCollectExiler1: ColorSchema
    val BuilderBaseCollectGem1: ColorSchema
    val BuilderBaseCollectGem2: ColorSchema
}

object BuilderBaseResourcesColors : IBuilderBaseResourcesColors {
    override val CannotCollectExilerCart = ColorSchema.parse(
        859, 569, 1024, 645, "D3D3D3", "28|3|D2D2D2,66|0|D3D3D3,82|4|D2D2D2,86|11|CDCDCD,87|30|ADADAD,78|39|ACACAC,45|36|ADADAD,18|34|ADADAD,-8|27|ADADAD", 0, 0.98, "???????"
    )
    override val CollectExilerCart = ColorSchema.parse(
        859, 569, 1024, 645, "8DEABE", "25|-3|8EEABF,65|-1|8EEABF,101|0|8DEABE,107|5|87E9BB,106|35|3AD48B,95|40|39CE87,64|37|3AD38A,13|31|3AD48B,-2|29|3AD48B", 0, 0.98, "?????"
    )
    override val BuilderBaseCollectGold1 = ColorSchema.parse(
        115, 86, 1127, 609, "10CAEE", "-3|2|44D1EE,1|5|03B2E8,5|5|0DBEF1,4|0|1ED9EF,0|-2|46E9ED,-3|16|71B2A9,-13|4|90C0B9,-14|-3|A4C8C3,11|-2|A3CAC4", 0, 0.92, "????1"
    )
    override val BuilderBaseCollectExiler1 = ColorSchema.parse(
        115, 82, 1127, 609, "EA3494", "-3|0|DE217A,-3|-5|EB2C9D,-7|-11|B0D1CC,-11|-8|ABCCC9,10|-6|AFD5CF,3|-1|F294D2,1|2|E02280,-2|5|C41757,-5|5|9D114C", 0, 0.92, "????1"
    )
    override val BuilderBaseCollectGem1 = ColorSchema.parse(
        115, 86, 1127, 609, "85F1DA", "-13|-5|AED6D2,12|-5|ADD5D1,12|4|8FC7BF,4|5|3DA57C,-2|6|69EABD,-2|1|84F0D6,-5|3|69E7CB,1|-2|7EF0D9,4|0|62EDCB", 0, 0.92, "????1"
    )
    override val BuilderBaseCollectGem2 = ColorSchema.parse(
        115, 106, 1127, 609, "85F1D9", "-1|-3|74EBD1,4|-5|7BF0DC,3|0|5FE9C1,-13|0|9CCDC7,-12|-8|B4DAD5,13|-9|B6DAD6,3|1|7AF0D1,-2|7|24B48C,4|4|6DF0CC", 0, 0.92, "????2"
    )
}
