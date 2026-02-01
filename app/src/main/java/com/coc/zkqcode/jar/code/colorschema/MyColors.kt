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
    val DeleteAll1: ColorSchema = ColorSchema.parse(
        1219,
        152,
        1252,
        185,
        "EDFCFF-101010",
        "2|0|EDFCFF-101010,5|0|EDFCFF-101010,8|0|EDFCFF-101010,11|0|EDFCFF-101010,13|7|EDFCFF-101010,5|17|D5E4E9-101010,3|17|D5E4E9-101010,-1|7|EDFCFF-101010,-1|5|EDFCFF-101010",
        0,
        0.9,
        "删除部队"
    )
    val DeleteAll2: ColorSchema = ColorSchema.parse(
        959, 323, 996, 366,
        "EDFCFF-101010",
        "2|0|EDFCFF-101010,5|0|EDFCFF-101010,8|0|EDFCFF-101010,11|0|EDFCFF-101010,13|7|EDFCFF-101010,5|17|D5E4E9-101010,3|17|D5E4E9-101010,-1|7|EDFCFF-101010,-1|5|EDFCFF-101010",
        0,
        0.9, "删除法术"
    )
    val DeleteAll3: ColorSchema = ColorSchema.parse(
        1219, 324, 1252, 366,
        "EDFCFF-101010",
        "2|0|EDFCFF-101010,5|0|EDFCFF-101010,8|0|EDFCFF-101010,11|0|EDFCFF-101010,13|7|EDFCFF-101010,5|17|D5E4E9-101010,3|17|D5E4E9-101010,-1|7|EDFCFF-101010,-1|5|EDFCFF-101010",
        0,
        0.9, "删除机器"
    )
    val MiddleGreenYes: ColorSchema = ColorSchema.parse(
        657,
        404,
        903,
        522,
        "82F7DD-101010",
        "23|3|7EF7DA-101010,96|0|82F7DD-101010,147|5|7AF5D9-101010,162|12|70F2D4-101010,145|48|1FBD6E-101010,114|52|1FBC6D-101010,57|63|1FBD70-101010,38|58|1FBC6E-101010,12|55|1FBC6D-101010",
        0,
        0.9,
        "绿色中间确定"
    )
    val TrainDragon: ColorSchema = ColorSchema.parse(
        18,
        416,
        1275,
        710,
        "C95369-101010",
        "4|-12|F97389-101010,19|-6|D26275-101010,20|5|893F4E-101010,11|12|733071-101010,6|23|3C3276-101010,-3|24|737681-101010,-7|9|3A2F85-101010,8|2|D35D6B-101010,25|3|7F3C4F-101010",
        0,
        0.9,
        "训练飞龙"
    )
    val TrainGiant: ColorSchema = ColorSchema.parse(
        18,
        416,
        1275,
        710,
        "5578C6-101010",
        "-18|-14|4A609E-101010,-12|-23|5E71B2-101010,1|-22|86BDFF-101010,19|-22|5F8CED-101010,23|-21|7CADFF-101010,24|-13|85BCFF-101010,15|7|93CBFF-101010,1|3|84B8FF-101010,-12|-4|8287B9-101010",
        0,
        0.9,
        "训练巨人"
    )
    val TrainArcher: ColorSchema = ColorSchema.parse(
        18,
        416,
        1275,
        710,
        "663BAD-101010",
        "3|14|7D98F3-101010,-21|18|758BDE-101010,-33|9|7D81C0-101010,-54|-13|E37AFB-101010,-51|-25|C563EC-101010,-39|-39|BD5EF9-101010,-23|-32|9149D9-101010,-9|-24|733AC8-101010,8|-15|7739D5-101010",
        0,
        0.9,
        "训练弓箭手"
    )
    val TrainBarbarian: ColorSchema = ColorSchema.parse(
        18,
        416,
        1275,
        710,
        "37527E-101010",
        "-18|-9|677DA8-101010,-23|-27|53BAD8-101010,-17|-43|44B5E1-101010,-3|-44|3EB8F5-101010,6|-32|47CAFF-101010,9|-12|6FAAFF-101010,0|2|357FAC-101010,12|8|3F4D82-101010,30|5|6EF3FF-101010",
        0,
        0.9,
        "训练野蛮人"
    )
}
