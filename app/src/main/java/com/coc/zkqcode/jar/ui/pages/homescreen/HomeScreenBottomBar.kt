package com.coc.zkqcode.jar.ui.pages.homescreen

import android.os.Environment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.exit.AppExitHelper
import com.coc.zkqcode.jar.ui.components.CustomAlertDialog
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.CustomNotificationWindow
import com.coc.zkqcode.jar.ui.schema.ConfigManager
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreenBottomBar(
    scope: CoroutineScope,
    saveAndSetPlaying: (Boolean) -> Unit,
    showNotification: Boolean,
    notificationMessage: String,
    onDismissNotification: () -> Unit,
    showCleanAllConfirmation: Boolean,
    onDismissCleanAll: () -> Unit
) {
    val context = LocalContext.current

    var showExitConfirmation by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
    ) {
        Row(modifier = Modifier.padding(start = 6.dp)) {
            CustomButton(
                text = "保存并运行", onClick = {
                    saveAndSetPlaying(true)
                })
            CustomButton(
                text = "保存并暂停", onClick = {
                    saveAndSetPlaying(false)
                })
            CustomButton(
                text = "保存并退出", onClick = {
                    showExitConfirmation = true
                })
        }
    }

    if (showExitConfirmation) {
        CustomAlertDialog(onDismissRequest = { showExitConfirmation = false }, title = {
            Text(
                text = "退出提示", style = MaterialTheme.typography.titleMedium
            )
        }, text = {
            Text(
                text = "确认要退出吗？", style = MaterialTheme.typography.bodyMedium
            )
        }, confirmButton = {
            Row {
                TextButton(onClick = { showExitConfirmation = false }) {
                    Text("取消")
                }
                TextButton(onClick = {
                    showExitConfirmation = false
                    AppStateManager.setMode(AppMode.Run)
                    scope.launch {
                        ConfigManager.saveAndRun {
                            AppExitHelper.exitApplication(context)
                        }
                    }
                }) {
                    Text("确认")
                }
            }
        })
    }

    if (showCleanAllConfirmation) {
        CustomAlertDialog(onDismissRequest = onDismissCleanAll, title = {
            Text(
                text = "清除全部数据", style = MaterialTheme.typography.titleMedium
            )
        }, text = {
            Text(
                text = "确认要删除所有数据吗？\n此操作不可撤销，将删除辅助设置、账号信息等全部数据。\n\n清除数据后，辅助将自动关闭，请手动重启辅助。",
                style = MaterialTheme.typography.bodyMedium
            )
        }, confirmButton = {
            Row {
                TextButton(onClick = onDismissCleanAll) {
                    Text("取消")
                }
                TextButton(onClick = {
                    onDismissCleanAll()
                    scope.launch {
                        val sdPath = Environment.getExternalStorageDirectory().path
                        withContext(Dispatchers.IO) {
                            RunShell.runNoOutput(
                                "rm -rf $sdPath/zkqFiles && rm -rf $sdPath/loginData",
                                checkIsPlaying = false
                            )
                        }
                        AppExitHelper.exitApplication(context)
                    }
                }) {
                    Text("确认")
                }
            }
        })
    }

    if (showNotification) {
        CustomNotificationWindow(
            message = notificationMessage, onDismissRequest = onDismissNotification
        )
    }
}
