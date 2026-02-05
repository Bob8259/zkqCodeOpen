package com.coc.zkqcode.jar.code.colorschema

import com.coc.zkqcode.jar.code.colorschema.colorpackage.FeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IFeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IMainBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.INightBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IUIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IWorkerColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IMainBaseTraining
import com.coc.zkqcode.jar.code.colorschema.colorpackage.MainBaseTraining
import com.coc.zkqcode.jar.code.colorschema.colorpackage.MainBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.NightBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.UIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.WorkerColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.INightBaseResourcesColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.NightBaseResourcesColors

object MyColors : IWorkerColors by WorkerColors, IUIColors by UIColors, IFeatureColors by FeatureColors,
    IMainBaseTutorial by MainBaseTutorial, INightBaseTutorial by NightBaseTutorial,
    IMainBaseTraining by MainBaseTraining, INightBaseResourcesColors by NightBaseResourcesColors {
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

    //The followings are UI Colors
    val CollectChest: ColorSchema = ColorSchema.parse(
        547,
        569,
        735,
        627,
        "88E9BB-101010",
        "39|-2|8BEABD-101010,83|-7|8EEABF-101010,129|-5|8EEABF-101010,130|18|3AD48B-101010,124|26|3AD48B-101010,107|32|39D38A-101010,47|34|39D088-101010,19|30|3AD48B-101010,6|25|3AD48B-101010",
        0,
        0.9,
        "领取宝箱"
    )


    
}
