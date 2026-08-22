@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.clancapital

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IClanCapitalColors {
    val CapitalBarbarian: ColorSchema
    val CapitalBarbarian2: ColorSchema
    val CapitalCoin: ColorSchema
    val CapitalCoin2: ColorSchema
    val ZeroAttackTimes: ColorSchema
    val RaidInformation: ColorSchema
    val RaidMap: ColorSchema
    val RaidRedAttackButton: ColorSchema
    val NextRaid: ColorSchema
    val CapitalMiner: ColorSchema
    val CapitalTrainTroopPage: ColorSchema
    val CapitalTrainTroopPage2: ColorSchema
    val StartRaid: ColorSchema
    val CapitalDistrictLocked: ColorSchema
    val CollectCapitalCoins: ColorSchema
    val GemForCapital: ColorSchema
    val MiniPurpleCoinIcon: ColorSchema
    val MiniPurpleCoinIcon2: ColorSchema
    val SufficientCapitalCoin: ColorSchema
}

object ClanCapitalColors : IClanCapitalColors {
    // Capital barbarian troop icon
    override val CapitalBarbarian = ColorSchema.parse(
        176, 424, 265, 517, "7CBAFE", "18|0|73B6EF,36|0|A097A5,53|0|0D14A4,71|0|6C87EC,0|47|3C3E61,18|47|818EC5,36|47|587FC0,53|47|6D9CEE,71|47|6B77A0", 0, 0.9, "都城野蛮人"
    )

    // Capital barbarian troop icon (variant 2)
    override val CapitalBarbarian2 = ColorSchema.parse(
        176, 424, 265, 517, "6DB3ED", "9|0|B0B2F5,19|0|FEE5E8,29|0|343F80,38|0|0D2493,0|29|92DDFF,9|29|46ADF3,19|29|0E1021,29|29|0F101D,38|29|283757", 0, 0.9, "都城野蛮人2"
    )

    // Capital coin resource indicator
    override val CapitalCoin = ColorSchema.parse(
        1190, 18, 1264, 72, "E418BB", "-5|0|FF55F0,1|0|E418BB,5|0|D42FA6,3|-2|FB93DA,-9|-10|6CFFFF,-5|-9|68F4FF,3|-8|1BBDFA,-9|13|C53497,-20|8|0DD9FF", 0, 0.9, "都城币"
    )

    // Capital coin resource indicator (variant 2)
    override val CapitalCoin2 = ColorSchema.parse(
        1212, 17, 1268, 72, "FF77EB", "4|0|E711C4,8|0|DE21B6,10|10|DB19B2,4|4|E019B7,0|14|CA2D99,-2|4|EE1ED8,8|13|DB11B2,8|16|CA1196,12|6|DB1AB2", 0, 0.9, "都城币2"
    )

    // Zero attack times remaining indicator
    override val ZeroAttackTimes = ColorSchema.parse(
        1165, 545, 1200, 580, "FFFFFF", "-5|0|2126CF,7|2|2127D2,14|1|FFFFFF,17|1|2126CF,5|14|2129DC,5|-12|221DBA,8|-12|221DBA,7|-8|FFFFFF,7|7|FFFFFF", 0, 0.9, "无进攻机会"
    )

    // Blue raid information button
    override val RaidInformation = ColorSchema.parse(
        1130, 575, 1265, 705, "F2BF6C", "-1|17|EFBC6C,-1|35|E99D2F,-2|51|E4972E,89|0|F2BF6C,92|7|F2BE6C,89|45|E69A2F,90|63|DF922E,42|1|FFFFFF,43|11|FFFFFF", 0, 0.9, "蓝色突袭信息按钮"
    )

    // Raid map button
    override val RaidMap = ColorSchema.parse(
        1135, 570, 1260, 705, "CCE4E5", "-3|21|C9E2E4,-6|48|AAC6CD,-6|66|A8C4CC,43|12|FFE5B4,83|-4|CDE5E6,87|11|CAE3E4,91|36|ACC8CF,91|45|ABC6CE,87|51|AAC6CD", 0, 0.9, "突袭地图按钮"
    )

    // Red attack button in capital raid
    override val RaidRedAttackButton = ColorSchema.parse(
        705, 619, 807, 658, "8380FF", "21|0|827FFF,41|0|8380FF,61|0|8280FF,82|0|827FFF,0|19|221EF7,21|19|221EF7,41|19|1D1AB9,61|19|221EF7,82|19|221EF7", 0, 0.9, "都城红色进攻按钮"
    )

    // Next raid indicator
    override val NextRaid = ColorSchema.parse(
        1066, 63, 1102, 101, "8D8FF3", "7|0|FFFFFF,14|0|21222D,21|0|FFFFFF,28|0|8C8FF3,0|19|2722EF,7|19|F8F8F8,14|19|17171D,21|19|FFFFFF,28|19|2722EF", 0, 0.9, "下次突袭"
    )

    // Capital miner troop icon
    override val CapitalMiner = ColorSchema.parse(
        133, 394, 1143, 694, "0F5590", "8|0|1D4A9C,17|0|1F4DA2,25|0|466692,33|0|35557B,0|29|4F698E,8|29|335F8C,17|29|3E6794,25|29|85A2E1,33|29|95B5FD", 0, 0.9, "都城矿工"
    )

    // Capital train troop page confirm and cancel
    override val CapitalTrainTroopPage = ColorSchema.parse(
        569, 267, 935, 347, "8B85FF", "15|0|8B85FF,39|0|8B85FF,79|6|7E79FF,96|10|7570FF,207|4|7AF1D2,231|4|7AF1D2,267|4|7AF1D2,283|8|74EECC,284|18|60E5B8", 0, 0.9, "练兵界面确认和取消"
    )

    // Capital train troop page confirm and cancel (variant 2)
    override val CapitalTrainTroopPage2 = ColorSchema.parse(
        478, 278, 532, 332, "8D88FF", "13|-3|928DFF,22|-3|928DFF,37|-1|8F8AFF,42|10|7772FF,40|38|1915E0,27|48|1F1FBE,12|48|1F1FBE,13|8|FFFFFF,20|7|FFFFFF", 0, 0.9, "练兵界面确认和取消2"
    )

    // Start raid button
    override val StartRaid = ColorSchema.parse(
        1151, 592, 1254, 692, "64D4B4", "4|16|64D2B1,4|47|28BC8D,40|51|28BB8D,72|48|28BC8D,82|45|28BD8E,84|25|28C192,84|11|64D3B2,83|6|64D4B3,84|-3|64D5B5", 0, 0.9, "发起突袭"
    )

    // District locked indicator
    override val CapitalDistrictLocked = ColorSchema.parse(
        1232, 160, 1258, 188, "DBA34F", "5|0|D7A351,10|0|FFFFFF,15|0|A2783B,20|0|DCA24F,0|14|A96E25,5|14|A96E2A,10|14|F4F4F4,15|14|B5742A,20|14|A96F25", 0, 0.9, "区域未解锁"
    )

    override val CollectCapitalCoins = ColorSchema.parse(
        145, 390, 320, 460, "8EE9BF", "14|6|86E8BA,0|27|3BD48B,-3|38|3BD48B,125|3|88EABA,127|34|E019B7,132|36|DD17B3,125|37|E019B4,-7|43|3BD48B,-2|11|7DE7B4", 0, 0.9, "领都城币"
    )

    override val GemForCapital = ColorSchema.parse(
        1195, 20, 1270, 240, "5BDEB8", "5|0|6CEAD1,10|0|73EDD5,15|0|7BF0DB,20|0|82F3E1,0|11|7DEED3,5|11|84F0D6,10|11|8AF2DC,15|11|63EAC3,20|11|7CF2D8", 0, 0.9, "宝石，用于检测是否进入都城"
    )

    override val MiniPurpleCoinIcon = ColorSchema.parse(
        590, 100, 910, 560, "E514C4", "1|0|E219B9,3|0|DF1AB5,3|1|E018B6,3|2|DF18B4,3|3|DF17B4,2|3|DF17B4,1|3|DF17B5,0|3|E111B8,0|4|CE279D", 0, 0.9, "紫色小都城币"
    )

    override val MiniPurpleCoinIcon2 = ColorSchema.parse(
        590, 100, 910, 560, "E219B8", "1|0|E119B7,2|0|E019B7,3|0|DE1CB4,3|1|DF19B5,3|2|DF18B3,3|3|DC13AD,2|3|DE18B3,1|3|DF17B4,0|2|E018B6", 0, 0.9, "紫色小都城币2"
    )

    override val SufficientCapitalCoin = ColorSchema.parse(
        1194, 25, 1222, 60, "90EDFD", "0|1|8BEDFD,0|2|86EBFD,0|3|82E9FD,0|4|7CE8FD,0|5|77E7FD,0|6|70E5FD,0|7|69E3FD,0|8|17D5FF,0|9|0DD2FF", 0, 0.9, "都城币足够"
    )
}
