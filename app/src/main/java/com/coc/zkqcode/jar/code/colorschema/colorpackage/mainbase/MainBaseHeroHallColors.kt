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
    val NewGear: ColorSchema
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

    // Clan castle button variants shown after opening hero hall area
    val OpenClanCastle2: ColorSchema
    val OpenClanCastle3: ColorSchema
    val OpenClanCastle4: ColorSchema
    val OpenClanCastle5: ColorSchema
    val OpenClanCastle6: ColorSchema
    val OpenClanCastle7: ColorSchema
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

    // Upgrade arrow shown on available gear entries
    override val UpgradeGearArrow = ColorSchema.parse(
        114, 269, 1191, 302, "2EC87D", "0|-2|36D288,2|-2|36D288,0|-1|2ECB80,3|-1|2ECB80,3|1|2EC87D,3|3|2EC87D,2|3|2EC87D,2|-4|41E39E,1|-4|41E39E", 0, 0.9, "升级装备箭头"
    )

    // New gear indicator shown in the hero hall list
    override val NewGear = ColorSchema.parse(
        114, 269, 1191, 302, "2ECB88", "0|1|2ECD91,0|2|2ECE93,0|3|2ED196,0|4|41EABA,0|5|42EABB,24|1|2ECD91,24|2|2ECE93,24|3|2ED196,24|4|41EABA", 0, 0.9, "新装备"
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
}
