package com.coc.zkqcode.jar.code.colorschema

import com.coc.zkqcode.jar.code.colorschema.colorpackage.FeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IFeatureColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IMainBaseTraining
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IMainBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IBuilderBaseObstaclesRemovalColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IBuilderBaseResourcesColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IBuilderBaseTrainingColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IBuilderBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IUIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.IWorkerColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.MainBaseTraining
import com.coc.zkqcode.jar.code.colorschema.colorpackage.MainBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.BuilderBaseObstaclesRemovalColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.BuilderBaseResourcesColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.BuilderBaseTrainingColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.BuilderBaseTutorial
import com.coc.zkqcode.jar.code.colorschema.colorpackage.UIColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.WorkerColors

object MyColors : IWorkerColors by WorkerColors, IUIColors by UIColors, IFeatureColors by FeatureColors, IMainBaseTutorial by MainBaseTutorial, IBuilderBaseTutorial by BuilderBaseTutorial,
    IMainBaseTraining by MainBaseTraining, IBuilderBaseResourcesColors by BuilderBaseResourcesColors, IBuilderBaseObstaclesRemovalColors by BuilderBaseObstaclesRemovalColors,
    IBuilderBaseTrainingColors by BuilderBaseTrainingColors {
    //以下是前期检测颜色
    val UpgradeToTH6: ColorSchema = ColorSchema.parse(
        700, 189, 725, 221, "1919FF", "2|-6|1919FF,2|-6|1919FF,6|0|1919FF,8|2|1919FF,11|4|1919FF,7|8|1919FF,0|7|1919FF,0|5|1919FF,1|1|1919FF", 0, 0.9, "需要将大本营升至6级"
    )

    //The followings are Builder Base Upgrade color
    val InnerShopArrow: ColorSchema = ColorSchema.parse(
        161, 263, 1280, 363, "00A3FD", "9|0|00A2FD,18|0|01A0FF,27|0|08A7FF,36|0|13AEFF,0|25|02ABFF,9|25|01ABFE,18|25|01A6FD,27|25|04A1FF,36|25|0C9BFF", 0, 0.9, "商店内部箭头"
    )
    val WallInShop: ColorSchema = ColorSchema.parse(
        618, 401, 658, 453, "EADEAC", "8|0|567188,16|0|5C96CD,24|0|7BDBF3,32|0|EADEAC,0|26|4F9371,8|26|325A47,16|26|47956C,24|26|57B688,32|26|63CD98", 0, 0.9, "商店内部城墙"
    )
    val UpgradeHammer: ColorSchema = ColorSchema.parse(
        146, 499, 1154, 626, "E6E6F3", "11|-8|DEDFEF,12|2|476ECD,18|7|5582F0,21|9|5A82EF,25|10|4F74D5,30|14|5F87F0,38|18|5C7EE3,-4|10|CFDCE7,-2|1|D3D3DE", 0, 0.9, "升级锤子"
    )
    val BuilderBaseInsufficientResources: ColorSchema = ColorSchema.parse(
        560, 520, 1083, 676, "7F88FF", "1|0|7F88FF,2|0|7F88FF,3|0|7F88FF,3|1|7F88FF,2|1|7F88FF,0|1|7F88FF,0|1|7F88FF,0|2|7F88FF,1|2|7F88FF", 0, 0.97, "夜世界升级资源不足"
    )
}
