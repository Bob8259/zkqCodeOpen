package com.coc.zkqcode.jar.code.mainbase.daily

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions.swipe
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory

suspend fun mainBaseCheckTutorials(): Boolean {
    val storageKey = StorageKeys.withAccountNumber(StorageKeys.MAIN_BASE_CHECK_TUTORIALS, InGameVars.currentAccountNumber)
    if (!checkMemoryFile(storageKey, 1440)) {
        accountMessage("今天已检查常见教程")
        return true
    }

    accountMessage("准备检测常见教程")
    zoomSmallMainBase()

    // Check the current view first, then scan the lower area after swiping.
    handleTutorialArrowIfNeeded()
    TouchActions.tap(195, 115)//Resources Cart
    TouchActions.tap(640, 560)
    swipe(910, 135, 0, 720)
    handleTutorialArrowIfNeeded()
    TouchActions.tap(140, 325)//Resources Cart
    TouchActions.tap(640, 560)
    // Record the completed daily check so we only scan once per day.
    writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
    return enterMainScreen()
}

private suspend fun handleTutorialArrowIfNeeded() {

    val tutorialArrow = findMultiColorsUntil(
        schemas = listOf(MyColors.MainBaseSmallTutorial), duration = 1500
    ) ?: return

    val xOffsets = (-20..20 step 15).toMutableList()
    if (xOffsets.last() != 20) {
        xOffsets.add(20)
    }
    for (xOffset in xOffsets) {
        for (yOffset in 40..100 step 15) {
            TouchActions.tap(tutorialArrow.x + xOffset, tutorialArrow.y + yOffset)
            val clanMedalTutorial = findMultiColors(MyColors.ClanMedalTutorial)
            if (clanMedalTutorial != null) {
                TouchActions.tap(clanMedalTutorial.x, clanMedalTutorial.y)
            }
            if (findMultiColors(MyColors.OuterPetIcon) != null) {
                petsTutorial()
                return
            }

            if (findMultiColors(MyColors.ClanCastleAddReinforcement) != null) {
                clanCastleTutorialHelper()
            }

            val th18Tutorial = findMultiColorsUntil(schemas = listOf(MyColors.RemoteGuardsIcon, MyColors.MeleeGuardsIcon), duration = 200)
            if (th18Tutorial != null) {
                TouchActions.tap(th18Tutorial.x, th18Tutorial.y)
            }

        }
    }

    delayWithMultiplier(1000)
    clickRightBottom(10)
}

private suspend fun clanCastleTutorialHelper() {
    val clanCastle = findMultiColors(MyColors.ClanCastleAddReinforcement)
    if (clanCastle != null) {
        TouchActions.tap(clanCastle.x, clanCastle.y)
    }
    repeat(10) {
        TouchActions.tap(900, 130, delayTime = 50)//Edit troop. To skip tutorial.
    }
}

private suspend fun petsTutorial() {
    val startTime = System.currentTimeMillis()
    val timeoutMillis = 60_000L

    while (true) {
        // Exit if the pet tutorial does not complete within one minute.
        if (System.currentTimeMillis() - startTime >= timeoutMillis) {
            return
        }
        val remainingSeconds = (timeoutMillis - (System.currentTimeMillis() - startTime)) / 1000
        accountMessage("宠物店教程中，若${remainingSeconds}秒后未完成则强制退出")
        val petsTutorial = findMultiColors(MyColors.OuterPetIcon)
        if (petsTutorial != null) {
            TouchActions.tap(petsTutorial.x, petsTutorial.y)
        }
        val speakingVillager = findMultiColorsUntil(schemas = listOf(MyColors.SpeakingVillager, MyColors.SpeakingVillager2), duration = 200)
        if (speakingVillager != null) {
            TouchActions.tap(speakingVillager.x, speakingVillager.y)
        }
        val innerBanner = findMultiColors(MyColors.PetsShopInnerBanner)
        if (innerBanner != null) {
            completePetsTutorialSelection()
            return
        }
        delayWithMultiplier(500)
    }
}

private suspend fun completePetsTutorialSelection() {
    val tapSequence = listOf(
        Triple(542, 50, 300), // Tap the banner first to avoid tutorial UI bugs.
        Triple(229, 485, 500), // Choose a hero.
        Triple(67, 376, 500) // King Barbarian.
    )

    tapSequence.forEach { (x, y, delayTime) ->
        TouchActions.tap(x, y, delayTime = delayTime)
    }
    clickRightBottom(5)
}