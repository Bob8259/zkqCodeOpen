package com.coc.zkqcode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.coc.zkqcode.jar.code.Test
import com.coc.zkqcode.utils.CheckRootScreen

class MainActivity : ComponentActivity() {

    private val test = Test()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        test.startColorDetectionLoop()

        setContent {
            CheckRootScreen()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        test.destroy()
    }
}

