package com.coc.zkqcode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.coc.zkqcode.utils.checkpermissions.CheckRootScreen
import com.coc.zkqcode.core.system.screencapture.ProjectionPermissionHelper
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager

class MainActivity : ComponentActivity() {
    private lateinit var projectionPermissionHelper: ProjectionPermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectionPermissionHelper = ProjectionPermissionHelper(this)
        com.coc.zkqcode.utils.basic.ShowMessage.init(this)
        ScreenCaptureManager.init(this)
        
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
