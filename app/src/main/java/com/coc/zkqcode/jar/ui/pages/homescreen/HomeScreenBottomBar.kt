@file:Suppress("AssignedValueIsNeverRead")

package com.coc.zkqcode.jar.ui.pages.homescreen

import android.os.Environment
import androidx.compose.foundation.layout.Row
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
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.components.CustomAlertDialog
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.CustomNotificationWindow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.schema.ConfigManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreenBottomBar(
    scope: CoroutineScope, saveAndSetPlaying: (Boolean) -> Unit, showNotification: Boolean, notificationMessage: String, onDismissNotification: () -> Unit, showCleanAllConfirmation: Boolean, onDismissCleanAll: () -> Unit
) {
    val context = LocalContext.current

    var showExitConfirmation by rememberSaveable { mutableStateOf(false) }

    SettingSection(modifier = Modifier.padding(horizontal = 4.dp)) {
        Row {
            CustomButton(
                text = Strings.saveAndRun, marginTop = 0.dp, onClick = {
                    saveAndSetPlaying(true)
                })
            CustomButton(
                text = Strings.saveAndPause, marginTop = 0.dp, onClick = {
                    saveAndSetPlaying(false)
                })
            CustomButton(
                text = Strings.saveAndExit, marginTop = 0.dp, marginEnd = 0.dp, onClick = {
                    showExitConfirmation = true
                })
        }
    }

    if (showExitConfirmation) {
        CustomAlertDialog(onDismissRequest = { showExitConfirmation = false }, title = {
            Text(
                text = Strings.exitTitle, style = MaterialTheme.typography.titleMedium
            )
        }, text = {
            Text(
                text = Strings.exitMessage, style = MaterialTheme.typography.bodyMedium
            )
        }, confirmButton = {
            Row {
                TextButton(onClick = { showExitConfirmation = false }) {
                    Text(Strings.cancel)
                }
                TextButton(onClick = {
                    showExitConfirmation = false
                    scope.launch {
                        ConfigManager.saveAndRun {
                            AppExitHelper.exitApplication(context)
                        }
                    }
                }) {
                    Text(Strings.confirm)
                }
            }
        })
    }

    if (showCleanAllConfirmation) {
        CustomAlertDialog(onDismissRequest = onDismissCleanAll, title = {
            Text(
                text = Strings.cleanAllTitle, style = MaterialTheme.typography.titleMedium
            )
        }, text = {
            Text(
                text = Strings.cleanAllMessage, style = MaterialTheme.typography.bodyMedium
            )
        }, confirmButton = {
            Row {
                TextButton(onClick = onDismissCleanAll) {
                    Text(Strings.cancel)
                }
                TextButton(onClick = {
                    onDismissCleanAll()
                    scope.launch {
                        val sdPath = Environment.getExternalStorageDirectory().path
                        withContext(Dispatchers.IO) {
                            RunShell.runNoOutput(
                                "rm -rf $sdPath/zkqFiles && rm -rf $sdPath/loginData", checkIsPlaying = false
                            )
                        }
                        AppExitHelper.exitApplication(context)
                    }
                }) {
                    Text(Strings.confirm)
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
