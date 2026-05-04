@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseShopColors {
    val Merchant: ColorSchema
    val GemInShop: ColorSchema
    val RaidMedalInShop: ColorSchema
    val ConfirmBuyWithRaidMedal: ColorSchema
    val GoldOre: ColorSchema
    val InsufficientMedal: ColorSchema
    val PurpleOre: ColorSchema
    val ClockTowerPotionInShop: ColorSchema
    val RingOfWall: ColorSchema
    val ResearchPotionInShop: ColorSchema
    val ResourcePotionInShop: ColorSchema
}

object MainBaseShopColors : IMainBaseShopColors {
    // Main Base Shop Colours
    override val Merchant = ColorSchema.parse(
        207, 486, 286, 583, "2B5B50", "16|0|58A2A9,32|0|6B88DF,47|0|5A7DD7,63|0|7993E3,0|49|193C43,16|49|235052,32|49|3A707B,47|49|163FA5,63|49|203170", 0, 0.9, "商店商人"
    )
    override val GemInShop = ColorSchema.parse(
        307, 644, 326, 662, "50DAA3", "4|0|78F4DA,8|0|7EF8E1,11|0|85FCE5,15|0|8DFFEC,0|9|8BFADF,4|9|92FDE3,8|9|88FBDD,11|9|77F7D3,15|9|8AFEE3", 0, 0.9, "商店内宝石"
    )
    override val RaidMedalInShop = ColorSchema.parse(
        307, 644, 326, 662, "A47253", "4|0|FFFFFF,8|0|FFF8EB,11|0|D3B99C,15|0|CDB292,0|9|DECCB2,4|9|E0CEB6,8|9|BB9677,11|9|C6A88A,15|9|B89576", 0, 0.9, "商店内突袭币"
    )
    override val ConfirmBuyWithRaidMedal = ColorSchema.parse(
        582, 407, 756, 509, "84E4B9", "32|1|84E4B9,51|9|84E4B9,60|23|84E4B9,54|73|2CCD84,15|85|24C06E,7|38|F1E5D4,15|33|CDB294,25|37|A7785B,11|43|C9AC8E", 0, 0.9, "确认突袭币购买"
    )
    override val GoldOre = ColorSchema.parse(
        350, 10, 1120, 685, "0DF0FF", "-4|0|0DEBFA,-12|0|13D1F4,9|-15|8DD5F9,17|-4|0DAAFD,22|4|27C9F9,12|8|0DD6FD,5|15|0DBFFF,2|20|0DB0FB,4|25|337EFE", 0, 0.9, "黄矿"
    )
    override val InsufficientMedal = ColorSchema.parse(
        0, 0, 0, 0, "7F88FF", "0|2|7F88FF,0|4|7F88FF,-1|2|7F88FF,-1|1|7F88FF,-1|0|7F88FF,-1|-1|7F88FF,-1|-2|7F88FF,0|-3|7F88FF,1|-3|7F88FF", 0, 0.98
    )
    override val PurpleOre = ColorSchema.parse(
        350, 10, 1120, 685, "EC7DC2", "11|13|5B27A1,0|29|CD45A8,-7|9|D5A6FA,1|-3|DA5CA5,17|4|FFCFFF,25|7|F17DE1,23|18|C852BB,6|29|D03BCB,30|8|D668C8", 0, 0.9, "商店紫矿"
    )
    override val ClockTowerPotionInShop = ColorSchema.parse(
        350, 10, 1120, 685, "F6BAD8", "6|12|F2AFD3,-17|6|FFDCE7,-6|-9|FBD2EA,11|-9|D3FFFF,21|-4|36507B,29|3|7EDCF9,1|18|F6BAD9,-3|-52|94CFFF,5|-44|99D6FF", 0, 0.9, "商店内钟楼药水"
    )
    override val RingOfWall = ColorSchema.parse(
        350, 10, 1120, 685, "354C74", "-22|0|3F323B,-42|0|7DB8D0,-53|-12|86BDE0,-46|-24|5891C8,-34|-28|4D82B9,-20|-28|4D7EB0,-5|-27|7C4E52,3|-19|6B5B70,20|0|4779AC", 0, 0.9, "商店内壁垒之戒"
    )
    override val ResearchPotionInShop = ColorSchema.parse(
        350, 10, 1120, 685, "440F48", "-16|-3|660D89,-20|-24|730DA1,-2|-22|62216E,15|-24|8ABFCB,25|-23|6FBAE5,14|-11|6D5953,9|-9|5E4245,-5|-64|94D0FF,2|-66|90C9F9", 0, 0.9, "商店内研究药水"
    )
    override val ResourcePotionInShop = ColorSchema.parse(
        350, 10, 1120, 685, "F02FD0", "-15|6|FA5BE5,-16|-12|FC60E8,-4|-16|F9DDEE,10|-15|835C54,23|-10|97FFFF,25|-6|79C7EE,0|-35|FCC0EE,-4|-54|94CFFF,12|-56|99D6FF", 0, 0.9, "商店内资源药水"
    )
}
