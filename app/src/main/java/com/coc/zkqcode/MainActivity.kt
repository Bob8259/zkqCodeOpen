package com.coc.zkqcode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.coc.zkqcode.utils.CheckRootScreen
import com.coc.zkqcode.zkqnative.NativeTools

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CheckRootScreen()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

