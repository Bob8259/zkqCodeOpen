@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IBuilderBaseUpgradeColors {
    val InnerShopArrow: ColorSchema
    val BuilderBaseWallInShop: ColorSchema
    val UpgradeHammer: ColorSchema
    val UpgradeHammer2: ColorSchema
    val BuilderBaseInsufficientResources: ColorSchema
    val BuilderBaseWorker: ColorSchema
    val BuilderBaseWorker2: ColorSchema
    val BuilderBaseWorker3: ColorSchema
    val NewBuilderBaseWorker: ColorSchema
    val RebuildGemMine: ColorSchema
}

object BuilderBaseUpgradeColors : IBuilderBaseUpgradeColors {
    override val InnerShopArrow = ColorSchema.parse(
        161, 263, 1280, 363, "00A3FD", "9|0|00A2FD,18|0|01A0FF,27|0|08A7FF,36|0|13AEFF,0|25|02ABFF,9|25|01ABFE,18|25|01A6FD,27|25|04A1FF,36|25|0C9BFF", 0, 0.9, "商店内部箭头"
    )
    override val BuilderBaseWallInShop = ColorSchema.parse(
        503, 269, 772, 627, "2B3756", "3|0|5086B6,6|0|4C76A3,8|0|6197CB,11|0|65A4DC,0|11|252E47,3|11|252E48,6|11|263A57,8|11|2E466C,11|11|3B5D88", 0, 0.9, "商店内部城墙"
    )
    override val UpgradeHammer = ColorSchema.parse(
        146, 499, 1154, 626, "E6E6F3", "11|-8|DEDFEF,12|2|476ECD,18|7|5582F0,21|9|5A82EF,25|10|4F74D5,30|14|5F87F0,38|18|5C7EE3,-4|10|CFDCE7,-2|1|D3D3DE", 0, 0.9, "升级锤子"
    )
    override val UpgradeHammer2 = ColorSchema.parse(
        156, 493, 1127, 638, "DAC9C5", "3|2|EFEFF7,6|5|F0E7F1,6|10|BF9882,12|10|FFFEFF,12|15|CFA388,17|15|E6D8D8,17|22|C09A82,31|30|294A79,33|33|284A75", 0, 0.9, "大本营武器剑"
    )
    override val BuilderBaseInsufficientResources = ColorSchema.parse(
        560, 520, 1083, 676, "7F88FF", "1|0|7F88FF,2|0|7F88FF,3|0|7F88FF,3|1|7F88FF,2|1|7F88FF,0|1|7F88FF,0|1|7F88FF,0|2|7F88FF,1|2|7F88FF", 0, 0.97, "夜世界升级资源不足"
    )
    override val BuilderBaseWorker = ColorSchema.parse(
        518, 5, 1009, 78, "84A8EA", "3|0|ABD9FD,7|0|3F71CE,11|0|477DDC,14|0|4C81DD,0|17|455199,3|17|4D5BAF,7|17|3F4D87,11|17|7DA7EC,14|17|6493EA", 0, 0.9,
    )
    override val BuilderBaseWorker2 = ColorSchema.parse(
        518, 5, 1009, 78, "A0BBF5", "7|0|7687F1,14|0|7483EA,21|0|A8C6E7,28|0|DBD8E7,0|7|426BC0,7|7|5384E8,14|7|5081E5,21|7|3760B6,28|7|AEACCD", 0, 0.9,
    )
    override val BuilderBaseWorker3 = ColorSchema.parse(
        518, 5, 1009, 78, "A0C0FA", "6|0|5660AB,13|0|6B7ADC,19|0|A7CDFD,25|0|7687E1,0|9|111E3A,6|9|5E73A0,13|9|9AB0CC,19|9|41517C,25|9|2B4D90", 0, 0.9,
    )
    override val NewBuilderBaseWorker = ColorSchema.parse(
        1138,
        501,
        1189,
        556,
        "5484E7",
        "-3|-7|314E8B,-1|-14|52595C,4|-11|537AD1,7|-10|4874CF,8|-8|345392,10|-5|EFFFFF,10|0|4C7BDC,3|3|95AACB,-1|3|8F98C3",
        0,
        0.9,
    )

    // Rebuild gem mine button in builder base
    override val RebuildGemMine = ColorSchema.parse(
        522, 426, 789, 695, "8DFAE2", "17|0|8DFAE2,43|-1|8DFAE3,77|-1|8DFAE3,109|-1|8DFAE3,4|65|1FBC6D,37|61|1FBC6D,71|67|1FBC6D,95|68|1FBC6E,119|66|1FBC6D", 0, 0.9, "重建"
    )
}
