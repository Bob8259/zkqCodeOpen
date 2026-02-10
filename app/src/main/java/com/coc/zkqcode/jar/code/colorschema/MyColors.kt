package com.coc.zkqcode.jar.code.colorschema

import com.coc.zkqcode.jar.code.colorschema.colorpackage.FeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IFeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IMainBaseTraining
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IMainBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.INightBaseObstaclesRemovalColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.INightBaseResourcesColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.INightBaseTrainingColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.INightBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IUIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IWorkerColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.MainBaseTraining
import com.coc.zkqcode.jar.code.colorschema.colorpackage.MainBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.NightBaseObstaclesRemovalColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.NightBaseResourcesColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.NightBaseTrainingColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.NightBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.UIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.WorkerColors

object MyColors : IWorkerColors by WorkerColors, IUIColors by UIColors, IFeatureColors by FeatureColors, IMainBaseTutorial by MainBaseTutorial, INightBaseTutorial by NightBaseTutorial,
    IMainBaseTraining by MainBaseTraining, INightBaseResourcesColors by NightBaseResourcesColors, INightBaseObstaclesRemovalColors by NightBaseObstaclesRemovalColors,
    INightBaseTrainingColors by NightBaseTrainingColors {
    //以下是前期检测颜色
    val UpgradeToTH6: ColorSchema = ColorSchema.parse(
        700, 189, 725, 221, "1919FF", "2|-6|1919FF,2|-6|1919FF,6|0|1919FF,8|2|1919FF,11|4|1919FF,7|8|1919FF,0|7|1919FF,0|5|1919FF,1|1|1919FF", 0, 0.9, "需要将大本营升至6级"
    )

    //The followings are Night Base Upgrade color
    val InnerShopArrow: ColorSchema = ColorSchema.parse(
        161, 263, 781, 363, "00A3FD", "9|0|00A2FD,18|0|01A0FF,27|0|08A7FF,36|0|13AEFF,0|25|02ABFF,9|25|01ABFE,18|25|01A6FD,27|25|04A1FF,36|25|0C9BFF", 0, 0.9, "商店内部箭头"
    )
    val WallInShop: ColorSchema = ColorSchema.parse(
        618, 401, 658, 453, "EADEAC", "8|0|567188,16|0|5C96CD,24|0|7BDBF3,32|0|EADEAC,0|26|4F9371,8|26|325A47,16|26|47956C,24|26|57B688,32|26|63CD98", 0, 0.9, "商店内部城墙"
    )
    val NewBuildings: ColorSchema = ColorSchema.parse(
        501, 142, 705, 598, "0DFF0D", "3|0|0DFF0D,2|4|0DFD0D,1|7|0DFF0D,2|10|0DFF0D,2|9|0DFF0D,2|5|0DFF0D,2|5|0DFF0D,1|4|0DFF0D,0|4|0DFF0D", 0, 0.9, "新字"
    )

}
