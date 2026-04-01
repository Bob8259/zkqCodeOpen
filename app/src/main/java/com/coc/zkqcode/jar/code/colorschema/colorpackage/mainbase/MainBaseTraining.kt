@file:Suppress("PropertyName")

package com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase

import androidx.compose.ui.graphics.Color
import com.coc.zkqcode.jar.code.colorschema.ColorSchema

interface IMainBaseTraining {
    val TrainingPage: ColorSchema
    val AttackInTrainingPage: ColorSchema
    val AttackInTrainingPage2: ColorSchema
    val AttackInTrainingPage3: ColorSchema
    val DeleteAll1: ColorSchema
    val DeleteAll2: ColorSchema
    val DeleteAll3: ColorSchema
    val MiddleGreenYes: ColorSchema
    val TrainDragon: ColorSchema
    val TrainDragon2: ColorSchema
    val TrainGiant: ColorSchema
    val TrainArcher: ColorSchema
    val TrainBarbarian: ColorSchema
    val GrayBarbarian: ColorSchema
    val TrainLighteningSpell: ColorSchema
    val TrainSiegeMachine: ColorSchema
}

object MainBaseTraining : IMainBaseTraining {
    override val TrainingPage = ColorSchema.parse(
        4, 448, 19, 695, "5E6973", "-3|16|5D6871,-2|37|5E6872,-1|57|606A75,-1|68|5F6A73,1|84|606C76,2|99|5F6974,1|129|5D6971,0|147|5C6871,1|171|57626B", 0, 0.9, "训练部队页面"
    )
    override val AttackInTrainingPage = ColorSchema.parse(
        1009, 615, 1252, 665, "7084A2", "13|-1|6F83A1,50|-2|6E82A0,80|-5|6D809E,110|-5|6D809E,125|1|7184A2,125|6|768AA8,125|20|8298B6,112|26|859BB9,26|22|8399B7", 0, 0.9, "训练部队内进攻"
    )
    override val AttackInTrainingPage2 = ColorSchema.parse(
        1066, 626, 1113, 656, "99FBCD", "9|0|99FBCD,19|0|99FBCD,28|0|99FBCD,37|0|99FBCD,0|15|4CE69D,9|15|4CE69D,19|15|4CE69D,28|15|4CE69D,37|15|4CE69D", 0, 0.9, "训练部队内进攻2"
    )
    override val AttackInTrainingPage3 = ColorSchema.parse(
        1065, 626, 1113, 661, "D2D2D2", "9|0|D2D2D2,19|0|D2D2D2,29|0|D3D3D3,38|0|D3D3D3,0|17|B0B0B0,9|17|B0B0B0,19|17|B0B0B0,29|17|B0B0B0,38|17|B0B0B0", 0, 0.9, "训练部队内进攻3"
    )
    override val DeleteAll1 = ColorSchema.parse(
        1219, 152, 1252, 185, "EDFCFF", "2|0|EDFCFF,5|0|EDFCFF,8|0|EDFCFF,11|0|EDFCFF,13|7|EDFCFF,5|17|D5E4E9,3|17|D5E4E9,-1|7|EDFCFF,-1|5|EDFCFF", 0, 0.9, "删除部队"
    )
    override val DeleteAll2 = ColorSchema.parse(
        959, 323, 996, 366, "EDFCFF", "2|0|EDFCFF,5|0|EDFCFF,8|0|EDFCFF,11|0|EDFCFF,13|7|EDFCFF,5|17|D5E4E9,3|17|D5E4E9,-1|7|EDFCFF,-1|5|EDFCFF", 0, 0.9, "删除法术"
    )
    override val DeleteAll3 = ColorSchema.parse(
        1219, 324, 1252, 366, "EDFCFF", "2|0|EDFCFF,5|0|EDFCFF,8|0|EDFCFF,11|0|EDFCFF,13|7|EDFCFF,5|17|D5E4E9,3|17|D5E4E9,-1|7|EDFCFF,-1|5|EDFCFF", 0, 0.9, "删除机器"
    )
    override val MiddleGreenYes = ColorSchema.parse(
        657, 404, 903, 522, "82F7DD", "23|3|7EF7DA,96|0|82F7DD,147|5|7AF5D9,162|12|70F2D4,145|48|1FBD6E,114|52|1FBC6D,57|63|1FBD70,38|58|1FBC6E,12|55|1FBC6D", 0, 0.9, "绿色中间确定"
    )
    override val TrainDragon = ColorSchema.parse(
        18, 416, 1275, 710, "C95369", "4|-12|F97389,19|-6|D26275,20|5|893F4E,11|12|733071,6|23|3C3276,-3|24|737681,-7|9|3A2F85,8|2|D35D6B,25|3|7F3C4F", 0, 0.9, "训练飞龙"
    )
    override val TrainDragon2 = ColorSchema.parse(
        18, 416, 1275, 710, "DA758A", "6|0|F06D83,12|0|F67286,18|0|F47288,24|0|F46E83,0|26|2E1178,6|26|362B84,12|26|7445FF,18|26|6D45E3,24|26|703DB2", 0, 0.9, "训练飞龙2"
    )
    override val TrainGiant = ColorSchema.parse(
        18, 416, 1275, 710, "5578C6", "-18|-14|4A609E,-12|-23|5E71B2,1|-22|86BDFF,19|-22|5F8CED,23|-21|7CADFF,24|-13|85BCFF,15|7|93CBFF,1|3|84B8FF,-12|-4|8287B9", 0, 0.9, "训练巨人"
    )
    override val TrainArcher = ColorSchema.parse(
        18, 416, 1275, 710, "663BAD", "3|14|7D98F3,-21|18|758BDE,-33|9|7D81C0,-54|-13|E37AFB,-51|-25|C563EC,-39|-39|BD5EF9,-23|-32|9149D9,-9|-24|733AC8,8|-15|7739D5", 0, 0.9, "训练弓箭手"
    )
    override val TrainBarbarian = ColorSchema.parse(
        18, 416, 1275, 710, "37527E", "-18|-9|677DA8,-23|-27|53BAD8,-17|-43|44B5E1,-3|-44|3EB8F5,6|-32|47CAFF,9|-12|6FAAFF,0|2|357FAC,12|8|3F4D82,30|5|6EF3FF", 0, 0.9, "训练野蛮人"
    )
    override val GrayBarbarian = ColorSchema.parse(
        24, 430, 695, 690, "A9A9A9", "9|11|CDCDCD,30|11|D3D3D3,33|5|D2D2D2,27|-10|B8B8B8,27|-22|C3C3C3,18|-30|C3C3C3,8|-30|BEBEBE,-7|-32|B5B5B5,-18|-30|C1C1C1", 0, 0.9, "已练满"
    )
    override val TrainLighteningSpell = ColorSchema.parse(
        24, 430, 695, 690, "FFF14B", "12|2|FFFFFF,20|15|FFE10F,4|11|FFD747,7|-10|FFFFE5,9|-21|FFFFDB,6|-31|356B97,-3|-35|798584,-17|-28|FFC44F,-34|-24|FFFD1C", 0, 0.9, "训练法术"
    )
    override val TrainSiegeMachine = ColorSchema.parse(
        82, 509, 198, 567, "2B2D7F", "23|0|292E8C,46|0|404BBD,69|0|254687,92|0|111243,0|29|212053,23|29|191E68,46|29|181A4B,69|29|2C57A9,92|29|325595", 0, 0.9, "训练攻城机器"
    )
}
