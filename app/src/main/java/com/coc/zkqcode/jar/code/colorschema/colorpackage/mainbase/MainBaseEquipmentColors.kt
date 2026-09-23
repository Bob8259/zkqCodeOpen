@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

// Hero equipment identification colour schemas used by the equipment upgrade flow.
// Each schema identifies a specific hero equipment item by its on-screen colours.
interface IMainBaseEquipmentColors {
    // Barbarian King equipments
    val BarbarianKingBarbarianPuppet: ColorSchema
    val BarbarianKingBarbarianPuppetEquipped: ColorSchema
    val BarbarianKingRageVial: ColorSchema
    val BarbarianKingRageVialEquipped: ColorSchema
    val BarbarianKingEarthquakeBoots: ColorSchema
    val BarbarianKingEarthquakeBootsEquipped: ColorSchema
    val BarbarianKingVampstache: ColorSchema
    val BarbarianKingGiantGauntlet: ColorSchema
    val BarbarianKingGiantGauntletEquipped: ColorSchema
    val BarbarianKingSpikyBall: ColorSchema
    val BarbarianKingSnakeBracelet: ColorSchema
    val BarbarianKingSnakeBraceletEquipped: ColorSchema
    val BarbarianKingStickHorse: ColorSchema

    // Archer Queen equipments
    val ArcherQueenArcherPuppet: ColorSchema
    val ArcherQueenArcherPuppetEquipped: ColorSchema
    val ArcherQueenInvisibilityVial: ColorSchema
    val ArcherQueenInvisibilityVialEquipped: ColorSchema
    val ArcherQueenGiantArrow: ColorSchema
    val ArcherQueenGiantArrowEquipped: ColorSchema
    val ArcherQueenHealerPuppet: ColorSchema
    val ArcherQueenFrozenArrow: ColorSchema
    val ArcherQueenMagicMirror: ColorSchema
    val ArcherQueenActionFigure: ColorSchema
    val ArcherQueenMonolithArrow: ColorSchema

    // Minion Prince equipments
    val MinionPrinceHenchmenPuppet: ColorSchema
    val MinionPrinceHenchmenPuppetEquipped: ColorSchema
    val MinionPrinceDarkOrb: ColorSchema
    val MinionPrinceDarkOrbEquipped: ColorSchema
    val MinionPrinceMetalPants: ColorSchema
    val MinionPrinceNobleIron: ColorSchema
    val MinionPrinceDarkCrown: ColorSchema
    val MinionPrinceMeteorStaff: ColorSchema

    // Grand Warden equipments
    val GrandWardenEternalTome: ColorSchema
    val GrandWardenLifeGem: ColorSchema
    val GrandWardenRageGem: ColorSchema
    val GrandWardenHealingTome: ColorSchema
    val GrandWardenFireball: ColorSchema
    val GrandWardenLavaloonPuppet: ColorSchema
    val GrandWardenHeroicTorch: ColorSchema

    // Royal Champion equipments
    val RoyalChampionSeekingShield: ColorSchema
    val RoyalChampionRoyalGem: ColorSchema
    val RoyalChampionHogRiderPuppet: ColorSchema
    val RoyalChampionHasteVial: ColorSchema
    val RoyalChampionRocketSpear: ColorSchema
    val RoyalChampionElectroBoots: ColorSchema
    val RoyalChampionFrostFlake: ColorSchema

    // Dragon Duke equipments
    val DragonDukeFireHeart: ColorSchema
    val DragonDukeFlameBlower: ColorSchema
    val DragonDukeStunBlaster: ColorSchema
    val DragonDukeElectroFangs: ColorSchema
    val DragonDukeRocketBackpack: ColorSchema

    // Equipment upgrade flow indicators
    val EquipmentUpgradeArrow: ColorSchema
    val NewEquipment: ColorSchema
}

object MainBaseEquipmentColors : IMainBaseEquipmentColors {
    // Barbarian King equipments
    override val BarbarianKingBarbarianPuppet = ColorSchema.parse(
        65, 275, 1235, 615, "9BD8FF", "-10|-1|7FBDF8,-12|-18|6C97DD,-2|-19|38CFFF,12|-11|A7E9FF,12|3|D8FFFF,16|13|495F79,14|21|40546C,7|23|374961,0|22|33435A", 0, 0.9, "野蛮人木偶"
    )
    override val BarbarianKingBarbarianPuppetEquipped = ColorSchema.parse(
        93, 512, 177, 595, "737C82", "17|0|618DB6,34|0|575F67,51|0|737C82,68|0|23D37B,0|41|737C82,17|41|737C82,34|41|7DDE4B,51|41|E6AA60,68|41|8D8B65", 0, 0.9, "野蛮人木偶（穿戴）"
    )
    override val BarbarianKingRageVial = ColorSchema.parse(
        65, 275, 1235, 615, "FF43AD", "-18|-16|4B464F,-10|-22|54585D,1|-10|967F66,12|-1|D147C3,19|8|FF7FFF,17|16|FF5DFF,11|20|FF3D85,3|12|FF4087,-7|4|FF4C8B", 0, 0.9, "狂暴药水瓶"
    )
    override val BarbarianKingRageVialEquipped = ColorSchema.parse(
        99, 514, 170, 590, "524444", "14|0|74AADC,29|0|697176,43|0|737C82,57|0|23D37B,0|38|3F3E43,14|38|B345B3,29|38|504542,43|38|E66B9A,57|38|E659EA", 0, 0.9, "狂暴药水瓶（穿戴）"
    )
    override val BarbarianKingEarthquakeBoots = ColorSchema.parse(
        65, 275, 1235, 615, "1465B5", "-20|-9|3FAEEB,-11|-19|7EE7FF,1|-3|17234A,20|-8|1159BD,12|9|38C5F2,11|14|1767A3,6|20|34BCED,-5|7|2197E1,-17|-9|4BBCF2", 0, 0.9, "地震金靴"
    )
    override val BarbarianKingEarthquakeBootsEquipped = ColorSchema.parse(
        89, 416, 752, 599, "82D0E4", "4|32|399AD1,29|33|77DCEA,-9|33|334261,-9|23|2D3D70,30|-5|0D211B,34|-9|FFFFFF,33|-16|1DE891,36|-1|23D47B,2|19|6283A4", 0, 0.9, "地震金靴（穿戴）"
    )
    override val BarbarianKingVampstache = ColorSchema.parse(
        65, 275, 1235, 615, "FDDB6A", "-19|-8|5CD5FF,-9|-15|62C8FF,1|-18|7FE5F5,15|-12|6BCEFF,21|-7|6FDFFF,27|-1|4894DC,25|8|5698D1,16|16|4E9BFF,1|8|FFD54E", 0, 0.9, "嗜血胡须"
    )
    override val BarbarianKingGiantGauntlet = ColorSchema.parse(
        65, 275, 1235, 615, "1C4C8D", "6|8|1C6AB9,17|9|2B97D8,8|20|0D1449,-8|8|0E1B7C,-10|-1|0F27B9,-16|-10|12376E,2|-11|122B57,12|1|122243,23|7|2884C9", 0, 0.9, "巨型手套"
    )
    override val BarbarianKingGiantGauntletEquipped = ColorSchema.parse(
        182, 512, 264, 593, "6F7883", "17|0|45A6D1,33|0|305074,49|0|737C82,66|0|23D47B,0|41|737C82,17|41|737C82,33|41|404931,49|41|3B8DC3,66|41|737C82", 0, 0.9, "巨型手套（穿戴）"
    )
    override val BarbarianKingSpikyBall = ColorSchema.parse(
        65, 275, 1235, 615, "ABAB77", "-19|4|363D28,-6|-8|BEBF8E,18|-11|E6EEDA,22|-3|74956E,19|9|49B9DE,16|12|43AFE1,22|16|2C75AD,11|22|56644C,3|24|4C5942", 0, 0.9, "尖刺足球"
    )
    override val BarbarianKingSnakeBracelet = ColorSchema.parse(
        65, 275, 1235, 615, "FF74E2", "9|0|FF6BD9,22|-7|4D9790,27|9|58B9F2,16|28|144B94,1|23|0E90D8,-22|-6|0E3C77,12|-20|277CB6,16|-16|1760A4,12|1|FF63D3", 0, 0.9, "灵蛇手镯"
    )
    override val BarbarianKingSnakeBraceletEquipped = ColorSchema.parse(
        274, 431, 352, 507, "394866", "15|0|BFCA58,31|0|C8D865,47|0|2CB9DF,62|0|36753E,0|38|737C82,15|38|277DB7,31|38|2794C8,47|38|D0D67F,62|38|737C82", 0, 0.9, "灵蛇手镯（穿戴）"
    )
    override val BarbarianKingStickHorse = ColorSchema.parse(
        65, 275, 1235, 615, "C5D5F1", "-14|1|9C84E6,-7|-9|0D84FE,3|-16|CBDAF8,16|-17|10A9FC,19|-12|0D81E7,11|-6|C9D7F6,11|15|1D4F7F,15|21|225589,20|11|D33BBC", 0, 0.9, "木棍马驹"
    )

    // Archer Queen equipments
    override val ArcherQueenArcherPuppet = ColorSchema.parse(
        65, 275, 1235, 615, "3CB874", "-9|-8|77CAFC,-9|-19|FF6EFF,5|-23|FFA6FF,11|-6|70B5D9,15|-4|5696B7,14|12|44CA83,13|20|3CBA76,-5|20|258850,-1|13|238DDF", 0, 0.9, "弓箭手木偶"
    )
    override val ArcherQueenArcherPuppetEquipped = ColorSchema.parse(
        94, 425, 179, 510, "737C82", "17|0|DF6DEA,34|0|E691EA,51|0|20AB68,68|0|23D67D,0|43|737C82,17|43|737C81,34|43|3F9368,51|43|51B987,68|43|737C82", 0, 0.9, "弓箭手木偶（穿戴）"
    )
    override val ArcherQueenInvisibilityVial = ColorSchema.parse(
        65, 275, 1235, 615, "D0DA7B", "-19|-8|313235,-17|-15|5F6565,-12|-23|5B91C2,-6|-21|4D5357,6|14|FFB955,16|11|FFFF82,12|13|FFBB53,-12|15|63FF2B,-15|10|4AAD2F", 0, 0.9, "隐形药水瓶"
    )
    override val ArcherQueenInvisibilityVialEquipped = ColorSchema.parse(
        185, 431, 264, 507, "90D6E4", "16|0|7DCDE4,32|0|2C3564,47|0|737C81,63|0|36753E,0|38|737C82,16|38|2D324F,32|38|2D5981,47|38|2D4C73,63|38|737C82", 0, 0.9, "隐形药水瓶（穿戴）"
    )
    override val ArcherQueenGiantArrow = ColorSchema.parse(
        65, 275, 1235, 615, "3C5A8E", "5|-5|416298,9|-10|3B5788,15|-26|FFFFFF,22|-28|FFFFFF,25|-21|FFFFFF,16|-4|F4B84E,-11|-5|F8BA53,-12|-5|F4B64F,17|10|FFD54B", 0, 0.9, "巨型箭矢"
    )
    override val ArcherQueenGiantArrowEquipped = ColorSchema.parse(
        184, 430, 265, 506, "737C82", "16|0|737C82,33|0|808A8D,49|0|E6E8EA,65|0|36753E,0|38|737C82,16|38|878F97,33|38|737C82,49|38|737C82,65|38|737C82", 0, 0.9, "巨型箭矢（穿戴）"
    )
    override val ArcherQueenHealerPuppet = ColorSchema.parse(
        65, 275, 1235, 615, "22E6FF", "-13|-8|3A72B7,1|-13|4782CC,21|-22|9EFCFF,14|-10|6CB7FF,13|-2|2BECFF,12|8|27EBFF,12|14|25E6FF,3|16|1CCBFF,-5|16|16BEFF", 0, 0.9, "天使木偶"
    )
    override val ArcherQueenFrozenArrow = ColorSchema.parse(
        65, 275, 1235, 615, "FFD828", "4|-13|FFFF3D,16|-20|FFFFD4,17|-11|FF8416,8|2|FFFFAA,5|10|D67A11,-20|0|E44AB8,-20|-7|F557C0,19|12|E148C8,26|16|CD36BB", 0, 0.9, "冰封箭矢"
    )
    override val ArcherQueenMagicMirror = ColorSchema.parse(
        65, 275, 1235, 615, "FFFFF3", "9|-1|FFFFF5,5|10|FFFCCC,10|22|216CA9,20|12|2187C4,24|4|2B92CB,22|-13|0EE8FF,26|18|CA33BA,23|22|C32DBC,-20|0|FFFFD2", 0, 0.9, "克隆魔镜"
    )
    override val ArcherQueenActionFigure = ColorSchema.parse(
        65, 275, 1235, 615, "FAFFFF", "6|6|678BB6,-10|10|A9B5C9,-6|15|6FC84E,18|12|7284B1,15|-5|47BFFF,8|-13|4DA1FD,-6|-10|AFBFD8,-24|-4|6D8BBD,-20|1|A0ACC5", 0, 0.9, "动作人偶"
    )
    override val ArcherQueenMonolithArrow = ColorSchema.parse(
        65, 275, 1235, 615, "FF68FA", "-9|-4|FFA0F3,-2|-9|FFF1FF,10|-17|FFDEFF,20|-18|CE8198,12|0|BA657E,-3|12|9E2C33,-10|21|DA44D0,13|23|C934C3,21|9|DE45BE", 0, 0.9, "擎天箭矢"
    )

    // Minion Prince equipments
    override val MinionPrinceHenchmenPuppet = ColorSchema.parse(
        65, 275, 1235, 615, "DDC310", "12|6|FFEE28,14|13|EED51C,3|17|B6950E,-12|9|555555,-19|-15|937E65,-13|-23|A29C91,-1|-27|F5B44E,10|-17|F9B755,23|-14|626262", 0, 0.9, "护卫玩偶"
    )
    override val MinionPrinceHenchmenPuppetEquipped = ColorSchema.parse(
        96, 431, 175, 507, "E0E15E", "16|0|C7C4BF,32|0|9A9897,47|0|737C82,63|0|36753E,0|38|737C82,16|38|737C82,32|38|81682C,47|38|A1852B,63|38|737C82", 0, 0.9, "护卫玩偶（穿戴）"
    )
    override val MinionPrinceDarkOrb = ColorSchema.parse(
        65, 275, 1235, 615, "593955", "-10|0|FFDBFF,-19|-6|684464,-9|-20|BE2DB7,2|-15|7F437B,15|-11|7B436C,19|1|3F2533,14|7|181116,11|22|FDF22B,14|20|FDF52E", 0, 0.9, "暗黑魔球"
    )
    override val MinionPrinceDarkOrbEquipped = ColorSchema.parse(
        95, 514, 172, 593, "737C82", "15|0|5A3856,31|0|9A3899,46|0|775375,61|0|36753E,0|39|737C82,15|39|988D52,31|39|4B4937,46|39|E0E296,61|39|737C82", 0, 0.9, "暗黑魔球（穿戴）"
    )
    override val MinionPrinceMetalPants = ColorSchema.parse(
        65, 275, 1235, 615, "415F88", "-12|1|9B886A,-6|-16|4C4231,10|-13|95CCD8,25|-12|71A1C8,19|-13|8CC9D9,21|-7|FBFBEF,22|4|CECFB7,24|13|AAAA90,28|18|908D75", 0, 0.9, "铁甲短裤"
    )
    override val MinionPrinceNobleIron = ColorSchema.parse(
        65, 275, 1235, 615, "FDD474", "-19|0|223F4C,-13|-2|89D3EC,11|4|B39D88,12|14|578280,19|14|26495E,-4|13|EBC3A1,-20|8|3D6A6D,-27|3|D6C2A1,4|-10|FFC05F", 0, 0.9, "贵族哑铃"
    )
    override val MinionPrinceDarkCrown = ColorSchema.parse(
        65, 275, 1235, 615, "16140F", "-15|6|474530,-19|-6|D4DCC0,-7|-10|494933,15|-9|50472B,15|4|4B4226,20|15|302A1E,26|14|332D1E,25|-6|E346B0,26|-13|EE4FB7", 0, 0.9, "暗黑皇冠"
    )
    override val MinionPrinceMeteorStaff = ColorSchema.parse(
        65, 275, 1235, 615, "907B73", "-12|-2|FFFFD1,-20|-3|C2A090,-3|-8|FFFF99,5|-3|8B7063,13|5|FFF1C5,24|25|4CA6FB,8|23|D741CD,5|11|FFD769,14|6|FFFFD2", 0, 0.9, "陨石法杖"
    )

    // Grand Warden equipments
    override val GrandWardenEternalTome = ColorSchema.parse(
        65, 275, 1235, 615, "5B1DBA", "-15|-4|162B5A,2|-18|345BB9,10|-7|7AFFFF,18|3|3154AE,15|13|1C3C87,12|17|66859C,0|21|4C9EFC,-4|9|2C4EA4,-4|-4|D539FF", 0, 0.9, "永恒书卷"
    )
    override val GrandWardenLifeGem = ColorSchema.parse(
        65, 275, 1235, 615, "719BEA", "-15|-5|ACC4E6,-8|0|7FAAFF,-2|10|8CDEE7,2|15|5EEAFF,6|19|325BFD,12|21|2FE5FF,18|-6|F0B04D,13|-15|F5B453,5|-29|FBBA53", 0, 0.9, "生命宝石"
    )
    override val GrandWardenRageGem = ColorSchema.parse(
        65, 275, 1235, 615, "B89BEB", "1|7|C2ABE7,12|13|9C87E2,15|21|2847F5,5|10|B39EE5,-12|3|2A16F8,-18|-8|FFC5EF,2|-7|955DFF,12|-14|F6B554,20|-13|EDAC4B", 0, 0.9, "狂暴宝石"
    )
    override val GrandWardenHealingTome = ColorSchema.parse(
        65, 275, 1235, 615, "0D0F9D", "-12|-1|12158E,-9|6|B8FFFF,-5|17|1C5FDA,2|23|8DC4E9,16|19|82B3D4,24|9|5175D1,19|1|C9FFFF,11|-5|5FFFFF,12|-12|3D8DFF", 0, 0.9, "治疗书卷"
    )
    override val GrandWardenFireball = ColorSchema.parse(
        65, 275, 1235, 615, "3370FF", "-17|-3|396EFF,-9|-11|BCF3FF,7|-12|547FFF,15|-5|5F90FF,17|-1|EDFFFF,18|9|B0F7FF,15|13|339FFF,11|15|2E95FF,0|8|EDFFFF", 0, 0.9, "巨大火球"
    )
    override val GrandWardenLavaloonPuppet = ColorSchema.parse(
        65, 275, 1235, 615, "8BE666", "-16|-2|5C6261,-10|-8|757E7E,2|-9|6A7572,8|-9|75807F,11|-3|4D5454,12|1|80DE63,12|2|7AD95B,13|25|416BBF,3|26|4170BF", 0, 0.9, "熔岩气球玩偶"
    )
    override val GrandWardenHeroicTorch = ColorSchema.parse(
        65, 275, 1235, 615, "DCFFFF", "-13|-3|73BDFA,0|-11|FFFFFF,11|-9|FD5FC9,18|-1|DF45B6,12|15|2DC0FC,6|15|198EFD,-9|16|D640CB,0|2|D2FFFF,6|-7|FB66D2", 0, 0.9, "英雄火炬"
    )

    // Royal Champion equipments
    override val RoyalChampionSeekingShield = ColorSchema.parse(
        65, 275, 1235, 615, "4C4D4D", "-16|0|0E2154,-16|-14|121B47,0|-14|3159B1,18|-8|264D9F,24|-4|8C9595,20|5|1A397B,13|14|1D428C,5|21|494949,-4|21|444444", 0, 0.9, "寻踪飞盾"
    )
    override val RoyalChampionRoyalGem = ColorSchema.parse(
        65, 275, 1235, 615, "CBA562", "-15|-2|D2AA64,-24|-14|198EFF,-17|-19|178DFF,-2|-8|A1804C,8|8|34D2FF,7|14|3EF4FF,5|16|3FFCFF,19|27|1EA2ED,25|27|1DA2E8", 0, 0.9, "皇家宝石"
    )
    override val RoyalChampionHogRiderPuppet = ColorSchema.parse(
        65, 275, 1235, 615, "3B57BB", "-8|-3|1B1E49,3|-12|25295C,20|-12|4969E0,9|1|4769E3,11|9|2928C3,11|11|21219F,8|17|243046,0|16|2D3C53,-8|8|466BCE", 0, 0.9, "野猪骑士木偶"
    )
    override val RoyalChampionHasteVial = ColorSchema.parse(
        65, 275, 1235, 615, "8760FF", "-16|-7|454648,-9|-12|434A50,5|-3|815AFF,15|0|424965,21|8|8B62FF,17|16|8C63FF,9|21|FF5EBE,2|17|323237,-11|4|FF51BB", 0, 0.9, "急速药水瓶"
    )
    override val RoyalChampionRocketSpear = ColorSchema.parse(
        65, 275, 1235, 615, "0E6BA2", "-11|0|0D0DAE,-25|-8|EE50B9,-2|-15|F353BC,10|-17|1C98CC,15|-15|8DFFFF,20|-11|7AF2FF,20|3|EB50C4,16|8|E94FCA,13|11|EF56D6", 0, 0.9, "火箭飞矛"
    )
    override val RoyalChampionElectroBoots = ColorSchema.parse(
        65, 275, 1235, 615, "3F454B", "8|9|3A3D42,6|25|FCEF54,-7|18|FFFFFF,-14|7|AEABA1,-14|-5|686366,-4|-14|3C3A33,2|-17|575653,14|-10|505253,19|-7|E1D937", 0, 0.9, "雷电战靴"
    )
    override val RoyalChampionFrostFlake = ColorSchema.parse(
        65, 275, 1235, 615, "FFB74B", "-11|3|FFC8A8,-16|-1|FFC159,-6|-5|FFFFF5,10|-9|FFB240,19|2|FFBD4F,24|8|FFA93C,22|14|FFA93E,14|16|FFFFF8,10|23|FF9C37", 0, 0.9, "冷冽冰晶"
    )

    // Dragon Duke equipments
    override val DragonDukeFireHeart = ColorSchema.parse(
        65, 275, 1235, 615, "84D8FF", "-13|4|587AC6,-21|-4|B0D2FF,-13|-16|2219C0,-5|-16|3437D3,5|-6|89D7FF,13|-4|97E1FF,19|0|C1E8FF,19|9|1B25F3,10|15|152BB5", 0, 0.9, "烈焰之心"
    )
    override val DragonDukeFlameBlower = ColorSchema.parse(
        65, 275, 1235, 615, "84D8FF", "-13|4|587AC6,-21|-4|B0D2FF,-13|-16|2219C0,-5|-16|3437D3,5|-6|89D7FF,13|-4|97E1FF,19|0|C1E8FF,19|9|1B25F3,10|15|152BB5", 0, 0.9, "助燃器"
    )
    override val DragonDukeStunBlaster = ColorSchema.parse(
        65, 275, 1235, 615, "ACFFFF", "-9|-4|C7FFFF,9|0|62CCFF,20|4|6DD8FF,17|10|102F3F,15|16|2E3D60,13|22|201921,2|17|FCD43E,-4|12|FFDC52,-14|9|FFD655", 0, 0.9, "爆震器"
    )
    override val DragonDukeElectroFangs = ColorSchema.parse(
        65, 275, 1235, 615, "6F451D", "-2|9|FFFFDE,-16|1|FFFFDB,-6|-6|71451C,10|-15|FAE0AB,12|-10|F7D89E,15|1|FFD358,20|4|FFEA5C,22|13|F4C63F,4|17|C39239", 0, 0.9, "雷电獠牙"
    )
    override val DragonDukeRocketBackpack = ColorSchema.parse(
        65, 275, 1235, 615, "214C46", "-8|0|5EA6ED,-15|2|F5F2FF,-22|-3|B5B4B5,-7|3|EDEFFF,0|14|679BA7,5|15|81B1B8,5|-4|3D8DF0,15|6|5F5C4C,21|20|CB35C1", 0, 0.9, "火箭背包"
    )

    // Upgrade arrow shown next to an equipment that can be upgraded
    override val EquipmentUpgradeArrow = ColorSchema.parse(
        110, 269, 1173, 303, "4FF3B6", "0|2|41E19E,-2|3|3BD892,2|3|3BD892,2|5|31CB80,2|8|2FC87D,-1|9|2FC87D,-1|9|2FC87D,0|6|2FC87D,1|3|3BD892", 0, 0.9, "升级装备箭头"
    )

    // New equipment marker badge
    override val NewEquipment = ColorSchema.parse(
        88, 420, 1232, 610, "31D197", "-7|0|31D197,-4|-4|2AC788,-5|3|44EFC1,18|4|46F0C5,15|3|44EFC1,18|-4|2AC788,20|-4|2AC788,22|0|31D197,21|3|44EFC1", 0, 0.9, "新装备"
    )
}
