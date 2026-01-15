package com.coc.zkqcode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.coc.zkqcode.utils.CheckRootScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.coc.zkqcode.utils.basic.ShowMessage.init(this)
        setContent {
            CheckRootScreen()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
