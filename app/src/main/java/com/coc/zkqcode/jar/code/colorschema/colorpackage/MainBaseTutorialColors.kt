package com.coc.zkqcode.jar.code.colorschema.colorpackage

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseTutorial {
    val ImportantNotice: ColorSchema
    val SpeakingVillager: ColorSchema
    val SpeakingVillager2: ColorSchema
    val EnterAge: ColorSchema
    val PrivacyInfo: ColorSchema
    val TutorialShop: ColorSchema
    val ShopInnerArrow: ColorSchema
    val TutorialBuildClick: ColorSchema
    val TutorialGoblinAttack: ColorSchema
    val VillagerAttack: ColorSchema
    val TutorialBlueTroop: ColorSchema
    val TutorialTrain: ColorSchema
    val TutorialTrainInner: ColorSchema
    val AttackMap: ColorSchema
    val AttackGoblin: ColorSchema
    val MyVillageIsCalled: ColorSchema
    val TutorialUpgradeTownHall: ColorSchema
    val TutorialMagicalItem: ColorSchema
    val TutorialMagicalItemInner: ColorSchema
    val ShopAfterTutorial: ColorSchema
}

object MainBaseTutorial : IMainBaseTutorial {
    override val ImportantNotice: ColorSchema = ColorSchema.parse(
        252,
        142,
        1024,
        562,
        "1B1B1C",
        "84|3|1B1B1C,184|-3|1B1B1C,279|-4|1B1B1C,317|-8|1B1B1C,336|-91|1B1B1C,330|-157|1B1B1C,312|-247|1B1B1C,174|-254|1B1B1C,87|-253|1B1B1C",
        0,
        0.99,
        "重要提示"
    )
    override val SpeakingVillager: ColorSchema = ColorSchema.parse(
        277,
        313,
        674,
        517,
        "FFFFFF",
        "56|-1|FFFFFF,157|-3|FFFFFF,252|-7|62ACDE,274|-3|68ACDD,275|20|85CFDD,275|101|6AAFE1,-30|108|66AEDE,-48|104|66AADE,-49|5|9EE3FF",
        0,
        0.9,
        "村民说话框"
    )
    override val SpeakingVillager2: ColorSchema = ColorSchema.parse(
        238,
        295,
        665,
        516,
        "67AFDE",
        "10|-1|69B1DF,16|0|89B0D4,293|0|66AEDE,311|4|66AADD,294|162|FFFFFF,4|153|FFFFFF,-5|132|FFFFFF,-35|95|FFFFFF,-41|89|FFFFFF",
        0,
        0.9,
        "村民说话框2"
    )
    override val EnterAge: ColorSchema = ColorSchema.parse(
        417,
        205,
        847,
        602,
        "5E56DA",
        "0|17|3329CF,0|26|3329CF,264|-78|7B808D,266|-93|777D89,269|-166|7B818F,269|-186|777D89,266|-263|7B808E,262|-273|797E8B,129|-280|777D89",
        0,
        0.9,
        "输入年龄"
    )
    override val PrivacyInfo: ColorSchema = ColorSchema.parse(
        678,
        528,
        1215,
        569,
        "3CA8EF",
        "107|0|3CA8EF,215|0|3CA8EF,322|0|3CA8EF,429|0|3CA8EF,0|21|3CA8EF,107|21|3CA8EF,215|21|FFFFFF,322|21|3CA8EF,429|21|3CA8EF",
        0,
        0.9,
        "确定优化"
    )
    override val TutorialShop: ColorSchema = ColorSchema.parse(
        1140,
        572,
        1260,
        699,
        "7AF9FF",
        "5|20|94FFFF,6|35|2F4968,36|44|20A3E5,33|38|49D2FF,24|25|3D6493,36|9|375472,34|-3|F0F4EB,43|-3|ECF1E5,53|2|EEF5E9",
        0,
        0.9,
        "商店按钮"
    )
    override val ShopInnerArrow: ColorSchema = ColorSchema.parse(
        117,
        256,
        843,
        388,
        "00A2FE",
        "1|25|02AAFD,-21|31|0CADFF,-34|5|12B8FF,-12|-19|0896FF,5|-13|09A6FF,21|-36|4EB4FF,33|-26|4EB8FF,38|-15|4EB9FF,18|0|09A5FF",
        0,
        0.9,
        "商店内部箭头"
    )
    override val TutorialBuildClick: ColorSchema = ColorSchema.parse(
        121,
        60,
        1155,
        611,
        "FEFEFE",
        "3|5|FEFEFE,9|-2|FEFEFE,13|-8|FDFDFE,27|-2|2AF8A3,23|8|16BE4B,19|15|14B645,13|21|15BF53,-2|22|16C358,-14|15|14B645",
        0,
        0.93,
        "教程建造绿色箭头"
    )
    override val TutorialGoblinAttack: ColorSchema = ColorSchema.parse(
        629,
        548,
        818,
        617,
        "77F4D7",
        "34|-4|7FF7DC,74|-5|81F7DD,91|-4|7FF7DB,105|9|63EECA,113|29|1FBB6C,107|34|1EBB6D,47|37|1FBD71,14|33|1FBB6D,-3|28|1FBB6C",
        0,
        0.9,
        "教程哥布林进攻"
    )
    override val VillagerAttack: ColorSchema = ColorSchema.parse(
        364,
        411,
        572,
        494,
        "7DF7DA",
        "32|-3|83F8DD,66|-4|85F8DF,99|-3|83F8DE,114|8|6BF2D0,109|35|1FBB6C,73|41|20BF73,35|37|1FBB6D,10|33|1FBB6C,-7|29|1FBC6D",
        0,
        0.9,
        "村民建议进攻哥布林"
    )
    override val TutorialBlueTroop: ColorSchema = ColorSchema.parse(
        97, 582, 205, 618,
        "C08545",
        "1|-2|C78948,8|-9|DD9853,13|-3|CB8E4D,13|-2|C88C4C,9|-3|CA8D4C,5|-6|D4924F,-1|-3|CA8B4A,-3|0|C08545,-4|-6|D3914E",
        0,
        0.9,
        "蓝色部队标志"
    )
    override val TutorialTrain: ColorSchema = ColorSchema.parse(
        654,
        503,
        771,
        622,
        "F1EDF8",
        "8|6|EDE8EF,18|15|E9DCDC,26|21|1D476B,27|28|264873,32|31|27649E,17|28|347FAC,26|17|3A8AB7,6|13|D3A68A,-6|2|C29B83",
        0,
        0.9,
        "训练部队"
    )
    override val TutorialTrainInner: ColorSchema = ColorSchema.parse(
        92,
        206,
        341,
        566,
        "485A7E-101010",
        "20|17|405174-101010,34|58|425275-101010,-2|99|B9BFC2-101010,-1|115|C2CBD3-101010,14|125|38435B-101010,39|124|364564-101010,51|122|354464-101010,70|115|334260-101010,90|104|30405F-101010",
        0,
        0.9,
        "训练部队内部"
    )
    override val AttackMap: ColorSchema = ColorSchema.parse(
        22,
        580,
        144,
        674,
        "D5F0FF",
        "-14|2|C2E5F8,-20|-14|D6F1FF,0|-26|DAEDF9,15|-23|9EA8F5,21|-18|A9C9DE,23|-13|B5CFDD,18|-5|B6D0FC,-3|7|486BE2,3|15|E0F5FF",
        0,
        0.9,
        "进攻地图"
    )
    override val AttackGoblin: ColorSchema = ColorSchema.parse(
        349,
        470,
        520,
        538,
        "53C7FF",
        "20|-4|54CAFF,44|-8|56CBFF,65|-7|55CBFF,77|-1|53C7FF,87|9|4ABDFF,87|22|145EF1,74|27|1256EE,37|36|1250EC,10|33|1251ED",
        0,
        0.9,
        "哥布林森林"
    )
    override val MyVillageIsCalled: ColorSchema = ColorSchema.parse(
        381,
        197,
        909,
        434,
        "E0E8E8",
        "21|11|E0E8E8,351|5|E0E8E8,392|13|E0E8E8,392|154|E0E8E8,393|169|E0E8E8,146|141|79F5D8,200|138|7FF7DB,15|148|E0E8E8,4|151|E0E8E8",
        0,
        0.9,
        "村庄取名"
    )
    override val TutorialUpgradeTownHall: ColorSchema = ColorSchema.parse(
        799,
        578,
        1003,
        678,
        "89F6D4",
        "24|9|87F1CF,45|7|88F2D0,75|7|88F2D0,104|4|88F4D1,114|-2|89F8D5,107|48|48C997,87|46|48CA99,29|45|48CB99,10|42|49CD9C",
        0,
        0.9,
        "教程升级大本营"
    )
    override val TutorialMagicalItem: ColorSchema = ColorSchema.parse(
        953,
        85,
        1027,
        150,
        "FAF6F6",
        "4|-3|FFFFFF,7|-12|FFFFFF,17|-2|7972FF,15|10|1511EA,11|20|2B23CC,1|20|2B23CC,-19|16|211BDA,-17|5|1611EC,-17|-2|7972FF",
        0,
        0.9,
        "误触魔法物品"
    )
    override val TutorialMagicalItemInner: ColorSchema = ColorSchema.parse(
        965,
        58,
        1016,
        111,
        "FFFFFF",
        "0|4|FFFFFF,4|8|FBFBFB,17|9|221EF7,12|-8|8785FF,-3|-9|8785FF,-11|-5|8684FF,-13|1|7F7CFF,-15|10|221EF7,0|18|221FE7",
        0,
        0.9,
        "误触魔法物品内部"
    )
    override val ShopAfterTutorial: ColorSchema = ColorSchema.parse(
        1133,
        568,
        1263,
        702,
        "2F4C70-101010",
        "-16|15|2E587E-101010,-40|36|1EADEA-101010,-46|19|2EC1EA-101010,-42|0|36C7EA-101010,-40|-19|CDFAFF-101010,50|38|1BA9EA-101010,56|15|30C4EA-101010,60|2|35C5E9-101010,57|-9|C3F6FF-101010",
        0,
        0.9,
        "教程后的商店"
    )
}
