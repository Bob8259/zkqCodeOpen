@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IFeatureColors {
    val TrainTroops: ColorSchema
    val RebuildBuilderBase: ColorSchema
    val UpgradeToTH6: ColorSchema
    val OrangeTutorialArrow: ColorSchema
    val ResearchIcon: ColorSchema
    val WhiteNumberColor: ColorSchema
    val MiddleGreenConfirm: ColorSchema
}

object FeatureColors : IFeatureColors {
    override val TrainTroops = ColorSchema.parse(
        17, 483, 88, 559, "A2BFF1", "-7|15|D3DFFF,-13|7|87FFFF,-13|-5|60F8FF,-10|-11|5CF2FF,-3|-11|4CD3FF,10|-10|C9AC97,17|-14|FFFFFF,17|-3|FAFF78,15|0|F9FF6E", 0, 0.9
    )
    override val RebuildBuilderBase = ColorSchema.parse(
        580, 501, 706, 625, "DAE4F3", "4|-10|E2E1ED,12|-16|D3D2E1,14|-18|D2D1E0,10|-3|DCEAFF,23|2|5684F0,24|2|5583F2,32|5|365AA7,38|13|6187F0,42|15|6D88EB", 0, 0.9, "重建夜世界帆船"
    )
    override val UpgradeToTH6 = ColorSchema.parse(
        700, 189, 725, 221, "1919FF", "2|-6|1919FF,2|-6|1919FF,6|0|1919FF,8|2|1919FF,11|4|1919FF,7|8|1919FF,0|7|1919FF,0|5|1919FF,1|1|1919FF", 0, 0.9, "需要将大本营升至6级"
    )
    override val OrangeTutorialArrow = ColorSchema.parse(
        513, 415, 843, 668, "22ADFD", "6|0|20B5FC,12|0|20BAFD,17|0|20B9FD,23|0|1FAFFD,0|23|58EAF1,6|23|51E7F2,12|23|4FE4F1,17|23|4DE3F2,23|23|4CE0F0", 0, 0.9, "教程橙色箭头"
    )
    override val ResearchIcon = ColorSchema.parse(
        374, 18, 900, 62, "F900D1", "3|0|F004A4,7|0|ED0599,10|0|E80691,13|0|E00587,0|8|F001AD,3|8|4BBFD1,7|8|59E2F2,10|8|FFFFFF,13|8|FFFFFF", 0, 0.9, "研究标志"
    )
    override val WhiteNumberColor = ColorSchema.parse(
        338, 102, 966, 560, "FFFFFF", "-1|1|FFFFFF,-1|2|FFFFFF,-1|3|FFFFFF,-1|4|FFFFFF,7|5|FFFFFF,7|4|FFFFFF,7|3|FFFFFF,7|2|FFFFFF,7|1|FFFFFF", 0, 0.99,
    )
    override val MiddleGreenConfirm = ColorSchema.parse(
        546, 427, 743, 501, "75F4D6", "39|0|75F4D6,79|0|75F4D6,118|0|74F4D6,157|0|74F4D6,0|37|20BE6F,39|37|20BE6F,79|37|20BE6F,118|37|20BE6F,157|37|1FBE6F", 0, 0.9, "中绿确认按钮"
    )
}
