package com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IBuilderBaseUpgradeColors {
    val InnerShopArrow: ColorSchema
    val WallInShop: ColorSchema
    val UpgradeHammer: ColorSchema
    val BuilderBaseInsufficientResources: ColorSchema
}

object BuilderBaseUpgradeColors : IBuilderBaseUpgradeColors {
    override val InnerShopArrow: ColorSchema = ColorSchema.parse(
        161, 263, 1280, 363, "00A3FD", "9|0|00A2FD,18|0|01A0FF,27|0|08A7FF,36|0|13AEFF,0|25|02ABFF,9|25|01ABFE,18|25|01A6FD,27|25|04A1FF,36|25|0C9BFF", 0, 0.9, "商店内部箭头"
    )
    override val WallInShop: ColorSchema = ColorSchema.parse(
        618, 401, 658, 453, "EADEAC", "8|0|567188,16|0|5C96CD,24|0|7BDBF3,32|0|EADEAC,0|26|4F9371,8|26|325A47,16|26|47956C,24|26|57B688,32|26|63CD98", 0, 0.9, "商店内部城墙"
    )
    override val UpgradeHammer: ColorSchema = ColorSchema.parse(
        146, 499, 1154, 626, "E6E6F3", "11|-8|DEDFEF,12|2|476ECD,18|7|5582F0,21|9|5A82EF,25|10|4F74D5,30|14|5F87F0,38|18|5C7EE3,-4|10|CFDCE7,-2|1|D3D3DE", 0, 0.9, "升级锤子"
    )
    override val BuilderBaseInsufficientResources: ColorSchema = ColorSchema.parse(
        560, 520, 1083, 676, "7F88FF", "1|0|7F88FF,2|0|7F88FF,3|0|7F88FF,3|1|7F88FF,2|1|7F88FF,0|1|7F88FF,0|1|7F88FF,0|2|7F88FF,1|2|7F88FF", 0, 0.97, "夜世界升级资源不足"
    )
}
