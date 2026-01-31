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

    //以上颜色是夜世界教程颜色
    //前期检测颜色
    val UpgradeToTH6: ColorSchema = ColorSchema.parse(
        700,
        189,
        725,
        221,
        "1919FF-101010",
        "2|-6|1919FF-101010,2|-6|1919FF-101010,6|0|1919FF-101010,8|2|1919FF-101010,11|4|1919FF-101010,7|8|1919FF-101010,0|7|1919FF-101010,0|5|1919FF-101010,1|1|1919FF-101010",
        0,
        0.9,
        "需要将大本营升至6级"
    )
}
