package com.coc.zkqcode.core.system.screencapture

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.coc.zkqcode.core.ui.floatingwindows.UIWindowService
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class ProjectionPermissionHelper(private val activity: ComponentActivity) {

    private val projectionLauncher: ActivityResultLauncher<Intent> = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            // Android 14+ requires a running foreground service with
            // FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION before calling getMediaProjection()
            val serviceIntent = Intent(activity, UIWindowService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity.startForegroundService(serviceIntent)
            } else {
                activity.startService(serviceIntent)
            }

            // Wait for the foreground service to finish startForeground() before
            // calling getMediaProjection(), avoiding the race condition on cold start
            CoroutineScope(Dispatchers.Main).launch {
                val ready = withTimeoutOrNull(5000) {
                    UIWindowService.foregroundReady.await()
                }
                if (ready == null) {
                    logAndRestart("前台服务未能在5秒内启动，无法获取MediaProjection权限")
                }
                ScreenCaptureManager.onPermissionGranted(
                    result.resultCode,
                    result.data!!
                )
            }
        }
    }

    fun requestMediaProjection() {
        ScreenCaptureManager.requestPermission(projectionLauncher)
    }
}
