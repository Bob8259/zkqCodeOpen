package com.coc.zkqcode.jar.code.colorschema

import androidx.compose.ui.graphics.Color
import com.coc.zkqcode.jar.code.colorschema.colorpackage.FeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IFeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IUIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IWorkerColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.UIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.WorkerColors

object MyColors : IWorkerColors by WorkerColors, IUIColors by UIColors, IFeatureColors by FeatureColors {
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

    //以上颜色是夜世界教程颜色
    //以下颜色是主世界教程颜色
    val ImportantNotice: ColorSchema = ColorSchema.parse(
        252,
        142,
        1024,
        562,
        "1B1B1C",
        "84|3|1B1B1C,184|-3|1B1B1C,279|-4|1B1B1C,317|-8|1B1B1C,336|-91|1B1B1C,330|-157|1B1B1C,312|-247|1B1B1C,174|-254|1B1B1C,87|-253|1B1B1C",
        0,
        0.9,
        "重要提示"
    )
    val SpeakingVillager: ColorSchema = ColorSchema.parse(
        277,
        313,
        674,
        517,
        "FFFFFF",
        "56|-1|FFFFFF,157|-3|FFFFFF,252|-7|62ACDE,274|-3|68ACDD,275|20|85CFDD,275|101|6AAFE1,-30|108|66AEDE,-48|104|66AADE,-49|5|9EE3FF",
        0,
        0.9,
        "村民说话框"
    )
    val SpeakingVillager2: ColorSchema = ColorSchema.parse(
        238,
        295,
        665,
        516,
        "67AFDE",
        "10|-1|69B1DF,16|0|89B0D4,293|0|66AEDE,311|4|66AADD,294|162|FFFFFF,4|153|FFFFFF,-5|132|FFFFFF,-35|95|FFFFFF,-41|89|FFFFFF",
        0,
        0.9,
        "村民说话框2"
    )
    val EnterAge: ColorSchema = ColorSchema.parse(
        417,
        205,
        847,
        602,
        "5E56DA",
        "0|17|3329CF,0|26|3329CF,264|-78|7B808D,266|-93|777D89,269|-166|7B818F,269|-186|777D89,266|-263|7B808E,262|-273|797E8B,129|-280|777D89",
        0,
        0.9,
        "输入年龄"
    )
    val PrivacyInfo: ColorSchema = ColorSchema.parse(
        76,
        542,
        617,
        608,
        "B8CACB",
        "-79|8|B8CACB,-52|-16|B8CACB,121|-17|B8CACB,191|-9|B8CACB,230|16|B8CACB,229|26|B8CACB,243|34|B8CACB,263|27|B8CACB,329|30|B8CACB",
        0,
        0.9,
        "拒绝优化"
    )
    val ShopArrow: ColorSchema = ColorSchema.parse(
        1129,
        472,
        1268,
        610,
        "09A7FD",
        "0|-13|1DB9FD,12|-5|0CA6FD,20|12|14AFFB,31|13|1BAEFC,14|25|32D0F6,4|31|3DD8F5,-3|34|42DBF6,-14|35|47DFF5,-17|-3|10A1FC",
        0,
        0.9,
        "商店箭头"
    )
    val ShopInnerArrow: ColorSchema = ColorSchema.parse(
        117,
        256,
        843,
        388,
        "00A2FE",
        "1|25|02AAFD,-21|31|0CADFF,-34|5|12B8FF,-12|-19|0896FF,5|-13|09A6FF,21|-36|4EB4FF,33|-26|4EB8FF,38|-15|4EB9FF,18|0|09A5FF",
        0,
        0.9,
        "商店内部箭头"
    )
    val TutorialBuildClick: ColorSchema = ColorSchema.parse(
        140,
        27,
        1074,
        608,
        "FFFFFF",
        "9|-13|FFFFFF,21|-1|1DD25B,21|9|15B846,13|13|14B746,-5|17|15BF51,-19|18|15C359,-22|13|14BA4A,-13|13|14B746,-3|16|15BC4C",
        0,
        0.9,
        "教程建造绿色箭头"
    )
    val TutorialGoblinAttack: ColorSchema = ColorSchema.parse(
        629,
        548,
        818,
        617,
        "77F4D7",
        "34|-4|7FF7DC,74|-5|81F7DD,91|-4|7FF7DB,105|9|63EECA,113|29|1FBB6C,107|34|1EBB6D,47|37|1FBD71,14|33|1FBB6D,-3|28|1FBB6C",
        0,
        0.9,
        "教程哥布林进攻"
    )
    val VillagerAttack: ColorSchema = ColorSchema.parse(
        364,
        411,
        572,
        494,
        "7DF7DA",
        "32|-3|83F8DD,66|-4|85F8DF,99|-3|83F8DE,114|8|6BF2D0,109|35|1FBB6C,73|41|20BF73,35|37|1FBB6D,10|33|1FBB6C,-7|29|1FBC6D",
        0,
        0.9,
        "村民建议进攻哥布林"
    )
    val TutorialBlueTroop: ColorSchema = ColorSchema.parse(
        98,586,207,619,"C08545-101010","1|-2|C78948-101010,8|-9|DD9853-101010,13|-3|CB8E4D-101010,13|-2|C88C4C-101010,9|-3|CA8D4C-101010,5|-6|D4924F-101010,-1|-3|CA8B4A-101010,-3|0|C08545-101010,-4|-6|D3914E-101010",0,0.9
,
        "蓝色部队标志"
    )
    val TutorialTrain: ColorSchema = ColorSchema.parse(
        654,
        503,
        771,
        622,
        "F1EDF8-101010",
        "8|6|EDE8EF-101010,18|15|E9DCDC-101010,26|21|1D476B-101010,27|28|264873-101010,32|31|27649E-101010,17|28|347FAC-101010,26|17|3A8AB7-101010,6|13|D3A68A-101010,-6|2|C29B83-101010",
        0,
        0.9,
        "训练部队"
    )
    val TutorialTrainInner: ColorSchema = ColorSchema.parse(
        18,
        105,
        368,
        176,
        "405786-101010",
        "29|-3|415887-101010,42|-5|415887-101010,65|-8|415A89-101010,85|-9|415A88-101010,115|-6|415988-101010,139|-6|415988-101010,105|28|405887-101010,73|33|415987-101010,31|30|405887-101010",
        0,
        0.9,
        "训练部队内部"
    )
    val AttackMap: ColorSchema = ColorSchema.parse(
        22,
        580,
        144,
        674,
        "D5F0FF-101010",
        "-14|2|C2E5F8-101010,-20|-14|D6F1FF-101010,0|-26|DAEDF9-101010,15|-23|9EA8F5-101010,21|-18|A9C9DE-101010,23|-13|B5CFDD-101010,18|-5|B6D0FC-101010,-3|7|486BE2-101010,3|15|E0F5FF-101010",
        0,
        0.9,
        "进攻地图"
    )
    val AttackGoblin: ColorSchema= ColorSchema.parse(349,470,520,538,"53C7FF-101010","20|-4|54CAFF-101010,44|-8|56CBFF-101010,65|-7|55CBFF-101010,77|-1|53C7FF-101010,87|9|4ABDFF-101010,87|22|145EF1-101010,74|27|1256EE-101010,37|36|1250EC-101010,10|33|1251ED-101010",0,0.9
    ,"哥布林森林")
    //以上颜色是主世界教程颜色
}
