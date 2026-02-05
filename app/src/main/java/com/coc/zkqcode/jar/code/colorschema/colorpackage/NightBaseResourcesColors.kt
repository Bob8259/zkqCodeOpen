package com.coc.zkqcode.jar.code.colorschema.colorpackage

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface INightBaseResourcesColors {
    val CannotCollectExilerCart: ColorSchema
    val CollectExilerCart: ColorSchema
    val NightBaseCollectGold1: ColorSchema
    val NightBaseCollectExiler1: ColorSchema
    val NightBaseCollectGem1: ColorSchema
    val NightBaseCollectGem2: ColorSchema
}

object NightBaseResourcesColors : INightBaseResourcesColors {
    override val CannotCollectExilerCart: ColorSchema = ColorSchema.parse(
        859,
        569,
        1024,
        645,
        "D3D3D3-101010",
        "28|3|D2D2D2-101010,66|0|D3D3D3-101010,82|4|D2D2D2-101010,86|11|CDCDCD-101010,87|30|ADADAD-101010,78|39|ACACAC-101010,45|36|ADADAD-101010,18|34|ADADAD-101010,-8|27|ADADAD-101010",
        0,
        0.98,
        "无法收集圣水车"
    )
    override val CollectExilerCart: ColorSchema = ColorSchema.parse(
        859,
        569,
        1024,
        645,
        "8DEABE-101010",
        "25|-3|8EEABF-101010,65|-1|8EEABF-101010,101|0|8DEABE-101010,107|5|87E9BB-101010,106|35|3AD48B-101010,95|40|39CE87-101010,64|37|3AD38A-101010,13|31|3AD48B-101010,-2|29|3AD48B-101010",
        0,
        0.98,
        "收集圣水车"
    )
    override val NightBaseCollectGold1: ColorSchema =
        ColorSchema.parse(
            115,
            86,
            1127,
            609,
            "10CAEE-101010",
            "-3|2|44D1EE-101010,1|5|03B2E8-101010,5|5|0DBEF1-101010,4|0|1ED9EF-101010,0|-2|46E9ED-101010,-3|16|71B2A9-101010,-13|4|90C0B9-101010,-14|-3|A4C8C3-101010,11|-2|A3CAC4-101010",
            0,
            0.92,
            "收集金币1"
        )
    override val NightBaseCollectExiler1: ColorSchema =
        ColorSchema.parse(
            115,
            82,
            1127,
            609,
            "EA3494-101010",
            "-3|0|DE217A-101010,-3|-5|EB2C9D-101010,-7|-11|B0D1CC-101010,-11|-8|ABCCC9-101010,10|-6|AFD5CF-101010,3|-1|F294D2-101010,1|2|E02280-101010,-2|5|C41757-101010,-5|5|9D114C-101010",
            0,
            0.92,
            "收集圣水1"
        )
    override val NightBaseCollectGem1: ColorSchema =
        ColorSchema.parse(
            115,
            86,
            1127,
            609,
            "85F1DA-101010",
            "-13|-5|AED6D2-101010,12|-5|ADD5D1-101010,12|4|8FC7BF-101010,4|5|3DA57C-101010,-2|6|69EABD-101010,-2|1|84F0D6-101010,-5|3|69E7CB-101010,1|-2|7EF0D9-101010,4|0|62EDCB-101010",
            0,
            0.92,
            "收集宝石1"
        )
    override val NightBaseCollectGem2: ColorSchema =
        ColorSchema.parse(
            115,
            106,
            1127,
            609,
            "85F1D9-101010",
            "-1|-3|74EBD1-101010,4|-5|7BF0DC-101010,3|0|5FE9C1-101010,-13|0|9CCDC7-101010,-12|-8|B4DAD5-101010,13|-9|B6DAD6-101010,3|1|7AF0D1-101010,-2|7|24B48C-101010,4|4|6DF0CC-101010",
            0,
            0.92,
            "收集宝石2"
        )
}
