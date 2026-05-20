@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseClanWarColors {
    val ClanWarInfo: ColorSchema
    val OurTeam: ColorSchema
    val MyPositionGreen: ColorSchema
    val ClanWarSwordIcon: ColorSchema
    val ClanWarAttack: ColorSchema
    val ClanWarAttack2: ColorSchema
    val RecommendedOpponent: ColorSchema
    val ClanLeagueGreenWarIcon: ColorSchema
    val IncreaseRank: ColorSchema
    val ClanLeagueSeasonInfo: ColorSchema
    val SmallBlueInfoIcon: ColorSchema
    val ClaimClanLeagueReward: ColorSchema

    // Clan war start and join buttons
    val StartClanWar: ColorSchema
    val InnerStartClanWar: ColorSchema
    val GrayInnerButton: ColorSchema
    val JoinedClanWar: ColorSchema
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
    override val ClanWarAttack2 = ColorSchema.parse(
        662, 536, 781, 573, "50C6FF", "24|0|50C6FF,48|0|50C6FF,71|0|50C6FF,95|0|50C6FF,0|19|1257EE,24|19|1257ED,48|19|1141AB,71|19|1357ED,95|19|1357ED", 0, 0.9, "部落战橙色进攻2"
    )

    // Recommended opponent indicator
    override val RecommendedOpponent = ColorSchema.parse(
        311, 232, 1092, 708, "2C98FD", "0|1|2D99FD,0|2|2C99FE,0|3|2C98FC,0|4|2C98FD,0|5|2D99FD,0|6|2C99FE,0|7|2C98FD,0|8|2C98FD,0|9|2C99FE", 0, 0.92, "推荐目标"
    )

    // Green war icon in clan league
    override val ClanLeagueGreenWarIcon = ColorSchema.parse(
        291, 612, 998, 705, "0DB63D", "1|11|0DB63D,0|27|0DB23B,39|25|0DB63D,29|12|0DB63D,39|4|0DB63D,39|-3|0DB63D,-1|-7|0DB63D,1|9|0DB63D,3|21|0DB63D", 0, 0.9, "竞赛绿色标志"
    )

    // Rank increase indicator
    override val IncreaseRank = ColorSchema.parse(
        662, 604, 837, 644, "88E9BB", "35|0|88E8BB,70|0|88E9BB,105|0|88E9BB,140|0|88E9BB,0|20|3AD48B,35|20|3AD48B,70|20|3AD48B,105|20|3AD48B,140|20|3AD48B", 0, 0.9, "段位提示"
    )

    // Clan league season info panel
    override val ClanLeagueSeasonInfo = ColorSchema.parse(
        1125, 597, 1268, 711, "CF933D", "2|14|CF933D,16|12|C68D3B,38|-11|2F5D74,61|-14|65C1E8,75|-10|CF933D,86|-1|CF933D,90|17|CF933D,81|58|CF933D,14|53|CF933D", 0, 0.9, "赛季信息"
    )

    // Small blue info icon button
    override val SmallBlueInfoIcon = ColorSchema.parse(
        1097, 215, 1144, 258, "FFFFFF", "0|4|FFFFFF,-12|4|885119,-14|-7|D29866,-9|15|61300D,0|15|773C17,6|15|6F360D,10|9|66380D,12|1|73480D,12|-15|F4D372", 0, 0.9, "蓝色信息按钮"
    )

    // Claim clan league reward tutorial button
    override val ClaimClanLeagueReward = ColorSchema.parse(
        369, 383, 583, 496, "7BF7DA", "18|-3|81F7DD,37|0|7BF7DA,76|0|7BF7DA,131|5|70F3D2,-20|-43|FFFFFF,27|-43|FFFFFF,79|-41|FFFFFF,121|-41|FFFFFF,193|-49|FFFFFF", 0, 0.98, "领取部落联赛奖励教程"
    )

    // Clan war start button
    override val StartClanWar = ColorSchema.parse(
        654, 508, 1003, 669, "37BCFC", "31|0|37BCFC,62|0|37BCFC,93|0|37BCFC,124|0|37BCFC,0|30|0D5AE8,31|30|0D5AE8,62|30|0D5AE8,93|30|0D5AE8,124|30|0D5AE8", 0, 0.9, "发起战争"
    )
    override val InnerStartClanWar = ColorSchema.parse(
        964, 586, 1109, 647, "37BCFC", "29|0|0D4393,58|0|83BAFF,87|0|37BCFC,116|0|37BCFC,0|31|0D5AE8,29|31|2E80ED,58|31|3D8FFC,87|31|0D5AE8,116|31|0D5AE8", 0, 0.9, "内部发起战争"
    )
    override val GrayInnerButton = ColorSchema.parse(
        964, 586, 1109, 647, "C1C1C1", "29|0|555555,58|0|CCCCCC,87|0|C1C1C1,116|0|C1C1C1,0|31|7C7C7C,29|31|989898,58|31|A7A7A7,87|31|7C7C7C,116|31|7C7C7C", 0, 0.9, "灰色发起战争"
    )
    override val JoinedClanWar = ColorSchema.parse(
        959, 97, 1134, 540, "8CEABD", "28|0|8CEABD,56|0|8CEABD,83|0|8CEABD,111|0|8CEABD,0|19|3AD48B,28|19|3AD48B,56|19|3AD48B,83|19|3AD48B,111|19|3AD48B", 1, 0.95, "参战按钮"
    )
}
