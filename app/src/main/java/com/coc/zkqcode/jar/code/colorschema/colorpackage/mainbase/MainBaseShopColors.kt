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
    val ShopButton: ColorSchema
    val ShopButton2: ColorSchema
    val OldVersion: ColorSchema
    val LeaguePageButton: ColorSchema
    val LeaguePageButton2: ColorSchema
    val ClanLeagueConfirmBuy: ColorSchema
    val LeagueShopResearchPotion1: ColorSchema
    val LeagueShopResearchPotion2: ColorSchema
    val LeagueShopBuilderPotion1: ColorSchema
    val LeagueShopBuilderPotion2: ColorSchema
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
    override val ShopButton = ColorSchema.parse(
        1138, 605, 1262, 697, "1B87C6", "13|0|2196DE,26|0|41669B,38|0|2EAFF9,51|0|4FD1FC,0|15|1DA8EF,13|15|335381,26|15|1C6494,38|15|50D4FF,51|15|3DD2FF", 0, 0.9, "商店按钮"
    )
    override val ShopButton2 = ColorSchema.parse(
        1166, 606, 1231, 655, "25B9FF", "13|0|22B6FD,26|0|2C3031,39|0|354E68,52|0|C6C9BF,0|25|3ACDFE,13|25|334F78,26|25|25CBD3,39|25|3ED0FF,52|25|36D0FF", 0, 0.9, "商店按钮2"
    )
    override val OldVersion = ColorSchema.parse(
        55, 6, 171, 54, "9FFCFF", "0|6|26A5F3,9|8|43D1F5,16|-6|EEF4E6,32|-7|DDFAF6,40|-7|DDFAF6,45|-8|DFFCF7,56|-7|DDFAF6,45|19|7AC6AF,55|20|7BC5B0", 0, 0.9, "切换旧版商店"
    )
    override val LeaguePageButton = ColorSchema.parse(
        46, 22, 1166, 109, "8FF5FB", "5|0|8DF5FB,11|0|87F4FB,16|0|85E1F4,21|0|A6F5FA,0|13|1176AF,5|13|021920,11|13|02161B,16|13|091E22,21|13|5DE2F8", 0, 0.9, "联赛页面按钮"
    )
    override val LeaguePageButton2 = ColorSchema.parse(
        127, 50, 1121, 146, "A9F8FC", "5|0|A8F8FD,11|0|A3F8FD,17|0|9BFAFE,22|0|B5F7FB,0|17|1588BD,5|17|021E25,11|17|031F25,17|17|051F25,22|17|41C1E4", 0, 0.9, "联赛页面按钮2"
    )
    override val ClanLeagueConfirmBuy = ColorSchema.parse(
        502, 387, 791, 526, "ABFFFF", "5|0|ACFFFF,11|0|A6FFFF,17|0|A0FEFF,22|0|9EE2EB,0|13|217AA0,5|13|51C8E6,11|13|53CEEC,17|13|45C7E7,22|13|41C5E5", 0, 0.9, "联赛商店确认购买"
    )
    override val LeagueShopResearchPotion1 = ColorSchema.parse(
        28, 258, 1267, 637, "500D5A", "-20|-8|760DA3,-20|-21|6F0D95,-11|-27|610D7A,10|-28|636E69,23|-24|715A55,34|-15|3C577B,21|8|43103E,6|-82|94CFFF,5|-83|94CFFF", 0, 0.9, "国际服联赛研究药水"
    )
    override val LeagueShopResearchPotion2 = ColorSchema.parse(
        28, 258, 1267, 637, "510D5B", "-8|0|660D87,-14|-15|730D9C,-8|-23|79129C,4|-19|755878,10|-19|541062,20|-9|604E49,32|-3|8A8D96,6|-55|94CFFF,-2|-59|8BC2F0", 0, 0.9, "国服联赛研究药水"
    )
    override val LeagueShopBuilderPotion1 = ColorSchema.parse(
        28, 258, 1267, 637, "D7B04B", "-6|19|D2A542,-24|6|F0C24D,-33|-16|F1C651,-23|-21|E7BF57,27|-12|7DD7F6,31|-17|7DD7F6,-9|-69|93CEFF,3|-80|9AD7FF,-7|-61|98D5FF", 0, 0.9, "国际服联赛工人药水"
    )
    override val LeagueShopBuilderPotion2 = ColorSchema.parse(
        28, 258, 1267, 637, "E4C25F", "7|14|CCA438,-9|13|F1C54E,-3|-2|E6C056,8|-7|F2E0BB,28|-5|7DD7F6,31|2|7DD6F5,9|-29|374855,3|-43|8CC1F0,10|-45|93CEFF", 0, 0.9, "国服联赛工人药水"
    )
}
