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
    val GiveUpTaskButton: ColorSchema
    val TaskCoolDown: ColorSchema
    val FailedTask: ColorSchema
    val FourThousandPoints: ColorSchema
    val UnclaimedReward1: ColorSchema
    val UnclaimedReward2: ColorSchema
    val UnclaimedReward3: ColorSchema
    val UnclaimedReward4: ColorSchema
    val UnclaimedReward5: ColorSchema
    val UnclaimedReward6: ColorSchema
    val ClanGameRewards: ColorSchema
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
        638, 258, 1078, 305, "52E053", "8|4|52E053,18|16|52E053,10|11|52E053,14|-1|52E053,14|-1|52E053,14|12|52E053,26|9|52E053,20|10|52E053,2|1|52E053", 0, 0.97, "竞赛页面普通任务"
    )
    override val OnGoingTask = ColorSchema.parse(
        478, 127, 505, 304, "44CCEE", "2|16|50D8FA,2|24|4DD5F7,2|36|44CDEF,2|43|43CBED,2|49|42CAEC,2|53|43CCEE,2|59|44CDEF,1|65|46CEF0,-1|68|45CEF0", 0, 0.97, "进行中的任务"
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
    override val GiveUpTaskButton = ColorSchema.parse(
        634, 120, 1028, 428, "7674FF", "7|0|7674FF,99|3|221EF7,117|24|221EF7,51|24|0D4DD0,65|24|0D39AC,12|33|221EF7,3|41|221EF7,99|43|221EF7,125|40|221EF7", 0, 0.92, "放弃任务"
    )
    override val TaskCoolDown = ColorSchema.parse(
        541, 164, 1024, 485, "4ED69F", "5|0|55DFAF,10|0|7CF8E0,14|0|86FBE6,19|0|90FFEF,0|13|90FCE1,5|13|94FDE1,10|13|85FBD8,14|13|7DFBD6,19|13|79FCDD", 0, 0.97, "冷却宝石标志"
    )
    override val FailedTask = ColorSchema.parse(
        478, 134, 526, 311, "3A2EF2", "0|8|272CEB,0|15|272CEB,0|24|272CEB,0|27|272CEB,1|35|282DEC,1|39|292EED,1|42|292FEE,1|51|3037F7,0|56|3D44FF", 0, 0.9, "失败的任务"
    )
    override val FourThousandPoints = ColorSchema.parse(
        243, 638, 249, 663, "6ADDFA", "-1|0|6ADDFA,-2|0|6ADDFA,-3|0|6ADDFA,-4|0|6ADDFA,-4|22|6ADDFA,-3|22|6ADDFA,-2|22|6ADDFA,-1|22|6ADDFA,1|12|FFFFFF", 0, 0.9, "4000积分"
    )
    override val UnclaimedReward1 = ColorSchema.parse(
        450, 165, 542, 485, "D2D2D2",
        "-3|19|D2D2D2,-1|111|D2D2D2,1|201|D2D2D2,72|201|D2D2D2,78|288|D2D2D2,2|291|D2D2D2,33|199|D2D2D2,31|110|D2D2D2,73|10|D2D2D2",
        0, 0.9, "第1个奖励未达标"
    )
    override val UnclaimedReward2 = ColorSchema.parse(
        566, 167, 658, 484, "D2D2D2",
        "72|8|D2D2D2,73|86|D2D2D2,0|97|D2D2D2,-3|184|D2D2D2,74|182|D2D2D2,0|275|D2D2D2,44|277|D2D2D2,71|274|D2D2D2,35|185|D2D2D2",
        0, 0.9, "第2个奖励未达标"
    )
    override val UnclaimedReward3 = ColorSchema.parse(
        679, 166, 773, 484, "D2D2D2",
        "-2|92|D2D2D2,-3|184|D2D2D2,-3|266|D2D2D2,76|267|D2D2D2,73|177|D2D2D2,75|84|D2D2D2,73|-11|D2D2D2,36|91|D2D2D2,36|181|D2D2D2",
        0, 0.9, "第3个奖励未达标"
    )
    override val UnclaimedReward4 = ColorSchema.parse(
        796, 168, 888, 484, "D2D2D2",
        "68|0|D2D2D2,-4|92|D2D2D2,68|89|D2D2D2,-5|181|D2D2D2,73|174|D2D2D2,-2|269|D2D2D2,74|267|D2D2D2,30|178|D2D2D2,32|89|D2D2D2",
        0, 0.9, "第4个奖励未达标"
    )
    override val UnclaimedReward5 = ColorSchema.parse(
        910, 168, 1003, 484, "D2D2D2",
        "76|-3|D2D2D2,-3|87|D2D2D2,72|83|D2D2D2,0|178|D2D2D2,74|172|D2D2D2,-4|265|D2D2D2,75|263|D2D2D2,37|179|D2D2D2,38|87|D2D2D2",
        0, 0.9, "第5个奖励未达标"
    )
    override val UnclaimedReward6 = ColorSchema.parse(
        1026, 168, 1119, 484, "D2D2D2",
        "78|-16|D2D2D2,3|86|D2D2D2,76|81|D2D2D2,1|175|D2D2D2,75|179|D2D2D2,0|264|D2D2D2,75|265|D2D2D2,39|179|D2D2D2,38|90|D2D2D2",
        0, 0.9, "第6个奖励未达标"
    )
    override val ClanGameRewards = ColorSchema.parse(
        453, 186, 526, 206, "59D2D2", "15|0|59D2D2,30|0|59D2D2,44|0|59D2D2,59|0|59D2D2,0|10|59D2D2,15|10|59D2D2,30|10|59D2D2,44|10|59D2D2,59|10|59D2D2", 0, 0.9, "竞赛奖励"
    )
}
