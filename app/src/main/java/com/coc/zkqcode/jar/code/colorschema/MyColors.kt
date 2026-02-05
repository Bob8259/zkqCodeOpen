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
        "88E9BB",
        "39|-2|8BEABD,83|-7|8EEABF,129|-5|8EEABF,130|18|3AD48B,124|26|3AD48B,107|32|39D38A,47|34|39D088,19|30|3AD48B,6|25|3AD48B",
        0,
        0.9,
        "领取宝箱"
    )
    val EditModeWrench: ColorSchema = ColorSchema.parse(
        1197,
        285,
        1257,
        345,
        "FFFFFF-101010",
        "-13|-15|ECFCF8-101010,-1|-20|EFFDFB-101010,4|-21|EFFDFB-101010,13|-21|EFFDFB-101010,18|-17|EDFDF9-101010,22|-10|E9FAF7-101010,6|2|FFFFFF-101010,8|6|FFFFFF-101010,27|5|BDDDDB-101010",
        0,
        0.9,
        "编辑模式扳手"
    )
    val CancelEditMode: ColorSchema = ColorSchema.parse(
        1051,
        484,
        1269,
        537,
        "817CFF-101010",
        "26|2|7D78FF-101010,50|1|7F7AFF-101010,75|1|7F7AFF-101010,85|24|1712E6-101010,64|29|1511DA-101010,40|29|1511DA-101010,-68|9|FFFFFF-101010,-76|15|FFFEF9-101010,-71|27|1611DF-101010",
        0,
        0.9,
        "取消编辑模式"
    )

    //The followings are Night Base Obstacles Removal Colors
    val CNEditBaseButton: ColorSchema = ColorSchema.parse(
        1050,
        630,
        1118,
        698,
        "E4F7F5",
        "1|-3|E4F7F5,5|-6|FFFFFF,11|-28|FFFFFF,11|-32|FFFFFF,24|-8|FFFFFF,28|-8|FFFFFF,29|-6|F5FFFF,27|-11|FFFFFF,29|-11|FFFFFF",
        0,
        0.9,
        "编辑阵型按钮"
    )
    val GlobalEditBaseButton: ColorSchema = ColorSchema.parse(
        1196,
        412,
        1262,
        470,
        "E4F7F5",
        "1|-3|E4F7F5,5|-6|FFFFFF,11|-28|FFFFFF,11|-32|FFFFFF,24|-8|FFFFFF,28|-8|FFFFFF,29|-6|F5FFFF,27|-11|FFFFFF,29|-11|FFFFFF",
        0,
        0.9,
        "编辑阵型按钮"
    )
    val GreenEditBaseButton: ColorSchema = ColorSchema.parse(
        236,
        620,
        425,
        701,
        "84F8DE-101010",
        "16|-1|85F8DF-101010,40|-3|89F9E0-101010,60|-1|85F8DF-101010,108|6|7AF6DA-101010,116|48|1FBB6C-101010,87|54|1FBD70-101010,57|49|1FBB6D-101010,0|51|1FBC6E-101010,-8|48|1FBB6C-101010",
        0,
        0.9,
        "绿色编辑阵型"
    )
    val EditModeRemoveAll: ColorSchema = ColorSchema.parse(
        1043,
        11,
        1269,
        138,
        "FFFFFF-101010",
        "5|2|FFFFFF-101010,18|5|FFFFFF-101010,21|5|FFFFFF-101010,31|-1|81F4D9-101010,17|-5|85F6DD-101010,-4|20|FFFFFF-101010,12|20|FFFFFF-101010,7|27|30B76E-101010,17|26|2FBB72-101010",
        0,
        0.9,
        "移除全部"
    )

    //The followings are Night Base Training Colors
    val RedCleanButton: ColorSchema = ColorSchema.parse(
        973,
        348,
        1156,
        425,
        "8684FF-101010",
        "33|-5|8785FF-101010,75|-5|8785FF-101010,117|-1|8785FF-101010,54|7|FFFFFF-101010,50|16|FFFFFF-101010,57|16|FFFFFF-101010,107|34|221EF7-101010,53|46|221EF6-101010,3|27|221EF7-101010",
        0,
        0.9,
        "清除夜世界部队"
    )
    val TrainNightWitch: ColorSchema = ColorSchema.parse(
        540,
        547,
        635,
        644,
        "605667-101010",
        "10|3|0D0D0D-101010,18|15|34303E-101010,1|20|252430-101010,-9|2|5A515F-101010,-23|-10|121212-101010,-24|-19|2A2628-101010,-8|-25|222120-101010,12|-17|FAE238-101010,-15|-13|F9DF35-101010",
        0,
        0.9,
        "训练暗夜女巫"
    )
}
