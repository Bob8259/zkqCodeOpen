@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseMagicItemColors {
    // Magic item type detection icons shown in the magic item dialog
    val MagicItemIcon: ColorSchema
    val MagicItemIcon2: ColorSchema
    val MagicItemIcon3: ColorSchema
    val MagicItemIcon4: ColorSchema

    // Individual potion type identifiers
    val BuilderPotion: ColorSchema
    val ResearchPotion: ColorSchema
    val ResourcePotion: ColorSchema
    val WallRings: ColorSchema
    val ClockTowerPotion: ColorSchema

    // Action buttons inside the magic item use dialog
    val UsePotionButton: ColorSchema
    val SellPotionButton: ColorSchema
}

object MainBaseMagicItemColors : IMainBaseMagicItemColors {
    // Magic item dialog header icon variant 1
    override val MagicItemIcon = ColorSchema.parse(
        167, 502, 1124, 628, "2B2E66", "13|0|6FA7D9,26|0|89BEEB,38|0|B8D7D3,51|0|BEDDD8,0|21|282C60,13|21|9BD5FF,26|21|89BDEA,38|21|DF8442,51|21|FBA05D", 0, 0.9, "魔法道具图标"
    )

    // Magic item dialog header icon variant 2
    override val MagicItemIcon2 = ColorSchema.parse(
        190, 513, 1106, 631, "2E316C", "13|0|76ADDF,27|0|90C7F4,41|0|C1DEDB,54|0|868684,0|17|2E3163,13|17|93CFFC,27|17|8DC3F0,41|17|EF9553,54|17|FA9B56", 0, 0.9, "魔法物品按钮7按钮"
    )

    // Magic item dialog header icon variant 3
    override val MagicItemIcon3 = ColorSchema.parse(
        179, 525, 1104, 631, "2B37BA", "10|0|9AD3FF,20|0|719BC5,29|0|BBD5D2,39|0|B3A8A3,0|17|2531B4,10|17|99D2FE,20|17|5A7BA3,29|17|D17028,39|17|FDA362", 0, 0.9, "魔法物品按钮8按钮"
    )
    override val MagicItemIcon4 = ColorSchema.parse(
        179, 557, 1105, 630, "2934B7", "7|3|98D0FD,0|13|2F3ABF,-4|4|1E345A,6|5|9AD3FF,20|13|CB6A23,25|9|FCA362,28|8|FCA05C,25|7|FFAD70,12|6|89BDE9", 0, 0.9, "魔法道具12按钮"
    )

    // Builder potion detected by its golden icon in the magic item list
    override val BuilderPotion = ColorSchema.parse(
        280, 170, 1000, 520, "E4B749", "13|3|C19832,13|18|C29734,39|-19|7DD7F6,36|-15|7DD7F6,34|-6|7DD7F5,2|-46|9AD7FF,8|-55|95D0FF,13|-51|92CCFD,13|-34|394B59", 0, 0.9, "工人药水"
    )

    // Research potion detected by its purple icon in the magic item list
    override val ResearchPotion = ColorSchema.parse(
        280, 170, 1000, 520, "430F49", "-16|-6|700D98,0|-17|68506C,17|-6|524440,26|-17|8FF3FF,28|-1|5C6B7F,-2|-58|94CFFF,-8|-2|580D67,-7|8|570D65,-15|-8|6B0D8F", 0, 0.9, "研究药水"
    )

    // Resource potion detected by its pink icon in the magic item list
    override val ResourcePotion = ColorSchema.parse(
        280, 170, 1000, 520, "F850DF", "10|11|F22FD2,3|15|F549D7,-7|2|FB5CE8,8|-14|FDEBF8,30|-11|A3FFFF,40|-5|71B7E7,31|1|629ECE,5|-52|90C9FB,17|-49|95CFFF", 0, 0.9, "资源药水"
    )

    // Wall rings detected by their teal icon in the magic item list
    override val WallRings = ColorSchema.parse(
        280, 170, 1000, 520, "7CD2F5", "3|16|3F6A9C,-38|3|6197B6,-43|-20|63A4D6,-27|-26|4B80B6,-5|-30|476990,10|-23|665669,19|-15|5C8ABD,21|0|4577AB,14|18|3D5C86", 0, 0.9, "壁垒之戒"
    )

    // Clock tower potion detected by its pink/white icon in the magic item list
    override val ClockTowerPotion = ColorSchema.parse(
        280, 170, 1000, 520, "FBCAE1", "-8|-2|FFDBE8,-14|-19|FFDAE7,-6|-19|FDCFE3,4|-6|F9C3DE,33|-15|8DEDFF,36|-9|7AD6F5,28|-13|3F5B7C,4|-56|94CFFF,12|-62|9AD7FF", 0, 0.9, "钟楼药水"
    )

    // Green confirm button shown in the magic item use dialog
    override val UsePotionButton = ColorSchema.parse(
        750, 546, 978, 623, "75E4AF", "45|0|75E4AF,91|0|75E4AF,137|0|75E4AF,182|0|75E4AF,0|39|3AD48B,45|39|3AD48B,91|39|3AD48B,137|39|3AD48B,182|39|3AD48B", 0, 0.9
    )

    // Blue sell button shown in the magic item use dialog
    override val SellPotionButton = ColorSchema.parse(
        299, 548, 530, 623, "221EF7", "46|0|221EF7,93|0|221EF7,139|0|221EF7,185|0|221EF7,0|37|221EF7,46|37|221EF7,93|37|221EF7,139|37|221EF7,185|37|221EF7", 0, 0.9
    )
}
