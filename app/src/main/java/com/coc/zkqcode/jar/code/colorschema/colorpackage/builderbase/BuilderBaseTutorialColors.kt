package com.coc.zkqcode.jar.code.colorschema.colorpackage.builderbase

import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IBuilderBaseTutorial {
    val RebuildBoat: ColorSchema
    val UpgradeNightTH: ColorSchema
    val UpgradeBarbarian: ColorSchema
    val BuilderMaster: ColorSchema
    val TutorialNightBarb: ColorSchema
    val NightAttack: ColorSchema
    val NightDeployBarbs: ColorSchema
}

object BuilderBaseTutorial : IBuilderBaseTutorial {
    override val RebuildBoat: ColorSchema = ColorSchema.parse(
        580, 501, 706, 625, "7AF5D9", "21|-5|82F7DD,64|-9|86F8DF,100|-9|86F8DF,122|-7|84F8DD,141|42|1FBE6F,118|49|1FBC6D,87|56|1FBD6F,48|54|1FBC6E,10|50|1FBC6D", 0, 0.9, "绿色重建帆船按钮"
    )
    override val UpgradeNightTH: ColorSchema = ColorSchema.parse(
        523, 573, 770, 691, "66F3D4", "-2|18|55E9C2,2|32|20C379,17|37|1FC379,47|39|20C47B,71|40|20C47C,131|43|20C178,159|21|10C6FD,161|2|57FCFF,170|0|28F8FF", 0, 0.9, "升级夜世界大本营"
    )
    override val UpgradeBarbarian: ColorSchema = ColorSchema.parse(
        943, 617, 1045, 651, "FFFFFF", "21|0|FFFFFF,41|0|E4F8F4,61|0|ED2773,82|0|FF67C4,0|17|21CE8C,21|17|21CD8B,41|17|20CC8A,61|17|20C882,82|17|0D0D0D", 0, 0.9, "升级野蛮人"
    )
    override val BuilderMaster: ColorSchema = ColorSchema.parse(
        880, 362, 1213, 708, "22337A", "6|16|1C2B51,-28|59|204EAC,-34|90|142247,-1|112|122144,47|111|2A3A5F,97|99|6E4527,101|106|6C4426,101|140|6C4425,30|160|16224C", 0, 0.9, "睡醒建筑大师"
    )
    override val TutorialNightBarb: ColorSchema = ColorSchema.parse(
        218, 366, 355, 501, "7574FF", "-5|12|3638B6,10|15|55D3FF,20|30|17253C,14|33|D7FFFF,0|30|95D7FF,-15|21|358CC6,-26|15|177CAE,-27|5|3848A6,-18|-26|48C4FF", 0, 0.9, "夜教程野蛮人"
    )
    override val NightAttack: ColorSchema = ColorSchema.parse(
        33, 580, 133, 689, "99867C", "10|10|456C8F,28|0|9D8B80,36|8|968276,19|17|547D9F,23|24|406485,6|25|406382,-13|3|A29188,-4|-1|9D8A80,34|3|9A867C", 0, 0.9, "夜教程进攻"
    )
    override val NightDeployBarbs: ColorSchema = ColorSchema.parse(
        97, 574, 193, 616, "FF763A", "-7|6|FF773A,-12|0|FF763A,-5|-1|FF763A,3|-1|FF763A,5|4|FF793C,12|6|FF7C3F,16|4|FF7A3E,18|3|FF7A3D,20|-3|FF763A", 0, 0.9, "部署野蛮人"
    )
}
