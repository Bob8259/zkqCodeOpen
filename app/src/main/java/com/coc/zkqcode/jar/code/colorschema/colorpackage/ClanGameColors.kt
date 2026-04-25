@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IClanGameColors {
    val Calendar: ColorSchema
    val Calendar2: ColorSchema
    val OpenClanGame: ColorSchema
    val FinishedTask: ColorSchema
    val NormalTasks: ColorSchema
    val OnGoingTask: ColorSchema
    val BuilderBaseClanGame: ColorSchema
    val MainBaseClanGame: ColorSchema
    val AcceptTask: ColorSchema
}

object ClanGameColors : IClanGameColors {
    override val Calendar = ColorSchema.parse(
        175, 630, 475, 705, "5678E1", "6|0|4770D3,12|0|9EB1C8,17|0|ECF1F8,23|0|FFFFFF,0|12|FFFFFF,6|12|FFFFFF,12|12|3620B1,17|12|B5AFF1,23|12|FFFFFF", 0, 0.9, "日历标志"
    )
    override val Calendar2 = ColorSchema.parse(
        175, 630, 475, 705, "784EFC", "-4|10|FDFDFF,-16|7|FFFFFF,-16|3|FFFFFF,-17|-1|FCFCFF,-14|-9|5477E0,-10|-9|5776E3,-4|-5|7050E4,2|-1|FFFFFF,5|13|FFFFFF", 0, 0.9, "日历标志2"
    )
    override val OpenClanGame = ColorSchema.parse(
        200, 85, 1075, 690, "7AF1D2", "70|-2|7FF3D7,93|0|7AF1D2,-562|-76|174AF1,-516|-58|1F41B8,-419|-81|C7974C,-415|-10|CD9D50,-136|9|FFBB9E,-18|-33|FFBDA5,82|-33|FFBDAB", 0, 0.9, "打开部落竞赛"
    )
    override val FinishedTask = ColorSchema.parse(
        597, 273, 601, 286, "6ADDFA", "1|0|6ADDFA,2|0|6ADDFA,3|0|6ADDFA,4|0|40C8EA,0|7|40C8EA,1|7|40C8EA,2|7|40C8EA,3|7|40C8EA,4|7|40C8EA", 0, 0.9, "已完成的竞赛"
    )
    override val NormalTasks = ColorSchema.parse(
        638, 258, 1078, 305, "52E053", "8|4|52E053,18|16|52E053,10|11|52E053,14|-1|52E053,14|-1|52E053,14|12|52E053,26|9|52E053,20|10|52E053,2|1|52E053", 0, 0.9, "竞赛页面普通任务"
    )
    override val OnGoingTask = ColorSchema.parse(
        478, 127, 505, 304, "44CCEE", "2|16|50D8FA,2|24|4DD5F7,2|36|44CDEF,2|43|43CBED,2|49|42CAEC,2|53|43CCEE,2|59|44CDEF,1|65|46CEF0,-1|68|45CEF0", 0, 0.9, "进行中的任务"
    )
    override val BuilderBaseClanGame = ColorSchema.parse(
        0, 0, 0, 0, "825B0D", "1|0|825B0D,2|0|825B0D,3|0|825B0D,4|0|825B0D,5|0|815B0D,6|0|815B0D,7|0|825B0D,8|0|815A0D,9|0|815A0D", 0, 0.96, "夜世界任务"
    )
    override val MainBaseClanGame = ColorSchema.parse(
        0, 0, 0, 0, "D2925C", "1|0|D1915C,2|0|D1915C,3|0|D1925C,4|0|D1925C,5|0|D1915C,6|0|D1915C,7|0|D1925C,8|0|D1925C,9|0|D2925D", 0, 0.96, "主世界任务"
    )
    override val AcceptTask = ColorSchema.parse(
        392, 92, 1158, 618, "74E3AE", "33|0|74E3AE,67|0|74E3AE,101|0|74E3AE,134|0|74E3AE,0|35|3AD48B,33|35|3AD48B,67|35|3AD48B,101|35|3AD48B,134|35|3AD48B", 0, 0.9, "接取任务"
    )
}
