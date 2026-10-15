@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IFeatureColors {
    val TrainTroops: ColorSchema
    val TrainTroopsGlobal: ColorSchema
    val TrainTroops2: ColorSchema
    val TrainTroops3: ColorSchema
    val RebuildBuilderBase: ColorSchema
    val UpgradeToTH6: ColorSchema
    val UpgradeToTH6_2: ColorSchema
    val OrangeTutorialArrow: ColorSchema
    val ResearchIcon: ColorSchema
    val ResearchIcon2: ColorSchema
    val WhiteNumberColor: ColorSchema
    val MiddleGreenConfirm: ColorSchema
    val ItemsToBePlaced: ColorSchema
    val ItemsToBePlaced2: ColorSchema

    // Edit mode indicator
    val EditMode: ColorSchema
    val EditMode2: ColorSchema
}

object FeatureColors : IFeatureColors {
    override val TrainTroops = ColorSchema.parse(
        17, 483, 88, 559, "A2BFF1", "-7|15|D3DFFF,-13|7|87FFFF,-13|-5|60F8FF,-10|-11|5CF2FF,-3|-11|4CD3FF,10|-10|C9AC97,17|-14|FFFFFF,17|-3|FAFF78,15|0|F9FF6E", 0, 0.9
    )
    override val TrainTroopsGlobal = ColorSchema.parse(
        22, 578, 147, 702, "0FC8FC", "25|0|1ED4FF,50|0|25D8FE,99|-14|0EC9FB,100|0|0FC8FC,0|62|11BBFE,25|62|22C8FF,50|62|29CEFF,75|62|21C7FF,100|62|10BAFD", 0, 0.9
    )
    override val TrainTroops2 = ColorSchema.parse(
        17, 483, 88, 559, "57F8FF", "33|13|E4DF1E,31|9|DBD815,11|0|57E8FF,14|0|50DEFF,0|17|E2CDEC,3|17|AF9EAF,7|17|ECDDF4,11|17|E3C7E3,14|17|35576E", 0, 0.9
    )
    override val TrainTroops3 = ColorSchema.parse(
        12, 487, 97, 564, "664812", "4|2|C1CBFF,8|7|61F6FF,4|15|C8D3FF,-4|13|47B3EB,-4|1|FFFEF0,-1|-9|61FBFF,5|-9|5BF2FF,14|-12|48BFFA,14|4|87A9FF", 0, 0.9
    )
    override val RebuildBuilderBase = ColorSchema.parse(
        580, 501, 706, 625, "DAE4F3", "4|-10|E2E1ED,12|-16|D3D2E1,14|-18|D2D1E0,10|-3|DCEAFF,23|2|5684F0,24|2|5583F2,32|5|365AA7,38|13|6187F0,42|15|6D88EB", 0, 0.9, "重建夜世界帆船"
    )
    override val UpgradeToTH6 = ColorSchema.parse(
        700, 189, 725, 221, "1919FF", "2|-6|1919FF,2|-6|1919FF,6|0|1919FF,8|2|1919FF,11|4|1919FF,7|8|1919FF,0|7|1919FF,0|5|1919FF,1|1|1919FF", 0, 0.9, "需要将大本营升至6级"
    )
    override val UpgradeToTH6_2 = ColorSchema.parse(
        627, 188, 763, 227, "1919FF", "-4|2|1919FF,-6|5|1919FF,-6|10|1919FF,-6|13|1919FF,-4|16|1919FF,0|16|1919FF,3|13|1919FF,3|10|1919FF,1|8|1919FF", 0, 0.9, "需要将大本营升至6级"
    )
    override val OrangeTutorialArrow = ColorSchema.parse(
        513, 415, 843, 668, "22ADFD", "6|0|20B5FC,12|0|20BAFD,17|0|20B9FD,23|0|1FAFFD,0|23|58EAF1,6|23|51E7F2,12|23|4FE4F1,17|23|4DE3F2,23|23|4CE0F0", 0, 0.9, "教程橙色箭头"
    )
    override val ResearchIcon = ColorSchema.parse(
        374, 18, 900, 62, "F900D1", "3|0|F004A4,7|0|ED0599,10|0|E80691,13|0|E00587,0|8|F001AD,3|8|4BBFD1,7|8|59E2F2,10|8|FFFFFF,13|8|FFFFFF", 0, 0.9,
    )
    override val ResearchIcon2 = ColorSchema.parse(
        374, 18, 900, 62, "F700CE", "3|0|EF0094,7|0|E8028E,10|0|E40389,13|0|DB0380,32|-2|FAF8F7,30|7|B9AFA5,7|7|62D8E7,10|7|FFFFFF,13|7|FFFFFF", 0, 0.9
    )
    override val WhiteNumberColor = ColorSchema.parse(
        338, 102, 966, 560, "FFFFFF", "-1|1|FFFFFF,-1|2|FFFFFF,-1|3|FFFFFF,-1|4|FFFFFF,7|5|FFFFFF,7|4|FFFFFF,7|3|FFFFFF,7|2|FFFFFF,7|1|FFFFFF", 0, 0.99,
    )
    override val MiddleGreenConfirm = ColorSchema.parse(
        546, 427, 743, 501, "75F4D6", "39|0|75F4D6,79|0|75F4D6,118|0|74F4D6,157|0|74F4D6,0|37|20BE6F,39|37|20BE6F,79|37|20BE6F,118|37|20BE6F,157|37|1FBE6F", 0, 0.9, "中绿确认按钮"
    )
    // Place item colours
    override val ItemsToBePlaced = ColorSchema.parse(
        563, 461, 719, 496, "B7FEFF", "0|1|B7FEFF,0|2|B7FEFF,5|12|B7FEFE,5|13|B7FEFE,-24|10|B7FFFF,-117|8|B4FBFB,-115|8|B4FBFC,-112|8|B7FEFE,-110|8|B7FFFF", 0, 0.95, "待放置的物品"
    )
    override val ItemsToBePlaced2 = ColorSchema.parse(
        563, 461, 719, 496, "B7FFFF", "0|5|B6FFFF,7|1|B4FBFC,12|1|B4FAFB,38|4|B7FFFF,50|4|B6FEFE,86|-7|B2F9FB,100|-6|B5FCFC,127|-3|ACEBE9,141|4|B6FEFF", 0, 0.95, "待放置的物品2"
    )

    // Edit mode indicator
    override val EditMode = ColorSchema.parse(
        6, 607, 271, 707, "AE8A39", "4|15|C8A142,10|34|C28A37,101|-9|E8C256,103|14|F1BF4C,103|32|DC9F3C,141|-8|E9C356,147|12|F6CF5C,144|32|D2983C,238|-6|F4CB55", 0, 0.9, "编辑模式"
    )
    override val EditMode2 = ColorSchema.parse(
        41, 586, 131, 708, "F2D263", "2|13|EFCB5A,1|25|E7B24A,-1|41|DDA842,-2|53|DFA13F,-4|61|C68D39,-7|60|CB8E39,-9|38|F3C652,-10|24|EBBA4E,-4|9|E8C559", 0, 0.9, "编辑模式2"
    )
}
