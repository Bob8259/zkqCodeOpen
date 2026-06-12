package com.coc.zkqcode

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.coc.zkqcode.core.system.checkpermissions.CheckRootScreen
import com.coc.zkqcode.core.system.screencapture.ProjectionPermissionHelper
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.fileactions.LogHelper
import com.coc.zkqcode.core.util.exit.AppExitHelper

class MainActivity : ComponentActivity() {
    private lateinit var projectionPermissionHelper: ProjectionPermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        AppExitHelper.setNormalExit(this, false)
        setTheme(R.style.Theme_ZKQcode)
        super.onCreate(savedInstanceState)
        // Unlock orientation after splash theme is replaced
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        projectionPermissionHelper = ProjectionPermissionHelper(this)
        ShowMessage.init(this)
        ScreenCaptureManager.init(this)
        LogHelper.showDebugInfo("MainActivity Start!")
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

