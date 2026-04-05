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
    val DonateNormalTroops: ColorSchema
    val DonateSpells: ColorSchema
    val PreviousDonation: ColorSchema
    val RequestReinforcement: ColorSchema
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
        284, 650, 482, 714, "3AD48B", "-27|-2|2D2D31,-8|-17|FFFFFF,-9|-33|ECECEC,2|-24|949494,14|-29|F5F5F5,15|-19|FFFFFF,-42|-24|97A4B1,14|-21|FFFFFF,12|-26|FDFDFD", 0, 0.9, "求援按钮"
    )
}
