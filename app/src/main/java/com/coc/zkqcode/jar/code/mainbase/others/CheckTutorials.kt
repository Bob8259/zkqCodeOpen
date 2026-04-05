package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen

suspend fun mainBaseCheckTutorials(): Boolean {
    zoomSmallMainBase()
    val tutorialArrow = findMultiColorsUntil(schemas = listOf(MyColors.MainBaseSmallTutorial), duration = 1500)
    if (tutorialArrow != null) {
        TouchActions.tap(tutorialArrow.x + 20, tutorialArrow.y + 60, delayTime = 800)
        val petsTutorial = findMultiColors(MyColors.OuterPetIcon)
        if (petsTutorial != null) {
            petsTutorialHelper()
        }
    }
    return enterMainScreen()
}

suspend fun petsTutorialHelper() {
    while (true) {
        val petsTutorial = findMultiColors(MyColors.OuterPetIcon)
        if (petsTutorial != null) {
            TouchActions.tap(petsTutorial.x, petsTutorial.y, delayTime = 500)
        }
        val speakingVillager = findMultiColorsUntil(schemas = listOf(MyColors.SpeakingVillager, MyColors.SpeakingVillager2), duration = 200)
        if (speakingVillager != null) {
            TouchActions.tap(speakingVillager.x, speakingVillager.y, delayTime = 500)
        }
        val innerBanner = findMultiColors(MyColors.PetsShopInnerBanner)
        if (innerBanner != null) {
            TouchActions.tap(542, 50, delayTime = 300)//tap the banner, to prevent bugs
            TouchActions.tap(229, 485, delayTime = 500)//Choose a hero
            TouchActions.tap(67, 376, delayTime = 500)//King Barbarian
            clickRightBottom(4)
            return
        }
        delayWithMultiplier(500)
    }
}