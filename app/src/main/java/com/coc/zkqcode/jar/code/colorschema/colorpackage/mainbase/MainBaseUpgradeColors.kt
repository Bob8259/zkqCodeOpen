package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseUpgradeColors {
    val MainBaseWallInShop: ColorSchema
    val MainBaseInsufficientResources: ColorSchema
    val MainBaseWorker: ColorSchema
    val GoblinWorker: ColorSchema
}

object MainBaseUpgradeColors : IMainBaseUpgradeColors {
    override val MainBaseWallInShop: ColorSchema = ColorSchema.parse(
        503, 269, 772, 627, "669CC7", "3|0|5687AB,6|0|6BC0FF,9|0|5698C9,12|0|4987C4,0|12|3A5668,3|12|2F4457,6|12|36516E,9|12|3A5879,12|12|5791BC", 0, 0.9, "商店内部城墙"
    )
    override val MainBaseInsufficientResources: ColorSchema = ColorSchema.parse(
        798, 594, 994, 668, "7F88FF", "1|0|7F88FF,2|0|7F88FF,3|0|7F88FF,3|1|7F88FF,2|1|7F88FF,0|1|7F88FF,0|1|7F88FF,0|2|7F88FF,1|2|7F88FF", 0, 0.97, "夜世界升级资源不足"
    )
    override val MainBaseWorker: ColorSchema = ColorSchema.parse(
        452, 10, 853, 82, "769BE7", "-7|8|CED8E6,-15|-1|90B5ED,-8|-14|6177BB,-3|-21|99C2F0,6|-18|2458C0,9|-13|5E65A1,12|-8|7282C7,5|5|8B92A1,-9|9|CAD4E1", 0, 0.9, "家乡建筑工人"
    )
    override val GoblinWorker: ColorSchema = ColorSchema.parse(
        550, 10, 853, 82, "37AB98", "-10|8|57D5CD,-20|0|3B9C8F,-20|-7|2C9098,-8|-12|155D68,-1|-17|48EED9,5|-18|4DFADA,7|-6|2C5A4E,13|-2|49DFC6,3|4|3CAA97", 0, 0.9, "哥布林工头"
    )

}
