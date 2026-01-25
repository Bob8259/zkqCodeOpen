package com.coc.zkqcode.utils.screencapture

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.coc.zkqcode.utils.floatingwindows.UIWindowService

class ProjectionPermissionHelper(private val activity: ComponentActivity) {

    private val projectionLauncher: ActivityResultLauncher<Intent> = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            // Android 14+ 要求在调用 getMediaProjection 前必须有运行中的前台服务
            // 延迟一点点或者确保服务已经 startForeground 了
            val serviceIntent = Intent(activity, UIWindowService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity.startForegroundService(serviceIntent)
            } else {
                activity.startService(serviceIntent)
            }

            ScreenCaptureManager.onPermissionGranted(
                result.resultCode,
                result.data!!
            )
        }
    }

    fun requestMediaProjection() {
        ScreenCaptureManager.requestPermission(projectionLauncher)
    }
}
