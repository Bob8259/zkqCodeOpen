package com.coc.zkqcode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.coc.zkqcode.utils.checkpermissions.CheckRootScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.coc.zkqcode.utils.basic.ShowMessage.init(this)
        com.coc.zkqcode.utils.screencapture.ScreenCaptureManager.init(this)
        
        setContent {
            CheckRootScreen()
        }
    }

    private val projectionLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            // Android 14+ 要求在调用 getMediaProjection 前必须有运行中的前台服务
            // 延迟一点点或者确保服务已经 startForeground 了
            val serviceIntent = android.content.Intent(this, com.coc.zkqcode.utils.floatingwindows.UIWindowService::class.java)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }

            com.coc.zkqcode.utils.screencapture.ScreenCaptureManager.onPermissionGranted(
                result.resultCode,
                result.data!!,
                this
            )
        }
    }

    fun requestMediaProjection() {
        com.coc.zkqcode.utils.screencapture.ScreenCaptureManager.requestPermission(this, projectionLauncher)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
