package com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IBuilderBaseObstaclesRemovalColors {
    val CNEditBaseButton: ColorSchema
    val GlobalEditBaseButton: ColorSchema
    val GreenEditBaseButton: ColorSchema
    val EditModeRemoveAll: ColorSchema
    val EditModeRemoveAll2: ColorSchema
}

object BuilderBaseObstaclesRemovalColors : IBuilderBaseObstaclesRemovalColors {
    override val CNEditBaseButton = ColorSchema.parse(
        1050, 630, 1118, 698, "E4F7F5", "1|-3|E4F7F5,5|-6|FFFFFF,11|-28|FFFFFF,11|-32|FFFFFF,24|-8|FFFFFF,28|-8|FFFFFF,29|-6|F5FFFF,27|-11|FFFFFF,29|-11|FFFFFF", 0, 0.9, "编辑阵型按钮"
    )
    override val GlobalEditBaseButton = ColorSchema.parse(
        1196, 412, 1262, 470, "E4F7F5", "1|-3|E4F7F5,5|-6|FFFFFF,11|-28|FFFFFF,11|-32|FFFFFF,24|-8|FFFFFF,28|-8|FFFFFF,29|-6|F5FFFF,27|-11|FFFFFF,29|-11|FFFFFF", 0, 0.9, "编辑阵型按钮"
    )
    override val GreenEditBaseButton = ColorSchema.parse(
        236, 620, 425, 701, "84F8DE", "16|-1|85F8DF,40|-3|89F9E0,60|-1|85F8DF,108|6|7AF6DA,116|48|1FBB6C,87|54|1FBD70,57|49|1FBB6D,0|51|1FBC6E,-8|48|1FBB6C", 0, 0.9, "绿色编辑阵型"
    )
    override val EditModeRemoveAll = ColorSchema.parse(
        1062, 85, 1106, 182, "FFFFFF", "5|2|FFFFFF,18|5|FFFFFF,21|5|FFFFFF,31|-1|81F4D9,17|-5|85F6DD,-4|20|FFFFFF,12|20|FFFFFF,7|27|30B76E,17|26|2FBB72", 0, 0.9, "移除全部"
    )
    override val EditModeRemoveAll2 = ColorSchema.parse(
        1062, 85, 1106, 182, "FFFFFF", "3|0|FFFFFF,5|2|FFFFFF,16|3|FFFFFF,18|5|FFFFFF,10|20|FFFFFF,-6|21|FFFFFF,-5|18|FFFFFF,-4|15|FFFFFF,-5|15|FFFFFF", 0, 0.9, "灰色移除全部"
    )
}
