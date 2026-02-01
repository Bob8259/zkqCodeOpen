package com.coc.zkqcode.jar.code.colorschema

import com.coc.zkqcode.jar.code.colorschema.colorpackage.FeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IFeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IMainBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.INightBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IUIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IWorkerColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.MainBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.NightBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.UIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.WorkerColors

object MyColors : IWorkerColors by WorkerColors, IUIColors by UIColors, IFeatureColors by FeatureColors,
    IMainBaseTutorial by MainBaseTutorial, INightBaseTutorial by NightBaseTutorial {
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

    //以下是训练部队颜色
    val AttackInTrainingPage: ColorSchema = ColorSchema.parse(
        1009,
        615,
        1252,
        665,
        "8DEABE-101010",
        "19|0|8DEABE-101010,51|-1|8EEABF-101010,110|0|8DEABE-101010,125|11|7EE6B4-101010,123|25|3AD38B-101010,99|32|3AD48B-101010,23|26|3AD289-101010,-3|25|3AD38B-101010,-11|23|3AD48B-101010",
        0,
        0.9,
        "训练部队内进攻"
    )
}
