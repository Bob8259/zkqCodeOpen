@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.clancapital

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IClanCapitalColors {
    val CapitalBarbarian: ColorSchema
    val CapitalBarbarian2: ColorSchema
    val CapitalCoin: ColorSchema
    val ZeroAttackTimes: ColorSchema
    val RaidInformation: ColorSchema
    val RaidMap: ColorSchema
    val RaidRedAttackButton: ColorSchema
    val NextRaid: ColorSchema
    val CapitalMiner: ColorSchema
    val CapitalTrainTroopPage: ColorSchema
    val TrainTroops2: ColorSchema
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
    // Capital train troops button (variant 2)
    override val TrainTroops2 = ColorSchema.parse(
        17, 483, 88, 559, "57F8FF", "33|13|E4DF1E,31|9|DBD815,11|0|57E8FF,14|0|50DEFF,0|17|E2CDEC,3|17|AF9EAF,7|17|ECDDF4,11|17|E3C7E3,14|17|35576E", 0, 0.9, "都城练兵按钮2"
    )
}
