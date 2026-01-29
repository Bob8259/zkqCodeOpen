package com.coc.zkqcode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.coc.zkqcode.core.system.checkpermissions.CheckRootScreen
import com.coc.zkqcode.core.system.screencapture.ProjectionPermissionHelper
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.fileactions.LogHelper
import com.coc.zkqcode.nativehelper.RustTools
import timber.log.Timber

class MainActivity : ComponentActivity() {
    private lateinit var projectionPermissionHelper: ProjectionPermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectionPermissionHelper = ProjectionPermissionHelper(this)
        ShowMessage.init(this)
        ScreenCaptureManager.init(this)
        LogHelper.initTimber(this)
        Timber.v("MainActivity Start!")
        setContent {
            CheckRootScreen()
//            GreetingScreen()
        }

    }

    fun requestMediaProjection() {
        projectionPermissionHelper.requestMediaProjection()
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}

@Composable
fun GreetingScreen() {
    val message = remember { RustTools.sayHello("Android 开发者") }
    Text(text = message)
}
