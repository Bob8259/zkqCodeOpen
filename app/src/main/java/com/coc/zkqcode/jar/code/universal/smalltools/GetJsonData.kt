package com.coc.zkqcode.jar.code.universal.smalltools

suspend fun getJsonData() {
    if (current game version is not CN) {
        TouchActions.tap(1230, 520)//settings
        TouchActions.tap(630, 610)//more settings
        TouchActions.swipe(630, 610, 630, -400, delayAfter = 200)
        TouchActions.tap(910, 450)//copy
    }else if(current game version is CN){
        TouchActions.tap(1230, 450)//arrow
        TouchActions.tap(1210, 250)//settings
        TouchActions.tap(630, 610)//more settings
        TouchActions.swipe(630, 610, 630, -400, delayAfter = 200)
        TouchActions.tap(900, 300)//copy
    }
}