package com.coc.zkqcode.jar.code.colorschema

import com.coc.zkqcode.jar.code.colorschema.colorpackage.FeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IFeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IMainBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IUIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IWorkerColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.MainBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.UIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.WorkerColors

object MyColors : IWorkerColors by WorkerColors, IUIColors by UIColors, IFeatureColors by FeatureColors,
    IMainBaseTutorial by MainBaseTutorial {
    //以下颜色是夜世界教程颜色
    val RebuildBoat: ColorSchema = ColorSchema.parse(
        580,
        501,
        706,
        625,
        "7AF5D9",
        "21|-5|82F7DD,64|-9|86F8DF,100|-9|86F8DF,122|-7|84F8DD,141|42|1FBE6F,118|49|1FBC6D,87|56|1FBD6F,48|54|1FBC6E,10|50|1FBC6D",
        0,
        0.9,
        "绿色重建帆船按钮"
    )
    val UpgradeNightTH: ColorSchema = ColorSchema.parse(
        523,
        573,
        770,
        691,
        "66F3D4",
        "-2|18|55E9C2,2|32|20C379,17|37|1FC379,47|39|20C47B,71|40|20C47C,131|43|20C178,159|21|10C6FD,161|2|57FCFF,170|0|28F8FF",
        0,
        0.9,
        "升级夜世界大本营"
    )
    val BuilderMaster: ColorSchema = ColorSchema.parse(
        880,
        362,
        1213,
        708,
        "22337A",
        "6|16|1C2B51,-28|59|204EAC,-34|90|142247,-1|112|122144,47|111|2A3A5F,97|99|6E4527,101|106|6C4426,101|140|6C4425,30|160|16224C",
        0,
        0.9,
        "睡醒建筑大师"
    )
    val TutorialNightBarb: ColorSchema = ColorSchema.parse(
        218,
        366,
        355,
        501,
        "7574FF",
        "-5|12|3638B6,10|15|55D3FF,20|30|17253C,14|33|D7FFFF,0|30|95D7FF,-15|21|358CC6,-26|15|177CAE,-27|5|3848A6,-18|-26|48C4FF",
        0,
        0.9,
        "夜教程野蛮人"
    )
    val NightAttack: ColorSchema = ColorSchema.parse(
        33,
        580,
        133,
        689,
        "99867C-101010",
        "10|10|456C8F-101010,28|0|9D8B80-101010,36|8|968276-101010,19|17|547D9F-101010,23|24|406485-101010,6|25|406382-101010,-13|3|A29188-101010,-4|-1|9D8A80-101010,34|3|9A867C-101010",
        0,
        0.9,
        "夜教程进攻"
    )
    val NightDeployBarbs: ColorSchema = ColorSchema.parse(
        97,
        574,
        193,
        616,
        "FF763A-101010",
        "-7|6|FF773A-101010,-12|0|FF763A-101010,-5|-1|FF763A-101010,3|-1|FF763A-101010,5|4|FF793C-101010,12|6|FF7C3F-101010,16|4|FF7A3E-101010,18|3|FF7A3D-101010,20|-3|FF763A-101010",
        0,
        0.9,
        "部署野蛮人"
    )
    //以上颜色是夜世界教程颜色
    //以下是前期检测颜色

    val UpgradeToTH6: ColorSchema = ColorSchema.parse(
        700,
        189,
        725,
        221,
        "1919FF",
        "2|-6|1919FF,2|-6|1919FF,6|0|1919FF,8|2|1919FF,11|4|1919FF,7|8|1919FF,0|7|1919FF,0|5|1919FF,1|1|1919FF",
        0,
        0.9,
        "需要将大本营升至6级"
    )

    //以下是回到主界面关闭广告的检测
    val ReturnAwards: ColorSchema = ColorSchema.parse(
        110,
        44,
        1173,
        679,
        "00528D",
        "116|-2|00518B,-68|491|00CCF3,-78|517|00DCFA,-78|523|00DDFA,801|536|00DDFA,840|529|009BD8,846|515|008AED,840|399|00437A,839|354|004279",
        0,
        0.9, "回归奖励"
    )
    val NightBackToCamp: ColorSchema = ColorSchema.parse(
        553,
        574,
        728,
        648,
        "8BEABD-101010",
        "18|-1|8CEABE-101010,46|-1|8CEABE-101010,68|0|8BEABD-101010,80|2|88E9BB-101010,89|35|3AD48B-101010,62|38|3AD38B-101010,35|32|3AD48B-101010,22|34|3AD48B-101010,-16|29|3AD48B-101010",
        0,
        0.9,
        "回营"
    )
    val MainBackToCamp: ColorSchema = ColorSchema.parse(
        554,
        578,
        726,
        658,
        "79F6D9-101010",
        "40|-5|83F8DD-101010,69|-7|86F8DF-101010,88|8|69F2D0-101010,97|11|64EFCB-101010,96|33|1EBB6C-101010,79|37|1FBB6C-101010,39|35|1FBB6C-101010,18|35|1FBB6C-101010,0|32|1FBC6D-101010",
        0,
        0.9,
        "主世界回营"
    )
}
