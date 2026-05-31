@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseHeroHallColors {
    val OpenHeroHall: ColorSchema
    val OpenHeroHall2: ColorSchema
    val OpenClanCastle: ColorSchema
    val PetShopIcon: ColorSchema
    val PetShopIcon2: ColorSchema
    val PetShopIcon3: ColorSchema
    val RedExclamationMark: ColorSchema
    val PlaceBannerButton: ColorSchema
    val UpgradeGearArrow: ColorSchema
    val UpgradeGearArrow2: ColorSchema
    val UpgradeGearArrow3: ColorSchema
    val UpgradeGearArrow4: ColorSchema
    val UpgradeGearArrow5: ColorSchema
    val UpgradeGearArrow6: ColorSchema
    val UpgradeGearArrow7: ColorSchema
    val NewGear: ColorSchema
    val GearMaxLevel: ColorSchema
    val SmithOreIcon: ColorSchema
    val PetsIconInHeroHall: ColorSchema
    val PetsIconInHeroHall2: ColorSchema
    val PetsShopInnerBanner: ColorSchema
    val Lassi: ColorSchema
    val Lassi2: ColorSchema
    val Lassi3: ColorSchema
    val ElectroOwl: ColorSchema
    val ElectroOwl2: ColorSchema
    val MightyYak: ColorSchema
    val MightyYak2: ColorSchema
    val Unicorn: ColorSchema
    val Frosty: ColorSchema
    val Frosty2: ColorSchema
    val Diggy: ColorSchema
    val Diggy2: ColorSchema
    val PoisonLizard: ColorSchema
    val PoisonLizard2: ColorSchema
    val Phoenix: ColorSchema
    val Phoenix2: ColorSchema
    val SpiritFox: ColorSchema
    val AngryJelly: ColorSchema
    val AngryJelly2: ColorSchema
    val Sneezy: ColorSchema
    val Sneezy2: ColorSchema
    val GreedyRaven: ColorSchema
    val ConfirmUpgradePet: ColorSchema
    val AlreadyBeingUpgraded: ColorSchema

    // Gear identification schemas
    val BarbarianKingIronFist: ColorSchema
    val BarbarianKingPuppet: ColorSchema
    val BarbarianKingRageVial: ColorSchema
    val BarbarianKingEarthquakeBoots: ColorSchema
    val BarbarianKingHealingBeard: ColorSchema
    val BarbarianKingSpikedSoccer: ColorSchema
    val ArcherQueenCloak: ColorSchema
    val ArcherQueenPuppet: ColorSchema
    val ArcherQueenGiantArrow: ColorSchema
    val ArcherQueenFrostArrow: ColorSchema
    val ArcherQueenHealerPuppet: ColorSchema
    val GrandWardenEternalTome: ColorSchema
    val GrandWardenLifeGem: ColorSchema
    val GrandWardenHealingTome: ColorSchema
    val GrandWardenGiantFireball: ColorSchema
    val GrandWardenRageGem: ColorSchema
    val RoyalChampionSeekingShield: ColorSchema
    val RoyalChampionHogPuppet: ColorSchema
    val RoyalChampionRoyalGem: ColorSchema
    val RoyalChampionSpeedVial: ColorSchema
    val DragonDukeFireRing: ColorSchema
    val DragonDukeScaleArmor: ColorSchema
    val DragonDukeCombustor: ColorSchema
    val DragonDukeLightningFang: ColorSchema
    val MinionPrinceGuardianPuppet: ColorSchema
    val MinionPrinceIronPants: ColorSchema
    val MinionPrinceNobleDumbbell: ColorSchema
    val MinionPrinceDarkOrb: ColorSchema

    // Clan castle button variants shown after opening hero hall area
    val OpenClanCastle2: ColorSchema
    val OpenClanCastle3: ColorSchema
    val OpenClanCastle4: ColorSchema
    val OpenClanCastle5: ColorSchema
    val OpenClanCastle6: ColorSchema
    val OpenClanCastle7: ColorSchema
    val OpenClanCastle8: ColorSchema
}

object MainBaseHeroHallColors : IMainBaseHeroHallColors {
    // Hero hall entry icon used to open the management page
    override val OpenHeroHall = ColorSchema.parse(
        160, 490, 1120, 630, "4EB7F9", "7|0|5FC5FD,14|0|98EBFF,20|0|F9FFFF,27|0|2D5B89,0|17|0E90D2,7|17|3EB6F2,14|17|51BCF2,20|17|4C8DBD,27|17|0E75AA", 0, 0.9, "英雄殿堂标志"
    )
    override val OpenHeroHall2 = ColorSchema.parse(
        180, 536, 1094, 625, "1471A7", "5|0|49B5F8,10|0|65CCFF,14|0|70D6FF,19|0|40BEEA,0|11|0D8BC8,5|11|108ECF,10|11|47B9F2,14|11|46B6EB,19|11|4484B5", 0, 0.9, "英雄殿堂9按钮标志"
    )

    // Clan castle entry button shown from the hero hall area
    override val OpenClanCastle = ColorSchema.parse(
        196, 509, 1082, 628, "497CB2", "9|0|6DC7E5,18|0|3D404F,27|0|3DAFD1,36|0|4B7DB6,0|21|4579AE,9|21|3699C7,18|21|141A20,27|21|86D9F3,36|21|4678AF", 0, 0.9, "检测宝库"
    )

    // Direct pet shop entry button variant shown near the hero hall area
    override val PetShopIcon = ColorSchema.parse(
        162, 495, 1106, 627, "45C1DE", "7|0|36B5DA,15|0|1B85AE,23|0|38B2CF,30|0|2A9FC3,0|15|2EACD4,7|15|42BEDB,15|15|3EB9D7,23|15|39B2D1,30|15|279ABF", 0, 0.9, "直接开启宠物店按钮1"
    )

    // Alternate direct pet shop entry button variant
    override val PetShopIcon2 = ColorSchema.parse(
        190, 540, 1090, 626, "45C1DD", "5|0|55D0ED,11|0|1981AD,16|0|39B3D2,21|0|2393BA,0|11|3CBDDF,5|11|42BEDC,11|11|3EB9D7,16|11|3AB3D2,21|11|2392B7", 0, 0.9, "直接开启宠物店按钮2"
    )
    override val PetShopIcon3 = ColorSchema.parse(
        205, 528, 1077, 624, "44C0DC", "5|0|0D4586,10|0|197AA4,15|0|25A0C1,20|0|126090,0|13|1E81B5,5|13|42BEDB,10|13|3EB9D7,15|13|3BB5D5,20|13|0D518C", 0, 0.9, "宠物店按钮8按钮"
    )

    // Red indicator shown when a hero banner can be placed
    override val RedExclamationMark = ColorSchema.parse(
        606, 559, 637, 585, "5B57F4", "0|2|5B57F4,0|3|5B57F4,0|4|5B57F4,0|5|5B57F4,14|6|5F5BF7,14|5|5F5BF7,14|4|5F5BF6,14|3|5F5BF6,14|2|5F5BF6", 0, 0.9, "红色可摆放战旗感叹号"
    )

    // Button used to confirm hero banner placement
    override val PlaceBannerButton = ColorSchema.parse(
        627, 465, 775, 514, "7EE6B4", "29|0|7EE6B4,59|0|7EE6B4,89|0|7EE6B4,118|0|7EE6B4,0|25|3AD48B,29|25|3AD48B,59|25|3AD48B,89|25|3AD48B,118|25|3AD48B", 0, 0.9, "放置英雄战旗"
    )

    // Equipped gear icon with upgrade indicator (precise region from game screenshot)
    override val UpgradeGearArrow = ColorSchema.parse(
        245, 433, 259, 447, "9B9C9B", "3|0|0D0D0D,6|0|0D0D0D,9|0|FCFAFC,12|0|929292,0|7|21D880,3|7|0E1713,6|7|0D0D0D,9|7|21D880,12|7|21D880", 0, 0.9, "升级装备箭头"
    )
    // Equipped gear icon variant 2 (different position/color scheme)
    override val UpgradeGearArrow2 = ColorSchema.parse(
        332, 433, 348, 447, "E6E3E6", "3|0|0D0D0D,6|0|171717,9|0|FCFAFC,12|0|3A383A,0|7|21D880,3|7|0D0F0E,6|7|0E1C15,9|7|21D880,12|7|21D880", 0, 0.9, "升级装备箭头2"
    )
    // Equipped gear icon variant 3 (different position/color scheme)
    override val UpgradeGearArrow3 = ColorSchema.parse(
        242, 518, 259, 533, "1DE18A", "3|0|FDFDFD,7|0|1ABB73,10|0|FCFAFC,13|0|FFFDFF,0|7|20D880,3|7|1DD880,7|7|252125,10|7|1DD880,13|7|20D880", 0, 0.9, "升级装备箭头3"
    )
    // Equipped gear icon variant 4 (different position/color scheme)
    override val UpgradeGearArrow4 = ColorSchema.parse(
        156, 432, 172, 449, "0E1C16", "3|0|0E1B14,6|0|1DD785,9|0|F4F4F4,12|0|FFFFFF,0|9|23D77D,3|9|1DA65F,6|9|1DA35F,9|9|23D77D,12|9|23D77D", 0, 0.9, "升级装备箭头4"
    )
    // Equipped gear icon variant 5 (different position/color scheme)
    override val UpgradeGearArrow5 = ColorSchema.parse(
        156, 518, 172, 534, "3A3B3A", "3|0|292829,6|0|0D0D0D,9|0|FCFAFC,12|0|EBEBEB,0|8|23D77D,3|8|1DA65F,6|8|1DA35F,9|8|23D77D,12|8|23D77D", 0, 0.9, "升级装备箭头5"
    )
    // Equipped gear icon variant 6 (different position/color scheme)
    override val UpgradeGearArrow6 = ColorSchema.parse(
        332, 517, 347, 534, "0D0D0D", "3|0|19AC6C,6|0|146F46,9|0|F5F4F5,12|0|CBCDCB,0|9|23D77D,3|9|1DA35F,6|9|1DA564,9|9|23D77D,12|9|23D77D", 0, 0.9, "升级装备箭头6"
    )
    // Equipped gear icon variant 7 (dynamic region, used by custom gear upgrade check)
    override val UpgradeGearArrow7 = ColorSchema.parse(
        0, 0, 0, 0, "3BD58E", "1|0|3BD58E,3|0|3BD58E,5|0|3BD58E,6|0|3BD58E,0|7|2EC87D,1|7|2EC87D,3|7|2EC87D,5|7|2EC87D,6|7|2ECA7F", 0, 0.9, "升级装备箭头7"
    )

    // New gear indicator shown in the hero hall list
    override val NewGear = ColorSchema.parse(
        114, 269, 1191, 302, "2ECB88", "0|1|2ECD91,0|2|2ECE93,0|3|2ED196,0|4|41EABA,0|5|42EABB,24|1|2ECD91,24|2|2ECE93,24|3|2ED196,24|4|41EABA", 0, 0.9, "新装备"
    )

    // Gear max level indicator shown in the upgrade detail view (no upgrade button available)
    override val GearMaxLevel = ColorSchema.parse(
        848, 588, 1153, 670, "383A3F", "61|0|383A3F,122|0|383A3F,183|0|383A3F,244|0|383A3F,0|41|383A3F,61|41|383A3F,122|41|383A3F,183|41|383A3F,244|41|383A3F", 0, 0.9, "装备已升到最高等级"
    )

    // Smithy ore icon used to open gear upgrades
    override val SmithOreIcon = ColorSchema.parse(
        405, 627, 418, 643, "B47F28", "3|0|F3A930,6|0|FFC44B,8|0|FFC752,11|0|FFCB59,0|8|B07C0D,3|8|C38E0D,6|8|D1980D,8|8|CB940D,11|8|FFF19E", 0, 0.9, "铁匠铺标志"
    )

    // Pet icon visible from the hero hall page
    override val PetsIconInHeroHall = ColorSchema.parse(
        758, 586, 786, 605, "45C2DF", "5|0|3DAAC9,11|0|1E8EB9,17|0|1672A0,22|0|289CC2,0|9|39B9DA,5|9|42BEDC,11|9|3DB9D7,17|9|32A9CA,22|9|2AA2C5", 0, 0.9, "英雄殿堂宠物标志"
    )
    override val PetsIconInHeroHall2 = ColorSchema.parse(
        760, 586, 784, 607, "3FB8DB", "5|0|33ACD0,10|0|2190BB,15|0|39B3D3,20|0|259FC2,0|11|2398C6,5|11|43BEDB,10|11|3EBAD8,15|11|38B3D2,20|11|2CA1C1", 0, 0.9, "英雄殿堂宠物标志2"
    )

    // Top banner inside the pet shop page
    override val PetsShopInnerBanner = ColorSchema.parse(
        356, 34, 929, 77, "5F6F00", "115|0|5F6F00,230|0|5F6F00,344|0|5F6F00,459|0|5F6F00,0|21|5F6F00,115|21|5F6F00,230|21|5F6F00,344|21|5F6F00,459|21|5F6F00", 0, 0.9, "战宠小屋顶部横幅"
    )

    // Pet slot used to detect Lassi in the hero hall upgrade screen
    override val Lassi = ColorSchema.parse(
        175, 385, 1110, 665, "FA51FA", "15|-34|CEA79B,37|-33|AA604D,55|-7|4E322D,81|-6|694337,88|-57|DF6B8F,93|-75|FF85FF,135|-55|834D3F,135|-28|E88E6D,116|-17|C06350", 0, 0.9, "莱希"
    )

    // Pet slot used to detect Lassi (colour variant 2) in the hero hall upgrade screen
    override val Lassi2 = ColorSchema.parse(
        175, 385, 1110, 665, "7C483E", "52|-3|513835,93|-23|9D5545,87|-25|834B3C,81|-45|784736,81|-47|7A4838,81|-55|925243,133|-56|F5AF7D,126|-38|EB916F,109|-16|BC6353", 0, 0.9, "莱希2"
    )

    // Pet slot used to detect Lassi (colour variant 3) in the hero hall upgrade screen
    override val Lassi3 = ColorSchema.parse(
        175, 385, 1110, 665, "C76A5D", "24|0|4A2F29,73|-2|5B3D37,108|-25|D07059,97|-71|FFFFFF,112|-72|FFECFF,136|-35|EF9E79,118|-8|D27260,68|-96|E78D60,101|-97|EB916C", 0, 0.9, "莱希3"
    )

    // Pet slot used to detect Electro Owl in the hero hall upgrade screen
    override val ElectroOwl = ColorSchema.parse(
        175, 385, 1110, 665, "E2CBB6", "22|-18|E3CDB7,48|-46|BC9562,101|-9|E3CBB5,106|-40|B79F6C,93|-76|BAB653,105|-83|857C4C,116|-19|7C503D,149|-12|8B5649,141|-5|8C584C", 0, 0.9, "闪枭"
    )

    // Pet slot used to detect Electro Owl (colour variant 2) in the hero hall upgrade screen
    override val ElectroOwl2 = ColorSchema.parse(
        175, 385, 1110, 665, "E1CBB5", "24|0|E0CAB4,59|1|D8C2AD,73|-14|D9C3AE,67|-52|FCF4DA,66|-78|FFFFFF,76|-92|DEDC66,100|-91|80784C,118|-63|FFFFA0,104|-48|C3AE72", 0, 0.9, "闪枭2"
    )

    // Pet slot used to detect Mighty Yak in the hero hall upgrade screen
    override val MightyYak = ColorSchema.parse(
        175, 385, 1110, 665, "9DB1BD", "-12|-43|82AEC5,64|-38|255BA1,56|-63|215DA4,66|-62|2A63AF,77|-41|2765B5,80|-16|2868B9,96|-7|2869BF,101|-15|2B7ADB,122|-41|2975DA", 0, 0.9, "大牦"
    )

    // Pet slot used to detect Mighty Yak (colour variant 2) in the hero hall upgrade screen
    override val MightyYak2 = ColorSchema.parse(
        175, 385, 1110, 665, "9DB1BD", "30|-1|4F678C,56|-1|1A4274,82|-18|2867B9,83|-87|2E6ABB,75|-110|3171C7,98|-101|6B8294,103|-74|317BE0,112|-58|378DFF,124|-27|2F74D2", 0, 0.9, "大牦2"
    )

    // Pet slot used to detect Unicorn in the hero hall upgrade screen
    override val Unicorn = ColorSchema.parse(
        175, 385, 1110, 665, "9DB1BD", "45|-8|C6B6B8,55|-24|D1C5CC,65|-50|D5CCD5,84|-62|D3CED9,105|-72|D7CACC,126|-56|E4DCE0,102|-39|D5DEEF,87|-15|CBC3CD,112|0|63CFFF", 0, 0.9, "独角"
    )

    // Pet slot used to detect Frosty in the hero hall upgrade screen
    override val Frosty = ColorSchema.parse(
        175, 385, 1110, 665, "947366", "11|-17|89695C,42|-26|5C8593,72|-14|B8A5A5,101|-13|968D96,84|-20|D3BCBF,114|-7|507D98,77|-39|7E6467,91|-65|362E2E,109|-37|8F8486", 0, 0.9, "冰牙"
    )

    // Pet slot used to detect Frosty (colour variant 2) in the hero hall upgrade screen
    override val Frosty2 = ColorSchema.parse(
        175, 385, 1110, 665, "937265", "31|0|63939F,69|2|9E8A8A,100|-10|9F959C,79|-41|735B5E,76|-65|AA8484,91|-71|463F3C,98|-60|FEDFDE,116|-48|FFEAEB,105|-7|8D8793", 0, 0.9, "冰牙2"
    )

    // Pet slot used to detect Diggy in the hero hall upgrade screen
    override val Diggy = ColorSchema.parse(
        175, 385, 1110, 665, "AC5D7D", "7|-25|E87E92,28|-47|BBD5F6,44|-22|FE8696,80|-2|795D67,115|-12|8D6072,135|-36|D36E99,99|-49|FFA0B9,88|-61|FF9872,88|-89|FFB874", 0, 0.9, "地兽"
    )

    // Pet slot used to detect Diggy (colour variant 2) in the hero hall upgrade screen
    override val Diggy2 = ColorSchema.parse(
        175, 385, 1110, 665, "AA5C7A", "24|-1|9F5964,57|0|B26A85,83|-7|B8708C,83|-44|FF90B9,79|-84|FFB87D,93|-103|FFA768,104|-87|FFAF70,117|-60|FFB3DB,129|-28|D7709A", 0, 0.9, "地兽2"
    )

    // Pet slot used to detect Poison Lizard in the hero hall upgrade screen
    override val PoisonLizard = ColorSchema.parse(
        175, 385, 1110, 665, "9DB1BD", "38|-26|44DBFF,76|-59|3A86FB,94|-81|52A9FF,96|-47|4397FF,83|-39|4082FF,96|-29|44A9FF,116|-39|4AA2FF,116|-70|0D0F52,116|-94|6FD2FF", 0, 0.9, "猛蜥"
    )

    // Pet slot used to detect Poison Lizard (colour variant 2) in the hero hall upgrade screen
    override val PoisonLizard2 = ColorSchema.parse(
        175, 385, 1110, 665, "9DB1BD", "50|-1|3CD0E7,66|-10|45C3FF,70|-32|3E9BFF,90|-68|0D1142,83|-92|73C9FF,96|-101|479BFD,109|-91|8ADBFF,115|-66|0D0E55,94|-17|65F0FF", 0, 0.9, "猛蜥2"
    )

    // Pet slot used to detect Phoenix in the hero hall upgrade screen
    override val Phoenix = ColorSchema.parse(
        175, 385, 1110, 665, "964EFF", "18|-33|AC4AFF,46|-14|48B6FF,67|-52|8956FF,84|-61|53FFFF,104|-43|576276,130|-46|546173,117|-28|35444D,75|-61|50C0DC,68|-108|C14FFF", 0, 0.9, "凤凰"
    )

    // Pet slot used to detect Phoenix (colour variant 2) in the hero hall upgrade screen
    override val Phoenix2 = ColorSchema.parse(
        175, 385, 1110, 665, "8A54FF", "20|2|8C39FC,56|-2|42CCFF,68|-40|5467FF,68|-78|6178FF,78|-115|BB4DFF,104|-81|4ACCFF,128|-54|596476,128|-45|556274,108|-25|404B54", 0, 0.9, "凤凰2"
    )

    // Pet slot used to detect Spirit Fox in the hero hall upgrade screen
    override val SpiritFox = ColorSchema.parse(
        175, 385, 1110, 665, "E3B23B", "28|-7|BE973C,60|-1|E8C48A,88|-1|FFFFCD,70|-33|FFE19E,58|-49|D6CC91,80|-70|8E5B1F,102|-59|0E0E0E,94|-47|FFFFCA,91|-11|FFF5AC", 0, 0.9, "灵狐"
    )

    // Pet slot used to detect Angry Jelly in the hero hall upgrade screen
    override val AngryJelly = ColorSchema.parse(
        175, 385, 1110, 665, "C271FF", "16|-21|DA7EFF,42|-42|FFACFF,59|-29|DE94FF,89|1|A941FF,127|-11|5228E0,133|-38|A54AF4,130|-66|CD53E4,72|-87|FFC7FF,69|-66|FFA8FF", 0, 0.9, "愤怒水母"
    )

    // Pet slot used to detect Angry Jelly (colour variant 2) in the hero hall upgrade screen
    override val AngryJelly2 = ColorSchema.parse(
        175, 385, 1110, 665, "CE66FF", "26|0|882FE5,62|-5|723CD8,56|-30|FEA9FF,58|-50|110EFF,72|-58|120DFF,77|-56|8952FF,109|-73|F599FF,130|-72|E94ADF,116|-36|C182FF", 0, 0.9, "愤怒水母2"
    )

    // Pet slot used to detect Sneezy in the hero hall upgrade screen
    override val Sneezy = ColorSchema.parse(
        175, 385, 1110, 665, "9DB1BD", "34|-36|7F5652,69|-41|C3A19C,92|-61|C9B6E8,113|-32|846CA1,128|-68|947BBE,141|-61|8A6160,121|-42|C8AFDD,89|-12|0D0D0D,66|-32|BA948D", 0, 0.9, "阿啾"
    )

    // Pet slot used to detect Sneezy (colour variant 2) in the hero hall upgrade screen
    override val Sneezy2 = ColorSchema.parse(
        175, 385, 1110, 665, "9DB1BD", "18|-3|9DB1BD,59|-1|6D4946,62|-19|C59D97,77|-59|9877A9,83|-60|B99DD0,104|-43|A08FD1,122|-62|8F7CC0,131|-52|9F87CA,114|-29|826A99", 0, 0.9, "阿啾2"
    )

    // Pet slot used to detect Greedy Raven in the hero hall upgrade screen
    override val GreedyRaven = ColorSchema.parse(
        175, 385, 1110, 665, "787272", "-1|-16|807C77,7|-37|205274,23|-39|43294C,42|-11|613761,63|-37|462C46,68|-54|582E54,82|-100|F8ABFD,103|-87|3E3E45,128|-77|312F2E", 0, 0.9, "贪婪渡鸦"
    )

    // Confirm button shown before starting a pet upgrade
    override val ConfirmUpgradePet = ColorSchema.parse(
        806, 591, 993, 664, "89F8D5", "37|0|89F8D5,63|14|87EFCC,112|0|89F8D5,149|0|89F8D5,0|37|4AD3A2,103|13|87F0CD,127|9|88F2D0,112|37|4AD3A2,149|5|88F5D2", 0, 0.9, "确认升级战宠"
    )

    // Banner shown when another pet upgrade is already running
    override val AlreadyBeingUpgraded = ColorSchema.parse(
        1018, 165, 1052, 326, "7AF7DB", "3|0|81F9DE,7|0|88FDE5,10|0|8DFEEA,13|0|94FFEF,0|9|99FFE7,3|9|6DF3C4,7|9|87FCDD,10|9|7BFBD9,13|9|76FCD9", 0, 0.9, "已有战宠升级中"
    )

    // Barbarian King gears
    override val BarbarianKingIronFist = ColorSchema.parse(
        0, 0, 0, 0, "000000", "0|0|000000", 0, 0.9, "铁剑"
    )
    override val BarbarianKingPuppet = ColorSchema.parse(
        102, 438, 168, 484, "2E27B9", "13|0|4C75D0,26|0|A9FCFF,39|0|4FFFFF,52|0|CAFFFF,0|23|626D7B,13|23|5782D9,26|23|537EDD,39|23|CFFFFF,52|23|D8973B", 0, 0.9, "野蛮人木偶"
    )
    override val BarbarianKingRageVial = ColorSchema.parse(
        99, 518, 174, 587, "2C274D", "16|-4|575A5F,9|7|494A4F,33|9|BC6C89,17|15|8F6C67,38|21|FF45FF,26|27|FF4088,50|35|FF4EFF,43|41|FF3C86,12|-10|5F9BD3", 0, 0.9, "狂暴药水瓶"
    )
    override val BarbarianKingEarthquakeBoots = ColorSchema.parse(
        184, 433, 256, 508, "55C5F4", "31|-10|5DCFFB,7|11|257CD0,32|1|254F99,15|19|153177,37|6|5582B5,16|26|0F2550,38|24|2EB5E9,54|4|71EBFE,38|34|36BEEC", 0, 0.9, "地震金靴"
    )
    override val BarbarianKingHealingBeard = ColorSchema.parse(
        188, 513, 261, 586, "55E6FE", "12|-8|55C2FD,31|-14|67CBFF,46|-3|74D5FF,61|6|A7FFFF,47|14|3466FF,49|26|38578C,59|17|A9FFFF,29|5|4757FF,61|20|A8F7FF", 0, 0.9, "治疗胡须"
    )
    override val BarbarianKingSpikedSoccer = ColorSchema.parse(
        278, 437, 346, 501, "254F67", "13|-6|958253,49|-14|CAD0B2,16|10|1B4156,28|21|48573E,49|10|2E82B9,54|8|72E9FF,30|-28|88F0FF,10|-17|806D41,20|-27|285667", 0, 0.9, "尖刺足球"
    )

    // Archer Queen gears
    override val ArcherQueenCloak = ColorSchema.parse(
        102, 521, 172, 586, "5A8CC0", "-11|13|273B40,7|7|475258,17|14|998B4E,5|23|948361,29|33|FFFF80,14|35|FFB954,39|40|EEFF65,31|49|FFA74F,-23|12|36904A", 0, 0.9, "隐形披风"
    )
    override val ArcherQueenPuppet = ColorSchema.parse(
        102, 433, 170, 505, "E561FF", "1|7|1A2233,22|2|BDFFFF,6|22|2EA35F,39|4|A0F0FF,13|33|238ADB,17|44|2E9257,28|48|437A84,32|28|44586A,20|-6|FFB7FF", 0, 0.9, "弓箭手木偶"
    )
    override val ArcherQueenGiantArrow = ColorSchema.parse(
        192, 436, 259, 490, "FFFFFF", "14|14|FFFFFF,-5|4|BBBEBB,8|4|FFFFFF,11|1|FFFFFF,1|12|1F2737,-3|19|3F5C94,-16|32|36507E,-19|34|2B4065,22|-8|FFFFFF", 0, 0.9, "巨型箭矢"
    )
    override val ArcherQueenFrostArrow = ColorSchema.parse(
        0, 0, 0, 0, "000000", "0|0|000000", 0, 0.9, "冰冻箭"
    )
    override val ArcherQueenHealerPuppet = ColorSchema.parse(
        192, 519, 256, 588, "70EEFF", "9|-1|BAFFFF,-7|4|8BFFFF,-7|15|498BDA,37|7|80D5FF,6|18|1ED9FF,23|18|28F6FF,20|49|3E6EA2,42|44|75ADEE,16|-12|BAFFFF", 0, 0.9, "天使木偶"
    )

    // Grand Warden gears
    override val GrandWardenEternalTome = ColorSchema.parse(
        102, 435, 172, 506, "6FFFFF", "3|0|FFB2FF,12|5|CE4BFF,20|11|460D83,26|12|3656AE,37|15|19FFFF,6|39|4F9FFC,17|37|5F7C8F,-13|16|2E3F33,-19|0|306BBD", 0, 0.9, "永恒书卷"
    )
    override val GrandWardenLifeGem = ColorSchema.parse(
        99, 522, 170, 588, "18365A", "17|7|4F6AFF,18|19|234572,37|22|255EFF,20|39|302EFF,32|50|0D91FF,45|52|0D3D96,55|43|1BF1FF,37|36|58BCDD,6|20|1B28FF", 0, 0.9, "生命宝石"
    )
    override val GrandWardenHealingTome = ColorSchema.parse(
        188, 520, 258, 589, "F6FFFF", "12|-7|78F6FF,2|12|3666FF,18|12|0D11A7,13|23|55F6FF,0|40|2B6AD0,18|41|86B6D8,37|21|2369DF,-10|28|5094AB,-22|9|2546B2", 0, 0.9, "治疗书卷"
    )
    override val GrandWardenGiantFireball = ColorSchema.parse(
        275, 436, 343, 504, "C8F5FF", "-13|18|B7F4FF,-19|30|4CB7FF,-3|43|A8F8FF,3|50|38B5FE,14|45|2EB3FF,26|38|3B9DFF,24|26|EEFFFF,18|15|5978FF,0|-9|7CC8FF", 0, 0.9, "巨大火球"
    )
    override val GrandWardenRageGem = ColorSchema.parse(
        188, 435, 258, 503, "4F1978", "13|-1|D553FF,7|7|FFA0EC,1|12|4B268F,13|12|613C94,24|26|B8A3E2,21|40|494FFF,29|47|1F1AFF,42|45|281EAF,-13|-7|471A71", 0, 0.9, "狂暴宝石"
    )

    // Royal Champion gears
    override val RoyalChampionSeekingShield = ColorSchema.parse(
        100, 436, 172, 504, "515451", "4|-5|E9FBFC,17|0|2653AB,47|5|254EA1,55|1|868F8E,44|27|4C4C4C,25|-7|B5C4C5,18|-7|204184,30|13|0D214F,30|-2|A0ABAC", 0, 0.9, "寻踪飞盾"
    )
    override val RoyalChampionHogPuppet = ColorSchema.parse(
        191, 436, 255, 504, "6A7791", "33|-9|5F8DFF,25|-5|91A7EB,20|17|3E5FC7,47|17|2627C0,27|28|2525BE,31|35|253146,52|37|1D2868,9|15|577AB2,24|-21|23233B", 0, 0.9, "野猪骑士木偶"
    )
    override val RoyalChampionRoyalGem = ColorSchema.parse(
        100, 518, 167, 588, "0D37FF", "8|11|473A2E,25|0|BFAF7A,33|-4|D9D0A4,41|3|C0AF76,19|21|483B2F,35|21|35CFFF,50|27|44FFFF,59|31|187EA9,21|-14|158AFF", 0, 0.9, "皇家宝石"
    )
    override val RoyalChampionSpeedVial = ColorSchema.parse(
        190, 519, 254, 589, "25384F", "24|-8|3E454F,10|4|464546,19|13|8C68FF,38|4|464380,17|17|FF58BA,23|23|313236,29|33|303439,52|26|8B64FF,12|-14|5787BD", 0, 0.9, "急速药水瓶"
    )

    // Dragon Duke gears
    override val DragonDukeFireRing = ColorSchema.parse(
        103, 435, 171, 500, "2923D2", "-18|23|5F7999,3|18|465FFF,19|19|9EE0FF,31|-1|727AFF,21|43|0D117D,9|40|252D77,33|32|83A9D8,-4|33|7B93DA,-19|8|B4D3FB", 0, 0.9, "烈焰之心"
    )
    override val DragonDukeScaleArmor = ColorSchema.parse(
        188, 433, 261, 505, "B3FFFF", "-9|15|AFFFFF,2|10|2E2C91,9|15|DE956E,15|13|DBF5FF,16|22|864B23,22|22|1D7091,9|26|8396FB,21|36|394D7D,-24|-4|C8FFFF", 0, 0.9, "爆震器"
    )
    override val DragonDukeCombustor = ColorSchema.parse(
        102, 520, 170, 590, "94FFFF", "10|4|1582D3,29|0|617893,52|-12|3890D1,45|10|D5EBFF,40|45|1E5771,28|30|102C42,43|2|807A78,45|30|49617B,4|-9|50A8FF", 0, 0.9, "助燃器"
    )
    override val DragonDukeLightningFang = ColorSchema.parse(
        192, 520, 260, 590, "FFEABA", "29|-8|FFE8BD,49|-4|E9C883,20|7|70451D,13|19|F4DC80,32|41|8C6222,37|14|FFFFFF,24|21|F7DC8C,32|19|663F1C,27|-22|D3AB91", 0, 0.9, "雷电獠牙"
    )

    // Minion Prince gears
    override val MinionPrinceGuardianPuppet = ColorSchema.parse(
        105, 435, 170, 504, "9E968E", "19|-8|DAD9DA,18|1|B3AEA3,24|14|CCC4B8,42|17|FCE81D,53|14|4E4946,18|32|494949,37|51|9E7E0D,53|40|F4DC1D,57|41|EACE2C", 0, 0.9, "护卫玩偶"
    )
    override val MinionPrinceIronPants = ColorSchema.parse(
        191, 433, 259, 506, "4F76A1", "-45|5|917657,-30|12|4D6991,-2|19|92937A,-49|19|716452,-14|36|DED8C0,-27|-1|9ABEB8,-40|-9|98C1BF,-8|5|A2E1EC,-40|-9|98C1BF", 0, 0.9, "铁甲短裤"
    )
    override val MinionPrinceNobleDumbbell = ColorSchema.parse(
        186, 519, 258, 586, "575149", "-18|9|DAC6A4,-9|17|8EB361,4|18|91CDD6,14|21|C2B094,32|17|7DB8C8,27|30|68A7A3,14|42|B3A770,9|37|8F7238,-8|-3|AF9780", 0, 0.9, "贵族哑铃"
    )
    override val MinionPrinceDarkOrb = ColorSchema.parse(
        101, 522, 171, 588, "361136", "18|-7|FFFFFF,29|-7|7D4475,48|11|23141B,23|15|675064,37|27|FFFFC2,45|22|403B15,49|-3|CF5FB1,16|4|FFB7FF,12|-14|572546", 0, 0.9, "暗黑魔球"
    )

    // Clan castle collect button variant 2 shown near hero hall
    override val OpenClanCastle2 = ColorSchema.parse(
        174, 548, 1113, 628, "4778AD", "7|0|5FAFD5,14|0|3C4050,20|0|3DAFD1,27|0|4B7DB5,0|15|4374A9,7|15|297E9B,14|15|377590,20|15|78CEEB,27|15|4577AD", 0, 0.9, "领取宝库按钮10按钮"
    )

    // Clan castle collect button variant 3 shown near hero hall
    override val OpenClanCastle3 = ColorSchema.parse(
        173, 526, 1091, 628, "2F557B", "8|0|437A93,17|0|3297B2,25|0|44BFE7,33|0|19374A,0|19|406CA0,8|19|3698C3,17|19|1A191C,25|19|3799C3,33|19|406C9F", 0, 0.9, "领取宝库按钮8按钮"
    )

    // Clan castle collect button variant 4 shown near hero hall
    override val OpenClanCastle4 = ColorSchema.parse(
        174, 562, 1119, 627, "1E303F", "6|0|1A252B,12|0|186267,18|0|42B3DB,24|0|395478,0|14|4173A7,6|14|3698C3,12|14|69DCFF,18|14|3698C3,24|14|4373A7", 0, 0.9, "领取宝库按钮12按钮"
    )
    override val OpenClanCastle5 = ColorSchema.parse(
        210, 502, 1069, 624, "4E7FC5", "11|0|60AFD0,21|0|3A4351,31|0|3EADCE,42|0|5A97D3,0|22|4271A4,11|22|3495C1,21|22|1B1A1E,31|22|2B6780,42|22|406CA1", 0, 0.9, "领取宝库按钮6按钮"
    )
    override val OpenClanCastle6 = ColorSchema.parse(
        172, 564, 1114, 630, "487AB4", "5|0|3CA0C8,10|0|353B4D,15|0|3DABCF,20|0|5695C9,0|11|467AB0,5|11|399FC8,10|11|7EECFF,15|11|3697C6,20|11|3F71A6", 0, 0.9, "领取宝库按钮13按钮"
    )
    override val OpenClanCastle7 = ColorSchema.parse(
        203, 526, 1095, 625, "4A78B5", "9|0|3CA2CB,17|0|3E4353,25|0|3EB7E1,34|0|558EC8,0|21|477CB2,9|21|40AED7,17|21|8AF4FF,25|21|3C8EB8,34|21|4272A8", 0, 0.9, "领取宝库按钮8按钮2"
    )
    override val OpenClanCastle8 = ColorSchema.parse(
        196, 556, 1091, 626, "35527A", "7|0|43A6CF,13|0|2C545F,19|0|41B5DE,26|0|558BC8,0|13|4271A4,7|13|399EC8,13|13|19191C,19|13|3799C4,26|13|497EB4", 0, 0.9, "领取宝库11按钮"
    )
}
