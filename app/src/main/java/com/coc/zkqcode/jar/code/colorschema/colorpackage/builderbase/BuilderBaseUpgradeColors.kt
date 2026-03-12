package com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IBuilderBaseUpgradeColors {
    val InnerShopArrow: ColorSchema
    val BuilderBaseWallInShop: ColorSchema
    val UpgradeHammer: ColorSchema
    val BuilderBaseInsufficientResources: ColorSchema
    val BuilderBaseWorker: ColorSchema
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
    override val BuilderBaseInsufficientResources = ColorSchema.parse(
        560, 520, 1083, 676, "7F88FF", "1|0|7F88FF,2|0|7F88FF,3|0|7F88FF,3|1|7F88FF,2|1|7F88FF,0|1|7F88FF,0|1|7F88FF,0|2|7F88FF,1|2|7F88FF", 0, 0.97, "夜世界升级资源不�?
    )
    override val BuilderBaseWorker = ColorSchema.parse(
        518, 5, 1009, 78, "4375DB", "6|-15|A7D0FE,11|-19|3C6BC9,16|-10|8CAAE2,16|-9|403427,18|0|467CD6,7|12|3C69CD,4|12|416FD0,2|12|3E6BCB", 0, 0.9, "夜世界建筑工�?
    )
}
