@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseClanColors {
    val IUnderstand: ColorSchema
    val JoinClanButton: ColorSchema
    val ExitClanButton: ColorSchema
    val ApplyClanSetting: ColorSchema
    val SearchOptions: ColorSchema
    val NotJoinClanFlag: ColorSchema
    val ClanChatIcon: ColorSchema
    val DonationButton: ColorSchema
    val DonateSuperTroops: ColorSchema
    val DonateSuperTroops2: ColorSchema
    val DonateNormalTroops: ColorSchema
    val DonateSpells: ColorSchema
    val PreviousDonation: ColorSchema
    val RequestReinforcement: ColorSchema
    val CanNotRequestReinforcement: ColorSchema
    val TroopConfigCastleIcon: ColorSchema
    val ApproveJoinButton: ColorSchema
    val ClanInviteLanguageButton: ColorSchema
    val ClanInviteLanguageButtonAlt: ColorSchema
    val ClanInvitePageEntryIndicator: ColorSchema
    val ClanInvitePageEntryIndicatorAlt: ColorSchema
    val ClanInviteActionButton: ColorSchema
    val ClanInviteActionButtonAlt: ColorSchema
    val ClanInviteConfirmButton: ColorSchema
    val ClanInviteConfirmButtonAlt: ColorSchema
    val ClanInviteBackToListIndicator: ColorSchema
    val ClanInviteCloseButton: ColorSchema
    val ClanInviteCloseButtonAlt: ColorSchema
    val ClanInvitePlayerItemIndicator: ColorSchema
    val ClanInviteNotInClanFlag: ColorSchema
    val ClanInviteRefreshButton: ColorSchema
    val ClanInviteRefreshButtonAlt: ColorSchema
    val ClanKickButton: ColorSchema
    val ClanKickConfirmButton: ColorSchema
    val ClanKickPageIndicator: ColorSchema
}

object MainBaseClanColors : IMainBaseClanColors {
    // "I Understand" confirmation button when joining a clan
    override val IUnderstand = ColorSchema.parse(
        128, 456, 358, 532, "78F1D0", "2|-27|383B41,48|-27|383B41,94|-27|383B41,140|-27|383B41,-44|11|55E0AD,2|11|55E0AD,48|11|54DEAC,94|11|55E0AD,140|11|55E0AD", 0, 0.9, "加部落我了解"
    )

    // Button to join a clan
    override val JoinClanButton = ColorSchema.parse(
        962, 475, 1122, 511, "71EDC9", "32|0|71EDC9,111|0|71EDC9,96|0|71EDC9,128|0|71EDC9,0|18|2EC178,32|18|2EC178,64|18|2EC178,96|18|2EC178,128|18|2EC178", 0, 0.9, "加入部落按钮"
    )

    // Button to exit / leave a clan
    override val ExitClanButton = ColorSchema.parse(
        966, 475, 1121, 505, "615DF8", "31|0|615DF8,62|0|5653DA,93|0|615DF8,124|0|615DF8,0|15|0E0DCF,31|15|0E0DCF,88|6|5F5DF2,93|15|0E0DCF,124|15|0E0DCF", 0, 0.9,
    )

    // Apply clan settings confirmation button
    override val ApplyClanSetting = ColorSchema.parse(
        560, 519, 767, 574, "59D89F", "41|0|59D89F,83|0|59D89F,124|0|59D89F,165|0|59D89F,0|27|2CCD84,41|27|2CCD84,83|27|2CCD84,124|27|2CCD84,165|27|2CCD84", 0, 0.9, "应用部落设置"
    )

    // Search options indicator when a clan has been found
    override val SearchOptions = ColorSchema.parse(
        768, 200, 938, 231, "0B3763", "34|0|0B3763,68|0|7AF1D2,102|0|7AF1D2,136|0|7AF1D2,0|15|0B3F6B,34|15|0B3F6B,68|15|2CCD84,102|15|2CCD84,136|15|2CCD84", 0, 0.9, "设置选项，已搜索到部落"
    )

    // Flag indicating the player has not joined a clan
    override val NotJoinClanFlag = ColorSchema.parse(
        40, 302, 63, 318, "BFD9F2", "5|0|C7DFF3,10|0|D9EFF5,14|0|0D40E3,19|0|0D4BE6,0|8|BFD9F2,5|8|C7DFF3,10|8|D9EFF5,14|8|0D40E3,19|8|0D4BE7", 0, 0.9
    )

    // Clan chat icon for identifying chat window
    override val ClanChatIcon = ColorSchema.parse(
        39, 310, 66, 327, "FFFFFF", "5|0|FFFFFF,11|0|FFFFFF,16|0|FFFFFF,21|0|FFFFFF,0|9|FFFFFF,5|9|FFFFF9,11|9|E5E3DE,16|9|FFFFFF,21|9|FFFFFF", 0, 0.9, "部落聊天框"
    )

    // Donation button in clan chat
    override val DonationButton = ColorSchema.parse(
        409, 79, 430, 642, "69E9C1", "3|0|69E9C1,5|0|69E9C1,7|0|69E9C1,10|0|69E9C1,0|10|2CCD84,3|10|2CCD84,5|10|2CCD84,7|10|2CCD84,10|10|2CCD84", 0, 0.9, "增援按钮"
    )

    // Donate super troops tab
    override val DonateSuperTroops = ColorSchema.parse(
        500, 50, 1180, 690, "0E0D81", "-40|-10|0E0D81,-40|-8|0E0D81,-40|-5|0E0D81,-40|-3|0E0D81,35|-15|0E0D81,35|-13|0E0D81,35|-12|0E0D81,35|-10|0E0D81,35|-9|0E0D81", 1, 0.97, "捐超级兵"
    )

    // Donate super troops tab (variant 2)
    override val DonateSuperTroops2 = ColorSchema.parse(
        500, 50, 1180, 690, "FFFFFF", "0|5|B87940,0|-11|B87940,2|-11|B87940,5|-5|FFFFFF,5|-2|FFFFFF,5|4|B87940,4|5|B87940,3|5|B87940,2|5|B87940", 1, 0.97, "捐超级兵2"
    )

    // Donate normal troops tab
    override val DonateNormalTroops = ColorSchema.parse(
        500, 50, 1180, 690, "B87940", "-37|-6|B87940,-37|-8|B87940,-37|-10|B87940,-37|-14|B87940,38|-11|B87940,38|-13|B87940,38|-15|B87940,38|-17|B87940,37|-14|B87940", 1, 0.97, "捐普通兵"
    )

    // Donate spells tab
    override val DonateSpells = ColorSchema.parse(
        500, 50, 1180, 690, "C1476F", "-38|-5|C1476F,-38|-8|C1476F,-38|-10|C1476F,-38|-12|C1476F,36|-6|C1476F,36|-9|C1476F,36|-10|C1476F,36|-12|C1476F,36|-14|C1476F", 1, 0.97, "捐法术"
    )

    // Navigate to previous donation request
    override val PreviousDonation = ColorSchema.parse(
        417, 64, 479, 118, "FFFFFF", "-6|0|12CE97,-6|3|13C78D,-6|8|18A761,7|10|18A661,7|7|16B979,7|3|13C78D,7|2|13CA91,7|1|13CC94,7|0|12CE97", 0, 0.9, "上一个捐赠"
    )

    // Request reinforcements button in the clan chat window
    override val RequestReinforcement = ColorSchema.parse(
        285, 650, 470, 715, "3AD48B", "-27|-2|2D2D31,-8|-17|FFFFFF,-9|-33|ECECEC,2|-24|949494,14|-29|F5F5F5,15|-19|FFFFFF,-42|-24|97A4B1,14|-21|FFFFFF,12|-26|FDFDFD", 0, 0.9, "求援按钮"
    )
    override val CanNotRequestReinforcement = ColorSchema.parse(
        285, 650, 470, 715, "393939", "15|0|4E4E4E,30|0|F6F6F6,45|0|9F9F9F,60|0|FFFFFF,0|17|0E0E0E,15|17|292929,30|17|959595,45|17|ADADAD,60|17|ADADAD", 0, 0.9, "无法请求增援"
    )

    // Troop config castle icon for donation identification
    override val TroopConfigCastleIcon = ColorSchema.parse(
        0, 0, 0, 0, "4B596D", "3|0|6D7C8E,7|0|495568,11|0|485465,14|0|495565,0|11|282F40,3|11|1D2026,7|11|1F2023,11|11|232326,14|11|8E9BA6", 0, 0.9
    )

    // Green approve button on join request cards in clan chat
    override val ApproveJoinButton = ColorSchema.parse(
        5, 72, 482, 652, "2EBD76",
        "-1|-23|7DF3D8,-41|-21|78F0D3,-76|-23|7DF3D8,-76|2|30B66D,-211|2|1511DB,-203|-19|7571FF,-162|-23|807AFF,-130|-20|7573FF,-130|2|1511DB",
        0, 0.9, "批准加入按钮"
    )

    override val ClanInviteLanguageButton = ColorSchema.parse(
        549, 509, 771, 587, "7AE3B5", "45|0|7AE3B5,89|0|7AE3B5,133|0|7AE3B5,178|0|7AE3B5,0|39|2BCE86,45|39|2BCE86,89|39|2BCE86,133|39|2BCE86,178|39|2BCE86", 0, 0.9, "部落拉人-语言确认按钮"
    )
    override val ClanInviteLanguageButtonAlt = ColorSchema.parse(
        549, 509, 771, 587, "2BCE86", "45|0|2BCE86,89|0|2BCE86,133|0|2BCE86,178|0|2BCE86,0|39|7AE3B5,45|39|7AE3B5,89|39|7AE3B5,133|39|7AE3B5,178|39|7AE3B5", 0, 0.9, "部落拉人-语言确认按钮(备选)"
    )
    override val ClanInvitePageEntryIndicator = ColorSchema.parse(
        852, 197, 948, 236, "7DF3D8", "19|0|7DF3D8,38|0|7DF3D8,57|0|7DF3D8,76|0|7DF3D8,0|19|2ECA80,19|19|2ECA80,38|19|2ECA80,57|19|2ECA80,76|19|2ECA80", 0, 0.9, "部落拉人-进入页面标志"
    )
    override val ClanInvitePageEntryIndicatorAlt = ColorSchema.parse(
        852, 197, 948, 236, "2ECA80", "19|0|2ECA80,38|0|2ECA80,57|0|2ECA80,76|0|2ECA80,0|19|7DF3D8,19|19|7DF3D8,38|19|7DF3D8,57|19|7DF3D8,76|19|7DF3D8", 0, 0.9, "部落拉人-进入页面标志(备选)"
    )
    override val ClanInviteActionButton = ColorSchema.parse(
        696, 558, 860, 615, "7EF4D9", "33|0|7EF4D9,66|0|7EF4D9,99|0|7EF4D9,132|0|7EF4D9,0|29|2EC57B,33|29|2EC57B,66|29|2EC57B,99|29|2EC57B,132|29|2EC57B", 0, 0.9, "部落拉人-更多信息按钮"
    )
    override val ClanInviteActionButtonAlt = ColorSchema.parse(
        696, 558, 860, 615, "2EC57B", "33|0|2EC57B,66|0|2EC57B,99|0|2EC57B,132|0|2EC57B,0|29|7EF4D9,33|29|7EF4D9,66|29|7EF4D9,99|29|7EF4D9,132|29|7EF4D9", 0, 0.9, "部落拉人-更多信息按钮(备选)"
    )
    override val ClanInviteConfirmButton = ColorSchema.parse(
        394, 355, 532, 400, "83F6DB", "27|0|83F6DB,55|0|83F6DB,83|0|83F6DB,110|0|83F6DB,0|23|2EC67C,27|23|2EC67C,55|23|2EC67C,83|23|2EC67C,110|23|2EC67C", 0, 0.9, "部落拉人-邀请确认按钮"
    )
    override val ClanInviteConfirmButtonAlt = ColorSchema.parse(
        394, 355, 532, 400, "2EC67C", "27|0|2EC67C,55|0|2EC67C,83|0|2EC67C,110|0|2EC67C,0|23|83F6DB,27|23|83F6DB,55|23|83F6DB,83|23|83F6DB,110|23|83F6DB", 0, 0.9, "部落拉人-邀请确认按钮(备选)"
    )
    override val ClanInviteBackToListIndicator = ColorSchema.parse(
        143, 42, 209, 71, "84E5BA", "13|0|84E5BA,26|0|FFFFFF,39|0|FFFFFF,52|0|84E5BA,0|15|26C779,13|15|26C779,26|15|C2B7B1,39|15|9D9394,52|15|26C779", 0, 0.9, "部落拉人-返回成员列表标志"
    )
    override val ClanInviteCloseButton = ColorSchema.parse(
        600, 300, 700, 400, "FF5722", "5|0|FF5722,0|5|FF5722,5|5|FF5722", 0, 0.9, "部落拉人-关闭弹窗按钮"
    )
    override val ClanInviteCloseButtonAlt = ColorSchema.parse(
        600, 300, 700, 400, "9E9E9E", "5|0|9E9E9E,0|5|9E9E9E,5|5|9E9E9E", 0, 0.9, "部落拉人-关闭弹窗按钮(备选)"
    )
    override val ClanInvitePlayerItemIndicator = ColorSchema.parse(
        1068, 517, 1138, 685, "3DBDE6", "0|-6|6EE5EF,0|-16|7EE6EF,-6|2|29C4E7,7|-1|31C7E7,-3|10|21BEE7,-4|21|40D7EC,3|22|62D6EF,0|24|52B6D8,-2|5|2182B5", 0, 0.9, "部落拉人-玩家项标志"
    )
    override val ClanInviteNotInClanFlag = ColorSchema.parse(
        17, 404, 83, 473, "B6B5B7", "13|0|FEFEFE,26|0|878787,39|0|FFFFFF,52|0|606464,0|35|565F66,13|35|8C8C8C,26|35|4F4F4F,39|35|9B9B9B,52|35|272727", 0, 0.9, "未加入部落标志"
    )
    override val ClanInviteRefreshButton = ColorSchema.parse(
        566, 609, 749, 653, "7FF3D7",
        "37|0|7FF3D7,74|0|7FF3D7,110|0|7FF3D7,147|0|7FF3D7,0|22|2EC57C,37|22|2EC57C,74|22|2EC57C,110|22|2EC57C,147|22|2EC57C",
        0, 0.9, "部落拉人-刷新按钮"
    )
    override val ClanInviteRefreshButtonAlt = ColorSchema.parse(
        566, 609, 749, 653, "2EC57C",
        "37|0|2EC57C,74|0|2EC57C,110|0|2EC57C,147|0|2EC57C,0|22|7FF3D7,37|22|7FF3D7,74|22|7FF3D7,110|22|7FF3D7,147|22|7FF3D7",
        0, 0.9, "部落拉人-刷新按钮(备选)"
    )

    // Kick clan member button on member profile
    override val ClanKickButton = ColorSchema.parse(
        687, 632, 872, 699, "82F5DA",
        "37|0|82F5DA,74|0|82F5DA,111|0|82F5DA,148|0|82F5DA,0|33|2FBD74,37|33|2FBD74,74|33|2FBD74,111|33|2FBD74,148|33|2FBD74",
        0, 0.9, "踢出成员-踢出按钮"
    )

    // Confirm kick dialog button
    override val ClanKickConfirmButton = ColorSchema.parse(
        674, 285, 916, 394, "84F8DF",
        "49|0|84F8DE,97|0|84F8DE,145|0|84F8DE,194|0|84F8DE,0|55|1EBC6C,49|55|1FBB6C,97|55|1FBB6C,145|55|1FBB6C,194|55|1FBB6C",
        0, 0.9, "踢出成员-确认按钮"
    )

    // Page indicator for clan member list
    override val ClanKickPageIndicator = ColorSchema.parse(
        0, 0, 0, 0, "000000", "", 0, 0.9, "踢出成员-页面标识"
    )
}
