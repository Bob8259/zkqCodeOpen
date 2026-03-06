package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseUpgradeColors {
    val MainBaseWallInShop: ColorSchema
    val MainBaseInsufficientResources: ColorSchema
}

object MainBaseUpgradeColors : IMainBaseUpgradeColors {
    override val MainBaseWallInShop: ColorSchema = ColorSchema.parse(
        146, 404, 1238, 449, "669CC7", "3|0|5687AB,6|0|6BC0FF,9|0|5698C9,12|0|4987C4,0|12|3A5668,3|12|2F4457,6|12|36516E,9|12|3A5879,12|12|5791BC", 0, 0.9, "商店内部城墙"
    )
    override val MainBaseInsufficientResources: ColorSchema = ColorSchema.parse(
        798, 594, 994, 668, "7F88FF", "1|0|7F88FF,2|0|7F88FF,3|0|7F88FF,3|1|7F88FF,2|1|7F88FF,0|1|7F88FF,0|1|7F88FF,0|2|7F88FF,1|2|7F88FF", 0, 0.97, "夜世界升级资源不足"
    )
}
