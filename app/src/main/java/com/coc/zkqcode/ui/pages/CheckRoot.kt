package com.coc.zkqcode.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class RootStatus {
    CHECKING,
    GRANTED,
    DENIED
}

@Composable
fun CheckRootScreen(content: @Composable () -> Unit) {
    var rootStatus by remember { mutableStateOf(RootStatus.CHECKING) }

    LaunchedEffect(Unit) {
        rootStatus = checkRoot()
    }
    when (rootStatus) {
        RootStatus.CHECKING -> {
            FullScreenMessage("检测Root权限中\n若遇到问题，请在官网右上角加群咨询")
        }

        RootStatus.DENIED -> {
            FullScreenMessage("请授予Root权限（本软件名称：紫孔雀）\n若不会操作，请在官网右上角加群咨询")
        }

        RootStatus.GRANTED -> {
            content()
        }
    }
}

@Composable
private fun FullScreenMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 32.sp,
            modifier = Modifier.padding(8.dp)
        )
    }
}

private suspend fun checkRoot(): RootStatus = withContext(Dispatchers.IO) {
    // Shell.getShell() will automatically try to get root access if not already granted
    val shell = Shell.getShell()
    if (shell.isRoot) {
        println("CheckRoot: Root access granted, attempting to grant overlay permission")

        // 尝试使用 pm grant 授予悬浮窗权限
        val grantResult =
            Shell.cmd("pm grant com.coc.zkqcode android.permission.SYSTEM_ALERT_WINDOW").exec()
        println("CheckRoot: pm grant result - code: ${grantResult.code}, out: ${grantResult.out}, err: ${grantResult.err}")

        // 尝试使用 appops 授予悬浮窗权限
        val appopsResult = Shell.cmd("appops set com.coc.zkqcode SYSTEM_ALERT_WINDOW allow").exec()
        println("CheckRoot: appops result - code: ${appopsResult.code}, out: ${appopsResult.out}, err: ${appopsResult.err}")

        RootStatus.GRANTED
    } else {
        RootStatus.DENIED
    }
}
