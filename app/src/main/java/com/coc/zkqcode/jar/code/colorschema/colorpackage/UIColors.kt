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
    val ReturnAwards: ColorSchema
    val NightBackToCamp: ColorSchema
    val MainBackToCamp: ColorSchema
    val CNBackFromAwards: ColorSchema
    val CollectChest: ColorSchema
    val EditModeWrench: ColorSchema
    val CancelEditMode: ColorSchema
    val GoldenPass: ColorSchema
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

    override val ReturnAwards: ColorSchema = ColorSchema.parse(
        110,
        44,
        1173,
        679,
        "00528D",
        "116|-2|00518B,-68|491|00CCF3,-78|517|00DCFA,-78|523|00DDFA,801|536|00DDFA,840|529|009BD8,846|515|008AED,840|399|00437A,839|354|004279",
        0,
        0.9, "回归奖励"
    )

    override val NightBackToCamp: ColorSchema = ColorSchema.parse(
        553,
        574,
        728,
        648,
        "8BEABD",
        "18|-1|8CEABE,46|-1|8CEABE,68|0|8BEABD,80|2|88E9BB,89|35|3AD48B,62|38|3AD38B,35|32|3AD48B,22|34|3AD48B,-16|29|3AD48B",
        0,
        0.9,
        "回营"
    )

    override val MainBackToCamp: ColorSchema = ColorSchema.parse(
        554,
        578,
        726,
        658,
        "79F6D9",
        "40|-5|83F8DD,69|-7|86F8DF,88|8|69F2D0,97|11|64EFCB,96|33|1EBB6C,79|37|1FBB6C,39|35|1FBB6C,18|35|1FBB6C,0|32|1FBC6D",
        0,
        0.9,
        "主世界回营"
    )

    override val CNBackFromAwards: ColorSchema = ColorSchema.parse(
        62,
        74,
        97,
        122,
        "37E0FA-101010",
        "0|2|39E2F7-101010,-2|3|3AE1F9-101010,-5|13|0DA2D1-101010,-2|16|0DA1D0-101010,-1|18|10A3CF-101010,2|12|0D8CBC-101010,2|8|3AE0F8-101010,5|8|0EA3CF-101010,5|12|066E9E-101010",
        0,
        0.9,
        "垃圾奖励"
    )

    override val CollectChest: ColorSchema = ColorSchema.parse(
        547,
        569,
        735,
        627,
        "88E9BB",
        "39|-2|8BEABD,83|-7|8EEABF,129|-5|8EEABF,130|18|3AD48B,124|26|3AD48B,107|32|39D38A,47|34|39D088,19|30|3AD48B,6|25|3AD48B",
        0,
        0.9,
        "领取宝箱"
    )
    override val EditModeWrench: ColorSchema = ColorSchema.parse(
        1197,
        285,
        1257,
        345,
        "FFFFFF-101010",
        "-13|-15|ECFCF8-101010,-1|-20|EFFDFB-101010,4|-21|EFFDFB-101010,13|-21|EFFDFB-101010,18|-17|EDFDF9-101010,22|-10|E9FAF7-101010,6|2|FFFFFF-101010,8|6|FFFFFF-101010,27|5|BDDDDB-101010",
        0,
        0.9,
        "编辑模式扳手"
    )
    override val CancelEditMode: ColorSchema = ColorSchema.parse(
        1051,
        484,
        1269,
        537,
        "817CFF-101010",
        "26|2|7D78FF-101010,50|1|7F7AFF-101010,75|1|7F7AFF-101010,85|24|1712E6-101010,64|29|1511DA-101010,40|29|1511DA-101010,-68|9|FFFFFF-101010,-76|15|FFFEF9-101010,-71|27|1611DF-101010",
        0,
        0.9,
        "取消编辑模式"
    )
    override val GoldenPass: ColorSchema = ColorSchema.parse(
        1118,
        84,
        1157,
        124,
        "8381FF",
        "8|0|FFFFFF,16|0|111115,23|0|FFFFFF,31|0|8381FF,0|20|221EF7,8|20|FBFBFB,16|20|181715,23|20|FBFBFB,31|20|221EF7",
        0,
        0.9, "黄金令牌"
    )
}
