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
        "8BEABD",
        "18|-1|8CEABE,46|-1|8CEABE,68|0|8BEABD,80|2|88E9BB,89|35|3AD48B,62|38|3AD38B,35|32|3AD48B,22|34|3AD48B,-16|29|3AD48B",
        0,
        0.9,
        "回营"
    )
    val MainBackToCamp: ColorSchema = ColorSchema.parse(
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
    val TrainingPage: ColorSchema = ColorSchema.parse(
        4,
        448,
        19,
        695,
        "5E6973-101010",
        "-3|16|5D6871-101010,-2|37|5E6872-101010,-1|57|606A75-101010,-1|68|5F6A73-101010,1|84|606C76-101010,2|99|5F6974-101010,1|129|5D6971-101010,0|147|5C6871-101010,1|171|57626B-101010",
        0,
        0.9,
        "训练部队页面"
    )

    //以下是训练部队颜色
    val AttackInTrainingPage: ColorSchema = ColorSchema.parse(
        1009,
        615,
        1252,
        665,
        "7084A2",
        "13|-1|6F83A1,50|-2|6E82A0,80|-5|6D809E,110|-5|6D809E,125|1|7184A2,125|6|768AA8,125|20|8298B6,112|26|859BB9,26|22|8399B7",
        0,
        0.9,
        "训练部队内进攻"
    )
    val DeleteAll1: ColorSchema = ColorSchema.parse(
        1219,
        152,
        1252,
        185,
        "EDFCFF",
        "2|0|EDFCFF,5|0|EDFCFF,8|0|EDFCFF,11|0|EDFCFF,13|7|EDFCFF,5|17|D5E4E9,3|17|D5E4E9,-1|7|EDFCFF,-1|5|EDFCFF",
        0,
        0.9,
        "删除部队"
    )
    val DeleteAll2: ColorSchema = ColorSchema.parse(
        959, 323, 996, 366,
        "EDFCFF",
        "2|0|EDFCFF,5|0|EDFCFF,8|0|EDFCFF,11|0|EDFCFF,13|7|EDFCFF,5|17|D5E4E9,3|17|D5E4E9,-1|7|EDFCFF,-1|5|EDFCFF",
        0,
        0.9, "删除法术"
    )
    val DeleteAll3: ColorSchema = ColorSchema.parse(
        1219, 324, 1252, 366,
        "EDFCFF",
        "2|0|EDFCFF,5|0|EDFCFF,8|0|EDFCFF,11|0|EDFCFF,13|7|EDFCFF,5|17|D5E4E9,3|17|D5E4E9,-1|7|EDFCFF,-1|5|EDFCFF",
        0,
        0.9, "删除机器"
    )
    val MiddleGreenYes: ColorSchema = ColorSchema.parse(
        657,
        404,
        903,
        522,
        "82F7DD",
        "23|3|7EF7DA,96|0|82F7DD,147|5|7AF5D9,162|12|70F2D4,145|48|1FBD6E,114|52|1FBC6D,57|63|1FBD70,38|58|1FBC6E,12|55|1FBC6D",
        0,
        0.9,
        "绿色中间确定"
    )
    val TrainDragon: ColorSchema = ColorSchema.parse(
        18,
        416,
        1275,
        710,
        "C95369",
        "4|-12|F97389,19|-6|D26275,20|5|893F4E,11|12|733071,6|23|3C3276,-3|24|737681,-7|9|3A2F85,8|2|D35D6B,25|3|7F3C4F",
        0,
        0.9,
        "训练飞龙"
    )
    val TrainGiant: ColorSchema = ColorSchema.parse(
        18,
        416,
        1275,
        710,
        "5578C6",
        "-18|-14|4A609E,-12|-23|5E71B2,1|-22|86BDFF,19|-22|5F8CED,23|-21|7CADFF,24|-13|85BCFF,15|7|93CBFF,1|3|84B8FF,-12|-4|8287B9",
        0,
        0.9,
        "训练巨人"
    )
    val TrainArcher: ColorSchema = ColorSchema.parse(
        18,
        416,
        1275,
        710,
        "663BAD",
        "3|14|7D98F3,-21|18|758BDE,-33|9|7D81C0,-54|-13|E37AFB,-51|-25|C563EC,-39|-39|BD5EF9,-23|-32|9149D9,-9|-24|733AC8,8|-15|7739D5",
        0,
        0.9,
        "训练弓箭手"
    )
    val TrainBarbarian: ColorSchema = ColorSchema.parse(
        18,
        416,
        1275,
        710,
        "37527E",
        "-18|-9|677DA8,-23|-27|53BAD8,-17|-43|44B5E1,-3|-44|3EB8F5,6|-32|47CAFF,9|-12|6FAAFF,0|2|357FAC,12|8|3F4D82,30|5|6EF3FF",
        0,
        0.9,
        "训练野蛮人"
    )
    val GrayBarbarian: ColorSchema = ColorSchema.parse(
        24,
        430,
        695,
        690,
        "A9A9A9-101010",
        "9|11|CDCDCD-101010,30|11|D3D3D3-101010,33|5|D2D2D2-101010,27|-10|B8B8B8-101010,27|-22|C3C3C3-101010,18|-30|C3C3C3-101010,8|-30|BEBEBE-101010,-7|-32|B5B5B5-101010,-18|-30|C1C1C1-101010",
        0,
        0.9,
        "已练满"
    )
    val TrainLighteningSpell: ColorSchema = ColorSchema.parse(
        24,
        430,
        695,
        690,
        "FFF14B-101010",
        "12|2|FFFFFF-101010,20|15|FFE10F-101010,4|11|FFD747-101010,7|-10|FFFFE5-101010,9|-21|FFFFDB-101010,6|-31|356B97-101010,-3|-35|798584-101010,-17|-28|FFC44F-101010,-34|-24|FFFD1C-101010",
        0,
        0.9,
        "训练法术"
    )
    val TrainSiegeMachine: ColorSchema = ColorSchema.parse(
        45,
        457,
        246,
        628,
        "2E4781-101010",
        "4|18|264C94-101010,-19|29|19274E-101010,-56|21|3259A5-101010,-69|-8|2E2F78-101010,-69|-13|393881-101010,-53|-35|2A2F8B-101010,-35|-43|2B318F-101010,-6|-46|242B8C-101010,24|-53|2C57A4-101010",
        0,
        0.9,
        "训练攻城机器"
    )
}
