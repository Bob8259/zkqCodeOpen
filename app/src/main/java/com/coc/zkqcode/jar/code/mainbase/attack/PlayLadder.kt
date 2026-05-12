package com.coc.zkqcode.jar.code.mainbase.attack

import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.killGame
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.runGame
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun playLadder(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.PLAY_LADDER.key)) return true

    // Guard: only check ladder once per day (1440 minutes)
    val storageKey = StorageKeys.withAccountNumber(StorageKeys.PLAY_LADDER, InGameVars.currentAccountNumber)
    if (!checkMemoryFile(storageKey, 1440)) {
        accountMessage("今日已检测排位赛，暂不重复执行")
        return true
    }
    val startTime = System.currentTimeMillis()
    val maxDuration = 2 * 60 * 1000L // 2 minutes
    while (System.currentTimeMillis() - startTime < maxDuration) {
        val battleIcon = findMultiColorsUntil(schemas = listOf(MyColors.TrainTroops), duration = 500)
        if (battleIcon != null) {
            TouchActions.tap(80, 630)
        }
        val joinTournament = findMultiColors(MyColors.JoinTournament)
        if (joinTournament != null) {
            TouchActions.tap(joinTournament.x, joinTournament.y)
            TouchActions.tap(865, 570)//Register
        }
        listOf(MyColors.PlayTournament, MyColors.AttackButton, MyColors.IncreaseRank, MyColors.ConfirmTournamentAttack).forEach { schema ->
            findMultiColors(schema = schema)?.let { TouchActions.tap(it.x, it.y) }
        }
        if (findMultiColors(MyColors.WaitForTournament) != null) {
            break
        }
        if (findMultiColors(MyColors.GreenSword)!=null){
            accountMessage("排位赛已完成")
            writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
            break
        }
        val endBattle = findMultiColors(schema = MyColors.EndBattle)
        if (endBattle != null) {
            accountMessage("已进入对战")
            mainBaseDeployTroops()
            killGame()
            runGame()
            break
        }
    }
    return enterMainScreen()
}