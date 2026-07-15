@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IUIColors {
    val Reconnection: ColorSchema
    val ReconnectionOnCloudPhone: ColorSchema
    val ReconnectionOnOppoA57: ColorSchema
    val ReconnectionOnLeiDian: ColorSchema
    val ReconnectionOnSamsung: ColorSchema
    val RatingOnCloudPhone: ColorSchema
    val RatingOnCloudPhone2: ColorSchema
    val GreenConfirm: ColorSchema
    val CNAd: ColorSchema
    val ClanChat: ColorSchema
    val CNProsperity: ColorSchema
    val CNPuppetAd: ColorSchema
    val Achievement: ColorSchema
    val ClaimAchievement: ColorSchema
    val ReturnAwards: ColorSchema
    val BuilderBackToCamp: ColorSchema
    val MainBackToCamp: ColorSchema
    val CNBackFromAwards: ColorSchema
    val CollectChest: ColorSchema
    val EditModeWrench: ColorSchema
    val CancelEditMode: ColorSchema
    val GoldenPass: ColorSchema
    val OldShopButton: ColorSchema
    val NewShopButton: ColorSchema
    val MagicalItem: ColorSchema
    val ArrowPointingDown: ColorSchema
    val DailyLoginReward: ColorSchema
    val BuilderBaseStarBonus: ColorSchema
    val SmallGemIcon: ColorSchema
    val PassSkinAd: ColorSchema
    val PassAwardAd: ColorSchema
    val ReturnTask: ColorSchema
    val ReturnTask2: ColorSchema
    val ReturnTask3: ColorSchema
    val MiddleGreenButton: ColorSchema
    val ExclusiveGift: ColorSchema
    val MorePointCoupon: ColorSchema
    val TencentChildProtection: ColorSchema
    val CollectElixirCartTutorial: ColorSchema
    val BackToCamp: ColorSchema
    val AgreeTerms: ColorSchema
    val AgreePrivacy: ColorSchema
    val LogIn: ColorSchema
    val SkinAd: ColorSchema
    val CapitalBackToCamp: ColorSchema
    val CapitalBackToCamp2: ColorSchema
    val CapitalAirShip: ColorSchema
    val CapitalAirShip2: ColorSchema
    val CloudPhoneShopButton: ColorSchema
    val TempGameContinue: ColorSchema
    val ReturnTask4: ColorSchema
    val ClanLeagueReturnCamp: ColorSchema
    val UpgradeNotification: ColorSchema
    val GameSettings: ColorSchema

    // Clan war UI indicators
    val ClanWarTutorialArrow: ColorSchema
    val TopRightClanWarInfo: ColorSchema
}

object UIColors : IUIColors {
    override val Reconnection = ColorSchema.parse(
        240, 195, 1030, 530, "1B1B1C", "106|-4|1B1B1C,184|8|1B1B1C,142|-121|1B1B1C,236|-139|1B1B1C,302|-142|1B1B1C,442|-13|1B1B1C,541|-48|1B1B1C,539|-91|1B1B1C,523|-135|1B1B1C", 0, 0.99, "重连1"
    )
    override val ReconnectionOnCloudPhone = ColorSchema.parse(
        240, 195, 1030, 530, "424242", "106|-4|424242,184|8|424242,142|-121|424242,236|-139|424242,302|-142|424242,442|-13|424242,541|-48|424242,539|-91|424242,523|-135|424242", 0, 0.99, "重连1"
    )
    override val ReconnectionOnOppoA57 = ColorSchema.parse(
        254, 137, 1020, 576, "1D1C19", "95|0|1D1C19,190|0|1D1C19,284|0|1D1C19,379|0|1D1C19,0|24|1D1C19,95|24|1D1C19,190|24|1D1C19,284|24|1D1C19,379|24|1D1C19", 0, 0.99, "OPPO A57 断线重连"
    )
    override val ReconnectionOnLeiDian = ColorSchema.parse(
        260, 142, 1001, 581, "201B1C", "15|8|201B1C,98|16|201B1C,176|19|201B1C,238|19|201B1C,290|17|201B1C,270|45|201B1C,170|51|201B1C,98|55|201B1C,62|56|201B1C", 0, 0.99, "雷电模拟器断线重连"
    )
    override val ReconnectionOnSamsung = ColorSchema.parse(
        20, 338, 1172, 691, "1F1F1F", "51|-7|1F1F1F,137|-6|1F1F1F,205|-7|1F1F1F,291|-8|1F1F1F,319|-8|1F1F1F,387|-5|1F1F1F,429|-5|1F1F1F,514|-5|1F1F1F,636|-7|1F1F1F", 0, 0.99, "三星断线重连"
    )
    override val RatingOnCloudPhone = ColorSchema.parse(
        249, 206, 1035, 522, "424242", "157|0|424242,314|0|424242,471|0|424242,628|0|424242,0|158|424242,157|158|424242,314|158|424242,471|158|424242,628|158|424242", 0, 0.99, "云手机打分提示"
    )
    override val RatingOnCloudPhone2 = ColorSchema.parse(
        264, 215, 1014, 506, "424242", "150|0|424242,300|0|424242,450|0|424242,600|0|424242,0|145|424242,150|145|424242,300|145|424242,450|145|424242,600|145|424242", 0, 0.99, "云手机打分提示2"
    )
    override val GreenConfirm = ColorSchema.parse(
        577, 564, 731, 642, "7AF1D2", "25|-3|7FF3D7,45|-4|81F4D9,60|-4|81F4D9,66|5|70ECC8,67|17|53DFAB,67|23|2CCD84,45|35|30BA71,22|33|2FBF76,-1|26|2CCC83", 0, 0.9, "绿色确认"
    )
    override val CNAd = ColorSchema.parse(
        1074, 44, 1140, 110, "FFFFFF", "5|9|FFFFFF,-17|18|F1F1F1,-15|-6|F8F9FA,-6|-18|F9FAF9,8|-18|FAFAF9,15|-17|F9FAF9,19|-8|F9FAFA,18|0|F5F5F5,15|7|E5EAEF", 0, 0.9, "国服广告"
    )
    override val ClanChat = ColorSchema.parse(
        485, 301, 562, 421, "28AAF3", "5|17|28AAF3,6|35|3B8AEA,3|54|3B8AEA,-15|56|3B8AEA,-23|46|3B8AEA,-23|40|3B8AEA,-24|31|3B8AEA,-22|14|28AAF3,-19|7|28AAF3", 0, 0.9, "部落聊天框"
    )
    override val CNProsperity = ColorSchema.parse(
        596, 379, 661, 491, "28AAF3", "1|16|28AAF3,0|33|3B8AEA,-15|49|3B8AEA,-24|40|3B8AEA,-27|26|3B8AEA,-31|13|28AAF3,-34|1|28AAF3,-27|-12|28AAF3,-13|-8|28AAF3", 0, 0.9, "繁荣度聊天框"
    )
    override val CNPuppetAd = ColorSchema.parse(
        1157, 84, 1206, 131, "6EB1F9", "-9|-3|173182,-1|-11|122C6E,5|-6|19317F,9|-3|17307F,5|3|638CDE,0|5|2136A1,-2|6|2337A6,-8|-2|193085,-11|-3|173181", 0, 0.9, "垃圾皮影广告"
    )
    override val Achievement = ColorSchema.parse(
        59, 3, 92, 39, "2915E2", "-3|0|2815E4,-15|0|2915E1,-11|-19|1818FF,-9|-19|1818FF,-7|-19|1818FF,-5|-19|1818FF,-4|-19|1818FF,-2|-19|1818FF,-1|-19|1818FF", 0, 0.9, "成就红色"
    )
    override val ClaimAchievement = ColorSchema.parse(
        932, 177, 1128, 666, "70EDC9", "22|0|70EDC9,26|-7|83F5DB,89|-3|7AF1D2,105|-2|77F0CF,120|-2|77F0CF,141|13|2CCC82,130|16|2EC47B,23|15|2DC77E,5|15|2DC77E", 0, 0.9, "领取成就绿色按钮"
    )

    override val ReturnAwards = ColorSchema.parse(
        110, 44, 1173, 679, "00528D", "116|-2|00518B,-68|491|00CCF3,-78|517|00DCFA,-78|523|00DDFA,801|536|00DDFA,840|529|009BD8,846|515|008AED,840|399|00437A,839|354|004279", 0, 0.9, "回归奖励"
    )

    override val BuilderBackToCamp = ColorSchema.parse(
        553, 574, 728, 648, "8BEABD", "18|-1|8CEABE,46|-1|8CEABE,68|0|8BEABD,80|2|88E9BB,89|35|3AD48B,62|38|3AD38B,35|32|3AD48B,22|34|3AD48B,-16|29|3AD48B", 0, 0.9, "夜世界回营"
    )

    override val MainBackToCamp = ColorSchema.parse(
        554, 578, 726, 658, "79F6D9", "40|-5|83F8DD,69|-7|86F8DF,88|8|69F2D0,97|11|64EFCB,96|33|1EBB6C,79|37|1FBB6C,39|35|1FBB6C,18|35|1FBB6C,0|32|1FBC6D", 0, 0.9, "主世界回营"
    )

    override val CNBackFromAwards = ColorSchema.parse(
        62, 74, 97, 122, "37E0FA", "0|2|39E2F7,-2|3|3AE1F9,-5|13|0DA2D1,-2|16|0DA1D0,-1|18|10A3CF,2|12|0D8CBC,2|8|3AE0F8,5|8|0EA3CF,5|12|066E9E", 0, 0.9, "垃圾奖励"
    )

    override val CollectChest = ColorSchema.parse(
        547, 569, 735, 627, "88E9BB", "39|-2|8BEABD,83|-7|8EEABF,129|-5|8EEABF,130|18|3AD48B,124|26|3AD48B,107|32|39D38A,47|34|39D088,19|30|3AD48B,6|25|3AD48B", 0, 0.9, "领取宝箱"
    )
    override val EditModeWrench = ColorSchema.parse(
        1197, 285, 1257, 345, "FFFFFF", "-13|-15|ECFCF8,-1|-20|EFFDFB,4|-21|EFFDFB,13|-21|EFFDFB,18|-17|EDFDF9,22|-10|E9FAF7,6|2|FFFFFF,8|6|FFFFFF,27|5|BDDDDB", 0, 0.9, "编辑模式扳手"
    )
    override val CancelEditMode = ColorSchema.parse(
        1051, 484, 1269, 537, "FFFFFF", "3|6|FFFEF9,6|-2|FFFFFF,20|-5|7B76FF,18|-1|726DFE,21|2|6B66FD,21|15|1712E7,20|18|1611E1,21|19|1611DF,25|21|1511D9", 0, 0.9, "取消编辑模式"
    )
    override val GoldenPass = ColorSchema.parse(
        1118, 84, 1157, 124, "8381FF", "8|0|FFFFFF,16|0|111115,23|0|FFFFFF,31|0|8381FF,0|20|221EF7,8|20|FBFBFB,16|20|181715,23|20|FBFBFB,31|20|221EF7", 0, 0.9, "黄金令牌"
    )
    override val OldShopButton = ColorSchema.parse(
        1195, 19, 1261, 88, "FFFFFF", "-4|2|FAF6F6,-23|8|1A16ED,-20|-6|867FFF,-18|-15|958FFF,4|-20|958DFF,21|-7|8A82FF,14|4|1511EC,12|21|1913D3,-14|26|352DCE", 0, 0.9, "旧版商店叉叉"
    )
    override val NewShopButton = ColorSchema.parse(
        1159, 8, 1209, 57, "FAF6F6", "-3|-5|FFFFFF,-10|-5|857DFF,-5|-15|958DFF,14|-10|938BFF,13|-3|7D76FF,14|8|1612E6,3|12|1A15D1,-4|13|211BCE,-10|13|221CCF", 0, 0.9, "新版商店叉叉"
    )
    override val MagicalItem = ColorSchema.parse(
        960, 57, 1016, 110, "FBFBFB", "-14|-4|716FFE,-6|-14|8785FF,13|10|221EF2,12|-13|8785FF,13|-10|8583FF,13|-3|7A78FF,13|5|221EF7,10|17|1B18AA,3|14|221EF7", 0, 0.9, "魔法物品"
    )
    override val ArrowPointingDown = ColorSchema.parse(
        116, 92, 1124, 620, "26ACFF", "7|0|24B9FF,14|0|24BCFE,21|0|25B6FE,28|0|26A7FE,0|21|0FADFD,7|21|0AB0FC,14|21|0AB2FC,21|21|0AAEFB,28|21|10ADFE", 0, 0.92, "向下箭头"
    )
    override val DailyLoginReward = ColorSchema.parse(
        1032, 153, 1050, 172, "FBFCFD", "3|0|F3F5FA,16|6|E8E9ED,11|0|FFFFFF,14|0|B9C8DD,0|9|F2F1EF,3|9|ECF1F6,0|4|F5F6F7,11|9|FAFBFD,14|9|E9EDF1", 0, 0.9, "疼讯签到奖励"
    )
    override val BuilderBaseStarBonus = ColorSchema.parse(
        541, 522, 739, 606, "82E8B8", "39|0|82E8B8,79|0|82E8B8,119|0|82E8B8,158|0|82E8B8,0|42|3AD38A,39|42|3AD38A,79|42|3AD38A,119|42|3AD38A,158|42|3AD38A", 0, 0.9, "夜世界胜利之星"
    )

    // Small gem icon used across popup and shop UIs
    override val SmallGemIcon = ColorSchema.parse(
        0, 0, 0, 0, "86FBE2", "0|2|91FEE6,-2|3|91FDE2,-4|1|7DF6DA,-4|-3|41C583,0|-3|4DDDA9,4|-2|8AFFED,4|1|79FCDF,3|4|74FAD4,1|5|73F8CD", 0, 0.9
    )

    // Golden pass skin advertisement entry
    override val PassSkinAd = ColorSchema.parse(
        1164, 67, 1209, 116, "8C85FF", "9|0|FFFFFF,18|0|353535,27|0|FFFFFF,36|0|8D85FF,0|25|0D0D1B,9|25|FAF7F7,18|25|1310B2,27|25|CBCACA,36|25|1210EA", 0, 0.9, "令牌皮肤广告"
    )

    // Golden pass reward advertisement page marker
    override val PassAwardAd = ColorSchema.parse(
        1213, 34, 1250, 70, "8E86FF", "7|0|FFFFFF,15|0|212121,22|0|FFFFFF,29|0|8E86FF,0|18|1511EC,7|18|FAF6F6,15|18|0D0D0D,22|18|F8F8F8,29|18|1511EC", 0, 0.9, "令牌奖励页面"
    )

    // Return task panel marker on the event page
    override val ReturnTask = ColorSchema.parse(
        0, 490, 1275, 650, "CD8C4A", "4|0|CE8C4B,10|2|C68A48,16|4|C08746,24|-2|D59351,1148|-76|FFFFFF,1156|-74|FFFFFF,1148|-58|FFFFFF,1149|-61|FFFFFF,1152|-65|FFFFFF", 0, 0.9, "回归任务"
    )

    // Return task panel marker on the event page (variant 2)
    override val ReturnTask2 = ColorSchema.parse(
        6, 489, 1278, 692, "D08E4D", "2|5|C48746,9|3|CE8D4C,13|-1|DC9652,17|5|C78C4C,1219|-59|FFFFFF,1224|-68|FFFFFF,1214|-70|FFFFFF,1221|-69|FFFFFF,1231|-60|FFFFFF", 0, 0.9, "回归任务"
    )

    // Return task panel marker on the event page (variant 3)
    override val ReturnTask3 = ColorSchema.parse(
        37, 496, 1267, 633, "DB9653", "3|4|CE8F50,-7|7|C3884A,-7|0|DB9652,-4|8|C18848,1173|-72|FFFFFF,1177|-67|FFFFFF,1176|-61|FFFFFF,1182|-53|FFFFFF,1186|-56|FFFFFF", 0, 0.9, "回归任务"
    )

    // Middle green confirmation button (e.g. events and helper tutorials)
    override val MiddleGreenButton = ColorSchema.parse(
        577, 526, 704, 567, "86E8BA", "25|0|86E8BA,51|0|86E8BA,76|0|86E8BA,101|0|86E8BA,0|21|3AD48B,25|21|3AD48B,51|21|3AD48B,76|21|3AD48B,101|21|3AD48B", 0, 0.9, "中心绿色确认（类似活动和帮手教程）"
    )

    // Exclusive gift/offer popup indicator
    override val ExclusiveGift = ColorSchema.parse(
        1080, 113, 1119, 153, "8A83FF", "8|0|FFFFFF,16|0|FFFFFF,23|0|FFFFFF,31|0|8A83FF,12|8|FFFFFF,32|13|1611EC,13|30|2D25CD,-5|18|1A16ED,31|20|1511E8", 0, 0.9, "垃圾专属礼包"
    )

    // More coupons/points button
    override val MorePointCoupon = ColorSchema.parse(
        880, 195, 925, 240, "8B83FF", "9|0|FFFFFF,19|0|FFFFFF,18|8|FFFFFF,34|11|716BFD,42|15|2621F0,38|23|1712EB,19|23|0D0D0D,28|23|FAF6F6,37|23|1611EA", 0, 0.9, "更多点券"
    )
    override val TencentChildProtection = ColorSchema.parse(
        344, 66, 928, 235, "8ED139", "117|0|85CA2E,234|0|78C11F,351|0|7BC520,468|0|9FDF38,0|85|8BCF35,117|85|7FC627,234|85|75C01A,351|85|75C013,468|85|89CF14", 0, 0.9, "未成年守护"
    )
    override val CollectElixirCartTutorial = ColorSchema.parse(
        618, 461, 658, 542, "02A5FE", "8|0|00A8FD,16|0|00A9FD,24|0|00A9FD,32|0|00A5FD,0|41|22AFFF,8|41|21B8FF,16|41|21BDFF,24|41|21BBFF,32|41|22B2FF", 0, 0.9, "领圣水车教程"
    )

    // Back to camp button (generic)
    override val BackToCamp = ColorSchema.parse(
        58, 597, 102, 644, "10135B", "9|0|80A3EA,18|0|6682B6,27|0|7FA2E6,36|0|293265,0|23|2540A8,9|23|718BFD,18|23|7996FE,27|23|86A6FE,36|23|637AAE", 0, 0.9, "回营"
    )

    // Agree terms checkbox
    override val AgreeTerms = ColorSchema.parse(
        260, 470, 299, 509, "ACBAC2", "8|0|ACBAC2,16|0|ACBAC2,23|0|ACBAC2,31|0|ACBAC2,0|19|ACBAC2,8|19|ACBAC2,16|19|ACBAC2,23|19|ACBAC2,31|19|ACBAC2", 0, 0.97, "同意条款"
    )

    // Agree privacy button
    override val AgreePrivacy = ColorSchema.parse(
        184, 598, 593, 653, "67E2BF", "82|0|67E2BF,208|5|5CDCB3,245|0|67E2BF,327|0|67E2BF,0|27|21B76E,82|27|21B76E,164|27|21B76E,245|27|21B76E,327|27|21B76E", 0, 0.9, "同意隐私"
    )

    // Log in button
    override val LogIn = ColorSchema.parse(
        663, 406, 879, 451, "FFFFFF", "-4|-8|261CE8,-1|-28|000000,27|-23|EDCD62,57|-16|E4C357,60|7|A77019,164|-23|EDCD62,182|-17|E6C459,178|4|B3791C,155|2|BB7E1E", 0, 0.9, "登录"
    )

    // Skin advertisement popup
    override val SkinAd = ColorSchema.parse(
        1167, 39, 1203, 77, "6761B8", "7|0|FFFFFF,14|0|FFFFFF,21|0|FFFFFF,28|0|8C84FF,0|19|3D3D3D,7|19|F9F7F7,14|19|0D0D0D,21|19|F9F9F9,28|19|1511EB", 0, 0.9, "皮肤广告"
    )

    // Capital back to camp button
    override val CapitalBackToCamp = ColorSchema.parse(
        32, 589, 134, 686, "C2D7DC", "21|0|ADDEFF,41|0|86A9F6,61|0|53537C,82|0|C2D7DC,0|49|A9C5CC,21|49|A3BFC9,41|49|B0B7B9,61|49|A3BEC4,82|49|A9C5CC", 0, 0.9, "都城回营"
    )

    // Capital back to camp button (variant 2)
    override val CapitalBackToCamp2 = ColorSchema.parse(
        60, 598, 99, 649, "5162B5", "8|0|5E6FB4,16|0|6D80C5,23|0|83A8F4,31|0|4E483A,0|25|2648BE,8|25|6786E2,16|25|6987D5,23|25|5C6DB7,31|25|6F7AB2", 0, 0.9, "都城回营2"
    )

    // Capital airship button
    override val CapitalAirShip = ColorSchema.parse(
        64, 589, 124, 613, "385CBF", "12|0|365975,24|0|2A4567,36|0|355DAA,48|0|3860C3,0|12|2A396D,12|12|242A5B,24|12|2E4F5F,36|12|39618D,48|12|2F388E", 0, 0.9, "都城飞艇"
    )

    // Capital airship button (variant 2)
    override val CapitalAirShip2 = ColorSchema.parse(
        60, 573, 113, 596, "4475AE", "11|0|2B4760,22|0|38557D,32|0|265AA9,43|0|3A61C5,0|11|325794,11|11|192F31,22|11|213636,32|11|305585,43|11|2E408F", 0, 0.9, "都城飞船2"
    )

    // Cloud phone shop button
    override val CloudPhoneShopButton = ColorSchema.parse(
        1231, 34, 1253, 72, "FFFFFF", "5|0|FFFFFF,9|0|101013,13|0|8C85FF,18|0|8B84FF,0|19|F6F6F6,5|19|FAF6F6,9|19|101010,13|19|1511EC,18|19|1C18ED", 0, 0.9, "云手机商店"
    )
    override val TempGameContinue = ColorSchema.parse(
        574, 626, 707, 676, "88E9BB", "27|0|88E9BB,54|0|88E9BB,80|0|88E9BC,107|0|88E9BC,0|25|3AD48B,27|25|3AD48B,54|25|3AD48B,80|25|3AD48B,107|25|3AD48B", 0, 0.9, "临时活动继续"
    )

    // Return task panel marker (variant 4)
    override val ReturnTask4 = ColorSchema.parse(
        86, 587, 330, 626, "CD8C4B", "5|4|BF8545,12|1|C98D4C,16|-7|E29B56,22|1|CB8F4E,38|2|FFFFFF,40|3|FFFFFF,38|0|FFFFFF,38|-9|E49C58,36|-8|E39B57", 0, 0.98, "回归任务4"
    )

    // Clan league return to camp button
    override val ClanLeagueReturnCamp = ColorSchema.parse(
        60, 606, 102, 652, "AAE3FF", "9|0|7D98E6,17|0|364C9C,25|0|7DA0E8,34|0|34456A,0|23|2646C3,9|23|758FFF,17|23|7A9AFF,25|23|92BAFF,34|23|6576B1", 0, 0.9, "联赛回营"
    )

    // Upgrade notification popup
    override val UpgradeNotification = ColorSchema.parse(
        414, 478, 584, 545, "69C3FE", "34|0|69C3FE,68|0|69C3FE,102|0|69C3FE,136|0|69C3FE,0|33|2554DF,34|33|2554DF,68|33|2554DF,102|33|2554DF,136|33|2554DF", 0, 0.9, "升级通知"
    )

    // Game settings button
    override val GameSettings = ColorSchema.parse(
        1167, 40, 1252, 390, "FFFFFF", "-12|4|E8F9F7,-19|-5|FFFFFF,-13|-15|FFFFFF,9|5|E4F7F5,21|11|E4F7F5,22|2|FFFFFF,2|15|E3F5F5,-4|18|E4F7F5,1|24|F0FEFC", 0, 0.9, "游戏设置"
    )

    // Clan war tutorial arrow indicator
    override val ClanWarTutorialArrow = ColorSchema.parse(
        1143, 310, 1226, 416, "4D5057", "31|5|FFFFFF,37|14|FFFFFF,56|-9|4C4E55,60|-6|4C4F56,65|15|50535A,66|24|36383D,9|31|3A3D41,6|36|3F4145,6|44|41444A", 0, 0.9, "部落战教程箭头"
    )

    // Top right clan war info indicator
    override val TopRightClanWarInfo = ColorSchema.parse(
        1216, 22, 1252, 57, "FED398", "7|0|FEE5B3,14|0|FFFFFF,21|0|FFE6B8,28|0|FCCA8E,0|17|E38B39,7|17|E79D4B,14|17|FFFFFF,21|17|E49C59,28|17|995A11", 0, 0.9, "右上角部落战信息"
    )
}
