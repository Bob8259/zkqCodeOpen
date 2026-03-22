package com.coc.zkqcode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.coc.zkqcode.core.system.checkpermissions.CheckRootScreen
import com.coc.zkqcode.core.system.screencapture.ProjectionPermissionHelper
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.fileactions.LogHelper
import timber.log.Timber

class MainActivity : ComponentActivity() {
    private lateinit var projectionPermissionHelper: ProjectionPermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectionPermissionHelper = ProjectionPermissionHelper(this)
        ShowMessage.init(this)
        System.loadLibrary("rust_logic")
        ScreenCaptureManager.init(this)
        LogHelper.initTimber(this)
        Timber.v("MainActivity Start!")
        setContent {
            CheckRootScreen()
        }

    }

    fun requestMediaProjection() {
        projectionPermissionHelper.requestMediaProjection()
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}

