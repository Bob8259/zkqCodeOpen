package com.coc.zkqcode.jar.code.colorschema

import androidx.compose.ui.graphics.Color
import com.coc.zkqcode.jar.code.colorschema.colorpackage.FeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IFeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IUIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IWorkerColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.UIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.WorkerColors

object MyColors :
    IWorkerColors by WorkerColors,
    IUIColors by UIColors,
    IFeatureColors by FeatureColors {
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
        0.9, "村民说话框"
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
        76,
        542,
        617,
        608,
        "00ABFD",
        "-15|4|02AAFE,-16|-32|0BA3FF,-11|-50|29B8FF,0|-54|2BB6FF,10|-51|29B4FF,15|-32|0BA1FF,17|2|05ABFF,5|11|04ABFF,-6|16|07ACFF",
        0,
        0.9,
        "商店箭头"
    )
    val ShopInnerArrow: ColorSchema = ColorSchema.parse(
        694,
        257,
        843,
        388,
        "00A2FE",
        "1|25|02AAFD,-21|31|0CADFF,-34|5|12B8FF,-12|-19|0896FF,5|-13|09A6FF,21|-36|4EB4FF,33|-26|4EB8FF,38|-15|4EB9FF,18|0|09A5FF",
        0,
        0.9,
        "商店内部箭头"
    )
    val TutorialBuildClick: ColorSchema = ColorSchema.parse(
        460,
        155,
        667,
        327,
        "FFFFFF-101010",
        "9|-13|FFFFFF-101010,21|-1|1DD25B-101010,21|9|15B846-101010,13|13|14B746-101010,-5|17|15BF51-101010,-19|18|15C359-101010,-22|13|14BA4A-101010,-13|13|14B746-101010,-3|16|15BC4C-101010",
        0,
        0.9,
        "教程建造绿色箭头"
    )
    //以上颜色是主世界教程颜色
}
