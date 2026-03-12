package com.coc.zkqcode.jar.code.colorschema.colorpackage

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IUniversalUpgradeColors {
    val UpgradeGemIcon: ColorSchema
    val UpgradeGemIcon2: ColorSchema
    val UpgradeGemIcon3: ColorSchema
}

object UniversalUpgradeColors : IUniversalUpgradeColors {
    override val UpgradeGemIcon = ColorSchema.parse(
        189, 501, 1117, 625, "7AF5D9", "6|0|81F9DE,12|0|8AFCE4,17|0|92FFEB,23|0|9CFFF1,0|13|8FFBDC,6|13|81F8D2,12|13|78F8CF,17|13|78FBD5,23|13|67E3BF", 0, 0.93, "升级宝石标志"
    )
    override val UpgradeGemIcon2 = ColorSchema.parse(
        189, 501, 1117, 625, "50D9A3", "5|0|77F5DA,10|0|7EF8DF,15|0|87FCE7,20|0|8FFFED,0|12|8CFADE,5|12|92FDE4,10|12|7EF7D3,15|12|87FCDD,20|12|79FCDA", 0, 0.93, "升级宝石标志2"
    )
    override val UpgradeGemIcon3 = ColorSchema.parse(
        189, 501, 1117, 625, "50D9A3", "5|0|77F5DA,10|0|7EF8DF,15|0|87FCE7,20|0|8FFFED,0|12|8CFADE,5|12|92FDE4,10|12|7EF7D3,15|12|87FCDD,20|12|79FCDA", 0, 0.93, "升级宝石标志8按钮"
    )

}
