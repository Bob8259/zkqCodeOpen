@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseClanWarColors {
    val ClanWarInfo: ColorSchema
    val ClanWarPageIcon: ColorSchema
    val OurTeam: ColorSchema
    val MyPositionGreen: ColorSchema
    val ClanWarSwordIcon: ColorSchema
    val ClanWarAttack: ColorSchema
    val ClanWarAttack2: ColorSchema
    val RecommendedOpponent: ColorSchema
    val RecommendedOpponent2: ColorSchema
    val ClanLeagueGreenWarIcon: ColorSchema
    val IncreaseRank: ColorSchema
    val ClanLeagueSeasonInfo: ColorSchema
    val SmallBlueInfoIcon: ColorSchema
    val ClaimClanLeagueReward: ColorSchema
    val ClanWarPageKingAndQueen: ColorSchema

    // Clan war start and join buttons
    val StartClanWar: ColorSchema
    val InnerStartClanWar: ColorSchema
    val GrayInnerButton: ColorSchema
    val JoinedClanWar: ColorSchema

    // Clan war league registration buttons
    val ClanLeagueEntryIndicator: ColorSchema
    val ClanLeagueEntryIndicator2: ColorSchema
    val StartClanLeagueButton: ColorSchema
    val ConfirmClanLeagueRegistration: ColorSchema
    val ClanWarEndButton: ColorSchema
    val LeagueWhiteCloud: ColorSchema
    val LeaguePreparePhase: ColorSchema
    val LeaguePreparePhase2: ColorSchema
    val LeagueGrayArrow: ColorSchema
    val LeaguePercentIndicator: ColorSchema
    val LeagueOcrSkipRegion: ColorSchema
    val LeagueReinforceRequest: ColorSchema
    val LeagueDonatableTroop: ColorSchema
    val ArcherDonatableTroop: ColorSchema
    val FlameFlingerDonatableTroop: ColorSchema
    val LeagueFirstMemberIndicator: ColorSchema

    // Clan league reward distribution
    val DistributeClanLeagueRewards: ColorSchema
    val SelectPlayerButton: ColorSchema
}

object MainBaseClanWarColors : IMainBaseClanWarColors {
    // Clan war page icon
    override val ClanWarPageIcon = ColorSchema.parse(
        28, 414, 55, 465, "15171B", "5|0|AAD1F4,11|0|F8FFFF,16|0|FFFFFF,21|0|3A67C2,0|25|0D0D0D,5|25|467FB7,11|25|5E9BD8,16|25|0F1113,21|25|233A68", 0, 0.9, "部落战标志"
    )

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
        310, 230, 1090, 710, "2C98FD", "0|1|2D99FD,0|2|2C99FE,0|3|2C98FC,0|4|2C98FD,0|5|2D99FD,0|6|2C99FE,0|7|2C98FD,0|8|2C98FD,0|9|2C99FE", 0, 0.92, "推荐目标"
    )
    override val RecommendedOpponent2 = ColorSchema.parse(
        310, 230, 1090, 710, "2C98FD", "0|2|2C99FE,0|5|2C98FE,0|8|2C98FE,10|8|3795EC,10|6|3291E9,10|4|3391EA,10|3|3495ED,7|3|3B8DD9,5|7|3685CD", 0, 0.92, "推荐目标2"
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

    // Clan war page indicator showing the King and Queen
    override val ClanWarPageKingAndQueen = ColorSchema.parse(
        373, 75, 973, 285, "789AEE", "-7|12|6B89DB,-48|17|191E64,30|24|6B86CC,57|27|536AA9,245|26|254F37,222|21|3D512F,215|5|15272F,223|-45|869DF1,230|-72|9C4966", 0, 0.9, "部落站界面蛮王女王"
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

    // Clan war league entry indicator (shown on home page when league is available)
    override val ClanLeagueEntryIndicator = ColorSchema.parse(
        19, 406, 86, 472, "E0FAFF", "0|-31|DBFFFF,3|-22|3E6ED3,-15|-18|28ACE8,-4|-9|BDEAFF,-25|-2|0DBADE,-11|22|0DA2C2,30|-13|92EDFC,20|-17|28ABE5,1064|-109|E0E8E8", 0, 0.9, "联赛入口标志"
    )
    override val ClanLeagueEntryIndicator2 = ColorSchema.parse(
        25, 410, 64, 466, "6ACCF6", "8|0|16566E,16|0|0E0F10,23|0|3B6DCE,31|0|3A72D6,0|28|10B5D5,8|28|FFFFFF,16|28|C6E0FF,23|28|C7E0FA,31|28|DFF9FF", 0, 0.9, "联赛入口标志2"
    )

    // Clan war league registration button
    override val StartClanLeagueButton = ColorSchema.parse(
        528, 428, 751, 504, "3BBCFC", "45|0|3BBCFC,90|0|3BBCFC,134|0|3BBCFC,179|0|3BBDFC,0|38|343C4B,45|38|1052E4,90|38|1052E4,134|38|1052E4,179|38|1052E4", 0, 0.9, "开联赛"
    )

    // Confirm clan league registration button
    override val ConfirmClanLeagueRegistration = ColorSchema.parse(
        884, 606, 1137, 676, "3E74DB", "51|0|3BBBFB,102|0|3BBBFB,152|0|3BBBFB,203|0|3BBBFB,0|35|CBE2FF,51|35|1057E5,102|35|1057E5,152|35|1057E5,203|35|1057E5", 0, 0.9, "联赛报名确认"
    )

    // Clan war end button (may appear after entering clan war page during league registration)
    override val ClanWarEndButton = ColorSchema.parse(
        762, 589, 981, 656, "396DD2", "44|0|37BCFC,88|0|37BCFC,131|0|37BCFC,175|0|37BCFC,0|33|203B6B,44|33|0D55E6,88|33|0D55E6,131|33|0D55E6,175|33|0D55E6", 0, 0.9, "部落战结束按钮"
    )

    override val LeagueWhiteCloud = ColorSchema.parse(
        1138, 580, 1256, 702, "CC9038", "23|0|65ABC5,47|0|1C3748,71|0|6DC3EB,94|0|CC9038,0|61|D0933E,23|61|C1AF95,47|61|13110F,71|61|EFDCC1,94|61|D0933E", 0, 0.9, "联赛白云图色"
    )

    override val LeaguePreparePhase = ColorSchema.parse(
        309, 619, 981, 647, "3B6ED0", "0|-10|D3E4F6,-9|-4|C2D8EB,-8|5|BAD4EB,0|11|BAD4EB,8|4|C2D9EF,9|-5|D3E6F4,14|0|3E79EA,-14|0|3B6ED0,-1|18|4B82E3", 0, 0.9, "联赛准备阶段图色"
    )
    override val LeaguePreparePhase2 = ColorSchema.parse(
        309, 619, 981, 647, "3B6ED0", "0|-7|CDDFF0,8|-4|D0E3F6,9|5|C2D9EF,-1|10|BAD4EB,-9|4|B9D4EB,-9|-7|C1E4EC,-9|0|BAD4EB,16|0|4982E9,-15|0|3E79E1", 0, 0.9, "联赛准备阶段图色2"
    )

    override val LeagueGrayArrow = ColorSchema.parse(
        925, 541, 961, 599, "BBBBBB", "7|0|CCCCCC,14|0|CBCBCB,21|0|CBCBCB,28|0|CBCBCB,0|29|A7A7A7,7|29|ABABAB,14|29|ABABAB,21|29|A9A9A9,28|29|ACACAC", 0, 0.9, "联赛灰色切换箭头"
    )

    override val LeaguePercentIndicator = ColorSchema.parse(
        598, 543, 718, 556, "1EA16B", "", 0, 0.9, "联赛百分比指示器"
    )

    override val LeagueOcrSkipRegion = ColorSchema.parse(
        552, 528, 597, 569, "FFFFFF", "", 0, 0.9, "联赛OCR跳过区域"
    )

    override val LeagueReinforceRequest = ColorSchema.parse(
        409, 535, 540, 566, "7BF7D8", "26|0|7BF7D8,53|0|7BF7D8,79|0|7BF6D7,105|0|7AF5D5,0|15|1DBF70,26|15|1DBF70,53|15|145F3B,79|15|20BF70,105|15|25BF70", 0, 0.9, "联赛增援请求"
    )

    override val LeagueDonatableTroop = ColorSchema.parse(
        261, 166, 1020, 414, "9CA5A8", "-7|-17|F3F47D,11|-8|DECB80,20|-15|EDE97B,-3|-30|F4F4F4,10|7|5F585B,4|22|D3D4DB,29|21|BDB3BE,18|-22|CCCACC,-35|-26|FCC379", 0, 0.9, "联赛可捐赠兵种"
    )

    override val ArcherDonatableTroop = ColorSchema.parse(
        261, 166, 1020, 414, "626DAB", "1|-13|7039CA,-28|-23|DB72FC,5|-35|AC5DFC,3|11|3E1D80,11|23|39BA9C,0|25|A1FFFF,-8|32|2BB999,17|33|A6C2FF,-7|-7|1A2022", 0, 0.9, "联赛可捐赠弓箭手"
    )

    override val FlameFlingerDonatableTroop = ColorSchema.parse(
        261, 166, 1020, 414, "27C1F1", "13|1|71859B,2|21|777A7D,-4|13|384ACA,-26|-4|399CE1,16|-27|F5FFFF,16|-38|4F4F53,-3|-41|457CD7,-12|-22|2669DB,3|-5|2124B5", 0, 0.9, "联赛可捐赠烈焰锅炉"
    )

    override val LeagueFirstMemberIndicator = ColorSchema.parse(
        925, 543, 958, 597, "5FA7FF", "7|0|57C2FF,14|0|57C1FF,20|0|57C1FF,27|0|57C1FF,0|27|468CFF,7|27|4793FF,14|27|4991FF,20|27|4990FF,27|27|4792FF", 0, 0.9, "联赛第一个成员指示器"
    )

    // Distribute clan league rewards button
    override val DistributeClanLeagueRewards = ColorSchema.parse(
        522, 622, 794, 699, "FFD9A7", "18|-2|FFDBAB,67|-1|FFDBAA,88|0|FFD9A7,118|0|FFD9A7,169|20|69FCFF,181|20|7FE6FD,110|48|D47636,58|45|D47636,36|44|D47636", 0, 0.9, "发放联赛奖励"
    )

    // Select player button in the reward distribution dialogue
    override val SelectPlayerButton = ColorSchema.parse(
        1006, 304, 1143, 614, "E5C286", "30|-1|E5C286,65|-1|E5C286,83|7|E2BA78,78|22|CF922E,68|22|CF922E,19|24|CD912D,11|23|CE922E,4|18|CF932F,4|1|E5C286", 0, 0.9, "选择玩家"
    )

    // League donatable troop list: pairs of display name -> ColorSchema.
    // This list must be kept in sync with the dropdown options in MainBaseConfigs.kt.
    // Add new troops here when more ColorSchema entries become available.
    val leagueDonatableTroopList: List<Pair<String, ColorSchema>> = listOf(
        "戈仑冰人" to LeagueDonatableTroop,
        "弓箭手" to ArcherDonatableTroop,
        "烈焰锅炉" to FlameFlingerDonatableTroop,
    )

    // Convenience: extract just the display names for UI dropdowns.
    val leagueDonatableTroopNames: List<String> get() = listOf("无") + leagueDonatableTroopList.map { it.first }
}
