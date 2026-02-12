package com.coc.zkqcode.jar.code.colorschema.colorpackage

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IFeatureColors {
    val OldShopButton: ColorSchema
    val NewShopButton: ColorSchema
    val MagicalItem: ColorSchema
    val TrainTroops: ColorSchema
    val RebuildNightBase: ColorSchema
}

object FeatureColors : IFeatureColors {
    override val OldShopButton: ColorSchema = ColorSchema.parse(
        1195,
        19,
        1261,
        88,
        "FFFFFF",
        "-4|2|FAF6F6,-23|8|1A16ED,-20|-6|867FFF,-18|-15|958FFF,4|-20|958DFF,21|-7|8A82FF,14|4|1511EC,12|21|1913D3,-14|26|352DCE",
        0,
        0.9,
        "旧版商店叉叉"
    )
    override val NewShopButton: ColorSchema = ColorSchema.parse(
        1159,
        8,
        1209,
        57,
        "FAF6F6",
        "-3|-5|FFFFFF,-10|-5|857DFF,-5|-15|958DFF,14|-10|938BFF,13|-3|7D76FF,14|8|1612E6,3|12|1A15D1,-4|13|211BCE,-10|13|221CCF",
        0,
        0.9,
        "新版商店叉叉"
    )
    override val MagicalItem: ColorSchema = ColorSchema.parse(
        960,
        57,
        1016,
        110,
        "FBFBFB",
        "-14|-4|716FFE,-6|-14|8785FF,13|10|221EF2,12|-13|8785FF,13|-10|8583FF,13|-3|7A78FF,13|5|221EF7,10|17|1B18AA,3|14|221EF7",
        0,
        0.9,
        "魔法物品"
    )
    override val TrainTroops: ColorSchema = ColorSchema.parse(
        17,
        483,
        88,
        559,
        "A2BFF1",
        "-7|15|D3DFFF,-13|7|87FFFF,-13|-5|60F8FF,-10|-11|5CF2FF,-3|-11|4CD3FF,10|-10|C9AC97,17|-14|FFFFFF,17|-3|FAFF78,15|0|F9FF6E",
        0,
        0.9
    )
    override val RebuildNightBase: ColorSchema = ColorSchema.parse(
        580,
        501,
        706,
        625,
        "DAE4F3-101010",
        "4|-10|E2E1ED-101010,12|-16|D3D2E1-101010,14|-18|D2D1E0-101010,10|-3|DCEAFF-101010,23|2|5684F0-101010,24|2|5583F2-101010,32|5|365AA7-101010,38|13|6187F0-101010,42|15|6D88EB-101010",
        0,
        0.9,
        "重建夜世界帆船"
    )
}
