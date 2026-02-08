package com.coc.zkqcode.jar.code.colorschema

import androidx.compose.ui.graphics.Color
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
import com.coc.zkqcode.jar.code.colorschema.colorpackage.INightBaseObstaclesRemovalColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.NightBaseObstaclesRemovalColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.INightBaseTrainingColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.NightBaseTrainingColors

object MyColors : IWorkerColors by WorkerColors, IUIColors by UIColors, IFeatureColors by FeatureColors,
    IMainBaseTutorial by MainBaseTutorial, INightBaseTutorial by NightBaseTutorial,
    IMainBaseTraining by MainBaseTraining, INightBaseResourcesColors by NightBaseResourcesColors,
    INightBaseObstaclesRemovalColors by NightBaseObstaclesRemovalColors,
    INightBaseTrainingColors by NightBaseTrainingColors {
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
}
