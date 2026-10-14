@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseUpgradeColors {
    val MainBaseWallInShop: ColorSchema
    val MainBaseInsufficientResources: ColorSchema
    val MainBaseWorker: ColorSchema
    val MainBaseWorker2: ColorSchema
    val MainBaseWorker3: ColorSchema
    val MainBaseWorker4: ColorSchema
    val MainBaseWorker5: ColorSchema
    val MainBaseWorker6: ColorSchema
    val NewMainBaseWorker: ColorSchema get() = MainBaseWorker6
    val MainBaseLightningWorker: ColorSchema
    val MainBaseLightningWorker2: ColorSchema
    val GoblinWorker: ColorSchema
    val GoblinWorker2: ColorSchema
    val MainBaseWorkerSchemas: List<ColorSchema>
    val smallElixirUpgradeIcon: ColorSchema
    val smallElixirUpgradeIcon2: ColorSchema
    val builderBaseSmallElixirUpgradeIcon: ColorSchema
    val GemInBuildingList: ColorSchema
}

object MainBaseUpgradeColors : IMainBaseUpgradeColors {
    override val MainBaseWallInShop = ColorSchema.parse(
        503, 269, 772, 627, "669CC7", "3|0|5687AB,6|0|6BC0FF,9|0|5698C9,12|0|4987C4,0|12|3A5668,3|12|2F4457,6|12|36516E,9|12|3A5879,12|12|5791BC", 0, 0.9, "商店内部城墙"
    )
    override val MainBaseInsufficientResources = ColorSchema.parse(
        798, 594, 994, 668, "7F88FF", "1|0|7F88FF,2|0|7F88FF,3|0|7F88FF,3|1|7F88FF,2|1|7F88FF,0|1|7F88FF,0|1|7F88FF,0|2|7F88FF,1|2|7F88FF", 0, 0.97, "主世界升级资源不足"
    )
    override val MainBaseWorker = ColorSchema.parse(
        450, 10, 850, 80, "90BDED", "3|0|93BFED,7|0|7095C9,11|0|0D37A3,14|0|2F6BCD,0|15|7D88D5,3|15|6271C2,7|15|707BC0,11|15|91BAEE,14|15|88B0EA", 0, 0.9,
    )
    override val MainBaseWorker2 = ColorSchema.parse(
        450, 10, 850, 80, "DEEDF1", "5|0|7194E0,11|0|97AFF5,17|0|DDECFB,22|0|B2BFCF,0|9|727381,5|9|898B96,11|9|828492,17|9|676875,22|9|46464B", 0, 0.9,
    )
    override val MainBaseWorker3 = ColorSchema.parse(
        450, 10, 850, 80, "807450", "6|0|6283CB,12|0|607AC5,17|0|CFC9B2,23|0|8992AB,0|7|92B9EF,6|7|7B88D1,12|7|6572B8,17|7|94BAEF,23|7|6C8BD7", 0, 0.9,
    )
    override val MainBaseWorker4 = ColorSchema.parse(
        450, 10, 850, 80, "687ECB", "7|0|9CBEEF,13|0|5F72CA,19|0|6371CE,26|0|93B4EB,0|11|7482C2,7|11|A3AABC,13|11|CAD3E2,19|11|C9D2E2,26|11|989EAF", 0, 0.9,
    )
    override val MainBaseWorker5 = ColorSchema.parse(
        450, 10, 850, 80, "6888D5", "6|0|728DE0,12|0|748DE6,18|0|7CA6E9,24|0|637ACA,0|9|8F97A8,6|9|BAC3D2,12|9|CAD4E1,18|9|A3ACBD,24|9|22242D", 0, 0.9,
    )
    override val MainBaseWorker6 = ColorSchema.parse(
        1141,
        504,
        1188,
        554,
        "7087E6",
        "-6|0|8DAEEE,-6|-5|5B66A0,-1|-7|9BCAFA,9|-7|3767CC,9|1|7A94E2,9|4|4F5688,6|5|A5ACBF,1|5|D6E0EC,-4|6|AFB8C6",
        0,
        0.9,
    )
    override val MainBaseLightningWorker = ColorSchema.parse(
        450, 10, 850, 80, "D2E0FE", "7|0|7489D9,13|0|5073DF,19|0|BBD1FF,26|0|BCD0FE,0|8|A9BACF,7|8|50668A,13|8|495881,19|8|496386,26|8|A2B6C1", 0, 0.9,
    )
    override val MainBaseLightningWorker2 = ColorSchema.parse(
        450, 10, 850, 80, "EDF3F9", "7|0|A2BBFB,14|0|6C8EF4,20|0|BFD3FC,27|0|E5EFFA,0|6|AAC5FF,7|6|8AABF0,14|6|6981CA,20|6|A8C6FE,27|6|A3C3FD", 0, 0.9,
    )
    override val GoblinWorker = ColorSchema.parse(
        450, 10, 850, 80, "37AB98", "-10|8|57D5CD,-20|0|3B9C8F,-20|-7|2C9098,-8|-12|155D68,-1|-17|48EED9,5|-18|4DFADA,7|-6|2C5A4E,13|-2|49DFC6,3|4|3CAA97", 0, 0.9, "哥布林工人"
    )
    override val GoblinWorker2 = ColorSchema.parse(
        450, 10, 850, 80, "5FA5E8", "4|9|41B4A3,-5|10|43BCCD,-14|6|328F8A,-14|-1|1D6871,-10|-6|53D6C1,-2|-6|53EFE2,8|-10|34A990,2|-14|4CFBE3,8|-1|54DDDE", 0, 0.9, "哥布林工人2"
    )
    override val MainBaseWorkerSchemas = listOf(MainBaseWorker, MainBaseWorker2, MainBaseWorker3, MainBaseWorker4, MainBaseWorker5, MainBaseWorker6, MainBaseLightningWorker, MainBaseLightningWorker2)
    override val smallElixirUpgradeIcon = ColorSchema.parse(
        1, 1, 1270, 710, "FF60FF", "-2|1|FF2DEB,-3|2|FF41D2,-1|3|FF22D3,0|5|DF21B2,2|5|F221C2,2|4|FF22D9,3|4|FF23DD,3|3|FF26EC,3|2|FF39FF", 0, 0.9
    )
    override val smallElixirUpgradeIcon2 = ColorSchema.parse(
        1, 1, 1270, 710, "F622C4", "-2|0|DE22B0,-4|0|B82296,-4|-2|F43EC0,-4|-4|FF55F3,-2|-4|FF3AFB,1|-4|FF97FF,1|-3|FF52FF,1|-2|FF30F4,2|-1|FF23E4", 0, 0.9
    )
    override val builderBaseSmallElixirUpgradeIcon = ColorSchema.parse(
        1, 1, 1270, 710, "FF299A", "0|-2|FF4CC2,-2|-3|FF47BF,-3|0|FF338B,3|0|FF45BC,3|-3|FF81ED,3|-5|FF5FD9,1|3|FF2C83,-2|3|FA307B,-3|2|FB307B", 0, 0.9
    )
    override val GemInBuildingList = ColorSchema.parse(
        0, 0, 0, 0, "94FFE8", "0|-3|82FBE3,-2|-3|7AF7DE,-5|-3|4CD89E,-5|0|82F8DC,-5|2|8EFCE0,-3|2|9BFFE9,0|2|88FBDC,2|2|75FBD8,3|-2|95FFEF", 0, 0.95, "建筑列表宝石"
    )
}
