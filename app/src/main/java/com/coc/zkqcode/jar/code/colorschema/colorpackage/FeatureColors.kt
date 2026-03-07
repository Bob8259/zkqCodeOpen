package com.coc.zkqcode.jar.code.colorschema.colorpackage

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IFeatureColors {
    val TrainTroops: ColorSchema
    val RebuildBuilderBase: ColorSchema
    val UpgradeToTH6: ColorSchema
    val OrangeTutorialArrow: ColorSchema
    val ResearchIcon: ColorSchema
}

object FeatureColors : IFeatureColors {
    override val TrainTroops: ColorSchema = ColorSchema.parse(
        17, 483, 88, 559, "A2BFF1", "-7|15|D3DFFF,-13|7|87FFFF,-13|-5|60F8FF,-10|-11|5CF2FF,-3|-11|4CD3FF,10|-10|C9AC97,17|-14|FFFFFF,17|-3|FAFF78,15|0|F9FF6E", 0, 0.9
    )
    override val RebuildBuilderBase: ColorSchema = ColorSchema.parse(
        580, 501, 706, 625, "DAE4F3", "4|-10|E2E1ED,12|-16|D3D2E1,14|-18|D2D1E0,10|-3|DCEAFF,23|2|5684F0,24|2|5583F2,32|5|365AA7,38|13|6187F0,42|15|6D88EB", 0, 0.9, "重建夜世界帆船"
    )
    override val UpgradeToTH6: ColorSchema = ColorSchema.parse(
        700, 189, 725, 221, "1919FF", "2|-6|1919FF,2|-6|1919FF,6|0|1919FF,8|2|1919FF,11|4|1919FF,7|8|1919FF,0|7|1919FF,0|5|1919FF,1|1|1919FF", 0, 0.9, "需要将大本营升至6级"
    )
    override val OrangeTutorialArrow: ColorSchema = ColorSchema.parse(
        513, 415, 843, 668, "22ADFD", "6|0|20B5FC,12|0|20BAFD,17|0|20B9FD,23|0|1FAFFD,0|23|58EAF1,6|23|51E7F2,12|23|4FE4F1,17|23|4DE3F2,23|23|4CE0F0", 0, 0.9, "教程橙色箭头"
    )
    override val ResearchIcon: ColorSchema = ColorSchema.parse(
        320, 12, 959, 73, "F727C7", "5|0|F300B1,10|0|F006A4,14|0|ED0898,19|0|E00786,0|8|F21DAE,5|8|A1E6EE,10|8|657FAE,14|8|FFFFFF,19|8|FFFFFF", 0, 0.9, "研究标志"
    )
}
