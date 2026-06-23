@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

// Hero equipment identification colour schemas used by the equipment upgrade flow.
// Each schema identifies a specific hero equipment item by its on-screen colours.
interface IMainBaseEquipmentColors {
    // Barbarian King equipments
    val BarbarianKingPuppet: ColorSchema
    val BarbarianKingRageVial: ColorSchema
    val BarbarianKingEarthquakeBoots: ColorSchema
    val BarbarianKingHealingBeard: ColorSchema
    val BarbarianKingSpikedSoccer: ColorSchema
    val BarbarianKingGiantGauntlet: ColorSchema
    val BarbarianKingSnakeBracelet: ColorSchema
    val BarbarianKingStickHorse: ColorSchema

    // Archer Queen equipments
    val ArcherQueenCloak: ColorSchema
    val ArcherQueenPuppet: ColorSchema
    val ArcherQueenGiantArrow: ColorSchema
    val ArcherQueenFrostArrow: ColorSchema
    val ArcherQueenHealerPuppet: ColorSchema
    val ArcherQueenCloneMirror: ColorSchema
    val ArcherQueenActionFigure: ColorSchema
    val ArcherQueenMonolithArrow: ColorSchema

    // Grand Warden equipments
    val GrandWardenEternalTome: ColorSchema
    val GrandWardenLifeGem: ColorSchema
    val GrandWardenHealingTome: ColorSchema
    val GrandWardenGiantFireball: ColorSchema
    val GrandWardenRageGem: ColorSchema
    val GrandWardenLavaBalloonPuppet: ColorSchema
    val GrandWardenHeroicTorch: ColorSchema

    // Royal Champion equipments
    val RoyalChampionSeekingShield: ColorSchema
    val RoyalChampionHogPuppet: ColorSchema
    val RoyalChampionRoyalGem: ColorSchema
    val RoyalChampionSpeedVial: ColorSchema
    val RoyalChampionFrozenCrystal: ColorSchema
    val RoyalChampionRocketSpear: ColorSchema
    val RoyalChampionElectroBoots: ColorSchema

    // Dragon Duke equipments
    val DragonDukeFireRing: ColorSchema
    val DragonDukeScaleArmor: ColorSchema
    val DragonDukeCombustor: ColorSchema
    val DragonDukeLightningFang: ColorSchema
    val DragonDukeRocketPack: ColorSchema

    // Minion Prince equipments
    val MinionPrinceGuardianPuppet: ColorSchema
    val MinionPrinceIronPants: ColorSchema
    val MinionPrinceNobleDumbbell: ColorSchema
    val MinionPrinceDarkOrb: ColorSchema
    val MinionPrinceDarkCrown: ColorSchema
    val MinionPrinceMeteorStaff: ColorSchema
}

object MainBaseEquipmentColors : IMainBaseEquipmentColors {
    // Barbarian King equipments
    override val BarbarianKingPuppet = ColorSchema.parse(
        65, 275, 1235, 615, "7ABCFF", "0|7|8894E8,4|17|394C65,-5|-1|6997EA,-13|-18|41AFFF,0|-17|A5EBFF,14|-12|AFF5FF,9|-7|A1DDFF,5|-1|D6FFFF,6|19|3B4E65", 0, 0.9, "野蛮人木偶"
    )
    override val BarbarianKingRageVial = ColorSchema.parse(
        32, 412, 1248, 610, "FF4489", "11|0|FF46FF,-16|-28|65A4D9,-12|-22|53575D,-40|8|E1B12E,-39|8|E2B22F,-39|7|E2B130,31|-42|54F9BE,31|-37|38D48D,31|-32|2FC87D", 0, 0.9, "狂暴药水瓶"
    )
    override val BarbarianKingEarthquakeBoots = ColorSchema.parse(
        32, 412, 1248, 610, "5278A4", "-14|-10|48BAF1,11|8|32B7EA,28|8|5DEFFF,-36|14|E2B52B,-35|14|E2B52B,-34|14|E3B62C,35|-40|59FFC8,35|-33|34CF86,35|-28|2FC87D", 0, 0.9, "地震金靴"
    )
    override val BarbarianKingHealingBeard = ColorSchema.parse(
        32, 412, 1248, 610, "3666FF", "1|-19|7DE3FF,19|-9|C8FFFF,21|10|3D6DFF,-33|13|E1B32C,-32|13|E1B32C,-31|13|E2B42D,38|-38|54F8BE,38|-31|30CA7F,38|-28|2FC87D", 0, 0.9, "治疗胡须"
    )
    override val BarbarianKingSpikedSoccer = ColorSchema.parse(
        32, 412, 1248, 610, "254F67", "13|-6|958253,49|-14|CAD0B2,16|10|1B4156,28|21|48573E,49|10|2E82B9,54|8|72E9FF,30|-28|88F0FF,10|-17|806D41,20|-27|285667", 0, 0.9, "尖刺足球"
    )
    override val BarbarianKingGiantGauntlet = ColorSchema.parse(
        32, 412, 1248, 610, "123267", "-19|-11|1D66A0,-1|13|0E1A6D,18|20|2892D7,821|15|CC33B1,822|15|CC33B1,823|15|CD34B2,77|-34|28C585,77|-23|4AF8CE,79|-23|4AF8CE", 0, 0.9, "巨型手套"
    )
    override val BarbarianKingSnakeBracelet = ColorSchema.parse(
        32, 412, 1248, 610, "0E68B4", "40|0|1186D7,39|13|C8F479,39|38|1672BE,753|27|CC33B1,754|27|CC33B1,755|27|CD34B2,9|-22|28C585,9|-11|4AF8CE,11|-11|4AF8CE", 0, 0.9, "灵蛇手镯"
    )
    override val BarbarianKingStickHorse = ColorSchema.parse(
        32, 412, 1248, 610, "C1D1EF", "-20|1|D6C2FF,4|-18|158EFF,18|27|113E65,734|-72|CC33B1,735|-72|CC33B1,736|-72|CD34B2,-10|-121|28C585,-10|-110|4AF8CE,-8|-110|4AF8CE", 0, 0.9, "木棍马驹"
    )

    // Archer Queen equipments
    override val ArcherQueenCloak = ColorSchema.parse(
        32, 412, 1248, 610, "AEAF69", "3|9|FFB752,-2|-17|4B545A,-20|-16|24353D,-37|12|E1B529,-36|12|E2B62A,-35|12|E1B52A,34|-41|53F8BE,34|-35|33CE85,34|-32|2FC87D", 0, 0.9, "隐形披风"
    )
    override val ArcherQueenPuppet = ColorSchema.parse(
        32, 412, 1248, 610, "3CB874", "0|13|238DDF,12|9|3B5160,21|-13|98E3FF,-35|13|E1B529,-34|13|E2B62A,-33|13|E0B52A,36|-42|5CFFCB,36|-36|38D48E,36|-31|2FC87D", 0, 0.9, "弓箭手木偶"
    )
    override val ArcherQueenGiantArrow = ColorSchema.parse(
        32, 412, 1248, 610, "3E5D93", "19|-22|FFFFFF,12|-14|354D68,-13|12|2C3F64,-36|12|E1B42A,-35|12|E2B52B,-34|12|E2B52B,35|-39|4EF2B4,35|-34|33CE85,35|-30|2FC87D", 0, 0.9, "巨型箭矢"
    )
    override val ArcherQueenFrostArrow = ColorSchema.parse(
        32, 412, 1248, 610, "FFFFEE", "10|0|FFFF95,26|-23|FF9620,7|-16|FFFF4F,-36|10|CC33B1,-35|10|CC33B1,-34|10|CD34B2,35|-43|53F7BC,35|-36|2FC97E,35|-32|2FC87D", 0, 0.9, "冰冻箭"
    )
    override val ArcherQueenHealerPuppet = ColorSchema.parse(
        32, 412, 1248, 610, "22E1FF", "2|9|76FAFF,1|-11|7AC1FF,13|-19|BCFFFF,-27|10|E1B32C,-26|10|E1B32C,-25|10|E2B42D,44|-42|55F9BF,44|-38|3DDC97,44|-31|2FC87D", 0, 0.9, "天使木偶"
    )
    override val ArcherQueenCloneMirror = ColorSchema.parse(
        32, 412, 1248, 610, "FFFFFF", "0|20|FBF0A2,19|19|2172AE,21|-12|0FEBFF,-36|17|C830B1,-35|17|C830B1,-34|17|C832B2,36|-32|34CF86,-52|51|3DDC97,36|-28|2FC87D", 0, 0.9, "克隆魔镜"
    )
    override val ArcherQueenActionFigure = ColorSchema.parse(
        32, 412, 1248, 610, "FBFFFF", "0|-16|5C9FF7,0|21|25BA1D,1|10|99BCE1,309|-70|CC33B1,310|-70|CC33B1,311|-70|CD34B2,-435|-119|28C585,-435|-108|4AF8CE,-433|-108|4AF8CE", 0, 0.9, "动作人偶"
    )
    override val ArcherQueenMonolithArrow = ColorSchema.parse(
        32, 412, 1248, 610, "FFBDFD", "24|-21|A4374E,0|18|7E2538,-10|18|4C9CBD,398|-65|CC33B1,399|-65|CC33B1,400|-65|CD34B2,-346|-114|28C585,-346|-103|4AF8CE,-344|-103|4AF8CE", 0, 0.9, "擎天箭矢"
    )

    // Grand Warden equipments
    override val GrandWardenEternalTome = ColorSchema.parse(
        32, 412, 1248, 610, "C84BFF", "1|-21|41FCFF,2|13|2D4FA5,15|23|67879D,-36|21|E2B52B,-35|21|E2B52B,-34|21|E2B52B,35|-31|54F8BE,35|-26|37D38C,35|-21|2FC87D", 0, 0.9, "永恒书卷"
    )
    override val GrandWardenLifeGem = ColorSchema.parse(
        32, 412, 1248, 610, "2C65FF", "19|20|1CF4FF,9|27|0E41A4,-17|13|2A2AFF,-48|11|E1B32C,-47|11|E1B32C,-46|11|E2B42D,23|-39|4EF2B5,23|-34|34CE85,23|-30|2FC87D", 0, 0.9, "生命宝石"
    )
    override val GrandWardenHealingTome = ColorSchema.parse(
        32, 412, 1248, 610, "F6FFFF", "12|-7|78F6FF,2|12|3666FF,18|12|0D11A7,13|23|55F6FF,0|40|2B6AD0,18|41|86B6D8,37|21|2369DF,-10|28|5094AB,-22|9|2546B2", 0, 0.9, "治疗书卷"
    )
    override val GrandWardenGiantFireball = ColorSchema.parse(
        32, 412, 1248, 610, "3871FF", "0|8|EDFFFF,7|22|2BB2FF,-5|-16|D5FEFF,-33|11|CB33B2,-32|11|CB33B2,-31|11|CB32B2,-29|-38|2AC788,-29|-29|48F4C9,-27|-28|4AF8CE", 0, 0.9, "巨大火球"
    )
    override val GrandWardenRageGem = ColorSchema.parse(
        32, 412, 1248, 610, "4F1978", "13|-1|D553FF,7|7|FFA0EC,1|12|4B268F,13|12|613C94,24|26|B8A3E2,21|40|494FFF,29|47|1F1AFF,42|45|281EAF,-13|-7|471A71", 0, 0.9, "狂暴宝石"
    )
    override val GrandWardenLavaBalloonPuppet = ColorSchema.parse(
        32, 412, 1248, 610, "697372", "8|0|98EF6F,8|25|314D81,-15|-12|97E28E,-34|12|CB32B2,-33|12|CB32B2,-32|12|CC33B3,-30|-38|28C585,-30|-27|4AF8CE,-28|-27|4AF8CE", 0, 0.9, "熔岩气球玩偶"
    )
    override val GrandWardenHeroicTorch = ColorSchema.parse(
        32, 412, 1248, 610, "FFFFFF", "16|16|2FCDFC,26|28|25FFFF,-13|-12|2499FD,-32|12|CD34B2,-31|12|CD34B2,-30|12|CE35B3,-29|-36|2AC788,-29|-27|48F4CA,-29|-30|42EABC", 0, 0.9, "英雄火炬"
    )

    // Royal Champion equipments
    override val RoyalChampionSeekingShield = ColorSchema.parse(
        32, 412, 1248, 610, "515451", "4|-5|E9FBFC,17|0|2653AB,47|5|254EA1,55|1|868F8E,44|27|4C4C4C,25|-7|B5C4C5,18|-7|204184,30|13|0D214F,30|-2|A0ABAC", 0, 0.9, "寻踪飞盾"
    )
    override val RoyalChampionHogPuppet = ColorSchema.parse(
        32, 412, 1248, 610, "6A7791", "33|-9|5F8DFF,25|-5|91A7EB,20|17|3E5FC7,47|17|2627C0,27|28|2525BE,31|35|253146,52|37|1D2868,9|15|577AB2,24|-21|23233B", 0, 0.9, "野猪骑士木偶"
    )
    override val RoyalChampionRoyalGem = ColorSchema.parse(
        32, 412, 1248, 610, "0D37FF", "8|11|473A2E,25|0|BFAF7A,33|-4|D9D0A4,41|3|C0AF76,19|21|483B2F,35|21|35CFFF,50|27|44FFFF,59|31|187EA9,21|-14|158AFF", 0, 0.9, "皇家宝石"
    )
    override val RoyalChampionSpeedVial = ColorSchema.parse(
        32, 412, 1248, 610, "25384F", "24|-8|3E454F,10|4|464546,19|13|8C68FF,38|4|464380,17|17|FF58BA,23|23|313236,29|33|303439,52|26|8B64FF,12|-14|5787BD", 0, 0.9, "急速药水瓶"
    )
    override val RoyalChampionFrozenCrystal = ColorSchema.parse(
        32, 412, 1248, 610, "FFB649", "-24|-1|FFEFC2,28|-1|FFFFFF,24|11|FFA73C,309|16|CC33B1,310|16|CC33B1,311|16|CD34B2,-455|-33|28C585,-455|-22|4AF8CE,-453|-22|4AF8CE", 0, 0.9, "冷冽冰晶"
    )
    override val RoyalChampionRocketSpear = ColorSchema.parse(
        32, 412, 1248, 610, "0D639C", "-8|2|0D25D4,-10|12|9BBECE,28|-28|53C9F0,371|10|CC33B1,372|10|CC33B1,373|10|CD34B2,-373|-39|28C585,-373|-28|4AF8CE,-371|-28|4AF8CE", 0, 0.9, "火箭飞矛"
    )
    override val RoyalChampionElectroBoots = ColorSchema.parse(
        32, 412, 1248, 610, "3F464C", "22|14|C6C5C3,-4|26|B38625,24|-4|CE9E18,371|-74|CC33B1,372|-74|CC33B1,373|-74|CD34B2,-373|-123|28C585,-373|-112|4AF8CE,-371|-112|4AF8CE", 0, 0.9, "雷电战靴"
    )

    // Dragon Duke equipments
    override val DragonDukeFireRing = ColorSchema.parse(
        32, 412, 1248, 610, "2923D2", "-18|23|5F7999,3|18|465FFF,19|19|9EE0FF,31|-1|727AFF,21|43|0D117D,9|40|252D77,33|32|83A9D8,-4|33|7B93DA,-19|8|B4D3FB", 0, 0.9, "烈焰之心"
    )
    override val DragonDukeScaleArmor = ColorSchema.parse(
        32, 412, 1248, 610, "B3FFFF", "-9|15|AFFFFF,2|10|2E2C91,9|15|DE956E,15|13|DBF5FF,16|22|864B23,22|22|1D7091,9|26|8396FB,21|36|394D7D,-24|-4|C8FFFF", 0, 0.9, "爆震器"
    )
    override val DragonDukeCombustor = ColorSchema.parse(
        32, 412, 1248, 610, "94FFFF", "10|4|1582D3,29|0|617893,52|-12|3890D1,45|10|D5EBFF,40|45|1E5771,28|30|102C42,43|2|807A78,45|30|49617B,4|-9|50A8FF", 0, 0.9, "助燃器"
    )
    override val DragonDukeLightningFang = ColorSchema.parse(
        32, 412, 1248, 610, "6F451E", "6|-18|FFE6BE,6|-28|C99D81,-1|31|563718,-34|17|E1B529,-33|17|E2B62A,-32|17|E1B52A,-710|-119|28C585,-710|-108|4AF8CE,-708|-108|4AF8CE", 0, 0.9, "雷电獠牙"
    )
    override val DragonDukeRocketPack = ColorSchema.parse(
        32, 412, 1248, 610, "F4F2FF", "-8|-17|A7E0FF,-11|-3|AAA49E,16|24|4279D6,-25|12|CC33B1,-24|12|CC33B1,-23|12|CD34B2,-789|-37|28C585,-789|-26|4AF8CE,-787|-26|4AF8CE", 0, 0.9, "火箭背包"
    )

    // Minion Prince equipments
    override val MinionPrinceGuardianPuppet = ColorSchema.parse(
        32, 412, 1248, 610, "3F3F3F", "38|-17|5E5E5E,3|-38|D0CFCA,-12|-28|8D8881,-25|4|E1B42A,-24|4|E2B52B,-23|4|E2B52B,46|-50|53F7BD,46|-43|2FC97E,46|-39|2FC87D", 0, 0.9, "护卫玩偶"
    )
    override val MinionPrinceIronPants = ColorSchema.parse(
        32, 412, 1248, 610, "4E709C", "19|0|EDEDDC,-319|19|2172AE,-15|-10|B78C58,-33|15|E1B42A,-32|15|E2B52B,-31|15|E2B52B,38|-38|54F9BF,38|-31|30CA7F,38|-28|2FC87D", 0, 0.9, "铁甲短裤"
    )
    override val MinionPrinceNobleDumbbell = ColorSchema.parse(
        32, 412, 1248, 610, "8DCED8", "-21|-9|D8C4A3,28|1|7BB5C7,12|3|BCAD9C,-32|13|E1B42A,-31|13|E2B52B,-30|13|E2B52B,39|-40|54FABF,39|-32|2FC87D,39|-30|2FC87D", 0, 0.9, "贵族哑铃"
    )
    override val MinionPrinceDarkOrb = ColorSchema.parse(
        32, 412, 1248, 610, "6E576A", "-3|-13|FFB1FF,-12|-21|5E2056,-7|-32|572647,-33|6|E1B529,-32|6|E2B62A,-31|6|E1B52A,38|-134|53F7BD,38|-127|2FC97E,38|-123|2FC87D", 0, 0.9, "暗黑魔球"
    )
    override val MinionPrinceDarkCrown = ColorSchema.parse(
        32, 412, 1248, 610, "17160F", "-23|0|BCC4A4,9|14|584E2D,23|14|48421F,-38|12|CC33B1,-37|12|CC33B1,-36|12|CD34B2,-782|-37|28C585,-782|-26|4AF8CE,-780|-26|4AF8CE", 0, 0.9, "暗黑皇冠"
    )
    override val MinionPrinceMeteorStaff = ColorSchema.parse(
        32, 412, 1248, 610, "8D786F", "-10|-1|FFFFDC,4|16|FFF9D5,26|25|419CEC,-35|-70|CC33B1,-34|-70|CC33B1,-33|-70|CD34B2,-779|-119|28C585,-779|-108|4AF8CE,-777|-108|4AF8CE", 0, 0.9, "陨石法杖"
    )
}
