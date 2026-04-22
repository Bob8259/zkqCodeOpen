@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseClanWarColors {
    val ClanWarInfo: ColorSchema
    val OurTeam: ColorSchema
    val MyPositionGreen: ColorSchema
    val ClanWarSwordIcon: ColorSchema
    val ClanWarAttack: ColorSchema
    val RecommendedOpponent: ColorSchema
}

object MainBaseClanWarColors : IMainBaseClanWarColors {
    // Clan war detail info panel
    override val ClanWarInfo = ColorSchema.parse(
        1150, 581, 1244, 667, "EBFDFE", "30|-9|EBFDFE,15|-20|FFFFFF,5|-31|FFFFFF,-6|-43|FFFFFF,-22|-40|FFFFFF,-13|-27|FFFFFF,7|-33|FFFFFF,-17|-10|EBFDFE,24|-22|FFFFFF", 0, 0.9, "对战详细信息"
    )

    // Our team indicator in clan war
    override val OurTeam = ColorSchema.parse(
        201, 104, 730, 161, "F4F2EC", "-4|-6|F3EFE2,2|-7|FAF8F3,7|-3|F7F4EE,6|0|F5F3ED,-464|7|1C3D68,-456|-1|B89581,-450|-5|B28C74,-449|-5|B38C74,-445|-9|AF8971", 0, 0.97, "我方战队"
    )

    // Green highlight for my position in clan war
    override val MyPositionGreen = ColorSchema.parse(
        140, 185, 160, 625, "81CCB1", "4|0|81CCB1,6|1|81CCB1,10|2|81CCB1,9|5|81CCB1,3|5|81CCB1,-2|5|81CCB1,-2|8|80CBB0,2|7|80CBB0,4|5|81CCB1", 0, 0.97, "绿色图标"
    )

    // Sword icon indicating clan war
    override val ClanWarSwordIcon = ColorSchema.parse(
        51, 398, 86, 426, "1D15ED", "3|-4|1D15ED,6|-6|1D15ED,10|-8|1D15ED,13|-1|FFFFFF,16|-4|FFFFFF,11|0|FFFFFF,16|8|1D15ED,18|4|1D15ED,20|2|1D15ED", 0, 0.9, "部落战剑标志"
    )

    // Clan war attack button
    override val ClanWarAttack = ColorSchema.parse(
        750, 527, 874, 570, "51C6FF", "25|0|51C6FF,50|0|51C6FF,75|0|51C6FF,100|0|51C6FF,0|21|1356EE,25|21|1356EE,50|21|1356EE,75|21|1356EE,100|21|1356EE", 0, 0.9, "部落战橙色进攻"
    )

    // Recommended opponent indicator
    override val RecommendedOpponent = ColorSchema.parse(
        311, 232, 1092, 708, "2C98FD", "0|1|2D99FD,0|2|2C99FE,0|3|2C98FC,0|4|2C98FD,0|5|2D99FD,0|6|2C99FE,0|7|2C98FD,0|8|2C98FD,0|9|2C99FE", 0, 0.92, "推荐目标"
    )
}
