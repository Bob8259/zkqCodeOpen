@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseUpgradeColors {
    val MainBaseWallInShop: ColorSchema
    val MainBaseInsufficientResources: ColorSchema
    val MainBaseWorker: ColorSchema
    val MainBaseWorker2: ColorSchema
    val MainBaseWorker3: ColorSchema
    val GoblinWorker: ColorSchema
    val smallElixirUpgradeIcon: ColorSchema
    val builderBaseSmallElixirUpgradeIcon: ColorSchema
}

object MainBaseUpgradeColors : IMainBaseUpgradeColors {
    override val MainBaseWallInShop = ColorSchema.parse(
        503, 269, 772, 627, "669CC7", "3|0|5687AB,6|0|6BC0FF,9|0|5698C9,12|0|4987C4,0|12|3A5668,3|12|2F4457,6|12|36516E,9|12|3A5879,12|12|5791BC", 0, 0.9, "商店内部城墙"
    )
    override val MainBaseInsufficientResources = ColorSchema.parse(
        798, 594, 994, 668, "7F88FF", "1|0|7F88FF,2|0|7F88FF,3|0|7F88FF,3|1|7F88FF,2|1|7F88FF,0|1|7F88FF,0|1|7F88FF,0|2|7F88FF,1|2|7F88FF", 0, 0.97, "主世界升级资源不足"
    )
    override val MainBaseWorker = ColorSchema.parse(
        452, 10, 853, 82, "90BDED", "3|0|93BFED,7|0|7095C9,11|0|0D37A3,14|0|2F6BCD,0|15|7D88D5,3|15|6271C2,7|15|707BC0,11|15|91BAEE,14|15|88B0EA", 0, 0.9,
    )
    override val MainBaseWorker2 = ColorSchema.parse(
        452, 10, 853, 82, "DEEDF1", "5|0|7194E0,11|0|97AFF5,17|0|DDECFB,22|0|B2BFCF,0|9|727381,5|9|898B96,11|9|828492,17|9|676875,22|9|46464B", 0, 0.9,
    )
    override val MainBaseWorker3 = ColorSchema.parse(
        452, 10, 853, 82, "807450", "6|0|6283CB,12|0|607AC5,17|0|CFC9B2,23|0|8992AB,0|7|92B9EF,6|7|7B88D1,12|7|6572B8,17|7|94BAEF,23|7|6C8BD7", 0, 0.9,
    )
    override val GoblinWorker = ColorSchema.parse(
        550, 10, 853, 82, "37AB98", "-10|8|57D5CD,-20|0|3B9C8F,-20|-7|2C9098,-8|-12|155D68,-1|-17|48EED9,5|-18|4DFADA,7|-6|2C5A4E,13|-2|49DFC6,3|4|3CAA97", 0, 0.9, "哥布林工人"
    )
    override val smallElixirUpgradeIcon = ColorSchema.parse(
        1, 1, 1270, 710, "FF60FF", "-2|1|FF2DEB,-3|2|FF41D2,-1|3|FF22D3,0|5|DF21B2,2|5|F221C2,2|4|FF22D9,3|4|FF23DD,3|3|FF26EC,3|2|FF39FF", 0, 0.9
    )
    override val builderBaseSmallElixirUpgradeIcon = ColorSchema.parse(
        1, 1, 1270, 710, "FF299A", "0|-2|FF4CC2,-2|-3|FF47BF,-3|0|FF338B,3|0|FF45BC,3|-3|FF81ED,3|-5|FF5FD9,1|3|FF2C83,-2|3|FA307B,-3|2|FB307B", 0, 0.9
    )
}
