package com.coc.zkqcode.jar.code.colorschema.colorpackage

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IUIColors {
    val Reconnection: ColorSchema
    val GreenConfirm: ColorSchema
    val CNAd: ColorSchema
    val ClanChat: ColorSchema
    val CNProsperity: ColorSchema
    val CNPuppetAd: ColorSchema
    val Achievement: ColorSchema
    val ClaimAchievement: ColorSchema
}

object UIColors : IUIColors {
    override val Reconnection: ColorSchema = ColorSchema.parse(
        240,
        195,
        1030,
        530,
        "1B1B1C",
        "106|-4|1B1B1C,184|8|1B1B1C,142|-121|1B1B1C,236|-139|1B1B1C,302|-142|1B1B1C,442|-13|1B1B1C,541|-48|1B1B1C,539|-91|1B1B1C,523|-135|1B1B1C",
        0,
        0.9,
        "重连1"
    )
    override val GreenConfirm: ColorSchema = ColorSchema.parse(
        577,
        564,
        731,
        642,
        "7AF1D2",
        "25|-3|7FF3D7,45|-4|81F4D9,60|-4|81F4D9,66|5|70ECC8,67|17|53DFAB,67|23|2CCD84,45|35|30BA71,22|33|2FBF76,-1|26|2CCC83",
        0,
        0.9,
        "绿色确认"
    )
    override val CNAd: ColorSchema = ColorSchema.parse(
        1074,
        44,
        1140,
        110,
        "FFFFFF",
        "5|9|FFFFFF,-17|18|F1F1F1,-15|-6|F8F9FA,-6|-18|F9FAF9,8|-18|FAFAF9,15|-17|F9FAF9,19|-8|F9FAFA,18|0|F5F5F5,15|7|E5EAEF",
        0,
        0.9,
        "国服广告"
    )
    override val ClanChat: ColorSchema = ColorSchema.parse(
        485,
        301,
        562,
        421,
        "28AAF3",
        "5|17|28AAF3,6|35|3B8AEA,3|54|3B8AEA,-15|56|3B8AEA,-23|46|3B8AEA,-23|40|3B8AEA,-24|31|3B8AEA,-22|14|28AAF3,-19|7|28AAF3",
        0,
        0.9,
        "部落聊天框"
    )
    override val CNProsperity: ColorSchema = ColorSchema.parse(
        596,
        379,
        661,
        491,
        "28AAF3",
        "1|16|28AAF3,0|33|3B8AEA,-15|49|3B8AEA,-24|40|3B8AEA,-27|26|3B8AEA,-31|13|28AAF3,-34|1|28AAF3,-27|-12|28AAF3,-13|-8|28AAF3",
        0,
        0.9,
        "繁荣度聊天框"
    )
    override val CNPuppetAd: ColorSchema = ColorSchema.parse(
        1157,
        84,
        1206,
        131,
        "6EB1F9",
        "-9|-3|173182,-1|-11|122C6E,5|-6|19317F,9|-3|17307F,5|3|638CDE,0|5|2136A1,-2|6|2337A6,-8|-2|193085,-11|-3|173181",
        0,
        0.9,
        "垃圾皮影广告"
    )
    override val Achievement: ColorSchema = ColorSchema.parse(
        59,
        3,
        92,
        39,
        "2915E2",
        "-3|0|2815E4,-15|0|2915E1,-11|-19|1818FF,-9|-19|1818FF,-7|-19|1818FF,-5|-19|1818FF,-4|-19|1818FF,-2|-19|1818FF,-1|-19|1818FF",
        0,
        0.9, "成就红色"
    )
    override val ClaimAchievement: ColorSchema = ColorSchema.parse(
        932,
        177,
        1128,
        666,
        "70EDC9-101010",
        "22|0|70EDC9-101010,26|-7|83F5DB-101010,89|-3|7AF1D2-101010,105|-2|77F0CF-101010,120|-2|77F0CF-101010,141|13|2CCC82-101010,130|16|2EC47B-101010,23|15|2DC77E-101010,5|15|2DC77E-101010",
        0,
        0.9,
        "领取成就绿色按钮"
    )
}
