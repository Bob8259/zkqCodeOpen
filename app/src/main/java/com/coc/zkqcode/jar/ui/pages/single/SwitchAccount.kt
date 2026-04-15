package com.coc.zkqcode.jar.ui.pages.single

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.smalltools.writeDataNumberFile
import com.coc.zkqcode.jar.code.universal.smalltools.writeGameFilesCore
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SettingDropdown
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SwitchAccount(onClose: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sharedPreferences = remember { context.getSharedPreferences("SwitchAccountPrefs", Context.MODE_PRIVATE) }
    var accountNumber by remember {
        mutableStateOf(sharedPreferences.getString("accountNumber", "1") ?: "1")
    }

    LaunchedEffect(accountNumber) {
        sharedPreferences.edit { putString("accountNumber", accountNumber) }
    }

    val imageBitmap = remember {
        try {
            context.assets.open("switch_explain.png").use {
                BitmapFactory.decodeStream(it).asImageBitmap()
            }
        } catch (_: Exception) {
            null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(16.dp),

        ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            // Top Text aligned to start
            Text(
                text = "小提示：在悬浮窗点击此按钮，即可回到切号工具。", color = Color.Black, modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Image
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap, contentDescription = "Switch Explain", modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            } else {
                Text(
                    text = "Image 'switch_explain.png' not found", color = Color.Red, modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp, color = Color.Gray
            )
            ExtractGameSaveContent()
            // Divider
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp, color = Color.Gray
            )
            FlowRow {
                SettingDropdown(
                    key = GLOBAL_SETTINGS.SWITCH_ACCOUNT_VERSION.key,
                    options = listOf("国服", "国际服", "数据号")
                )
                Row {
                    CustomButton(text = "▼", onClick = {
                        val current = accountNumber.toIntOrNull() ?: 1
                        if (current > 1) {
                            accountNumber = (current - 1).toString()
                        }
                    })
                    CustomButton(text = "▲", onClick = {
                        val current = accountNumber.toIntOrNull() ?: 0
                        accountNumber = (current + 1).toString()
                    })
                }
                // Match the compact input row sizing used by ExtractGameSaveContent.
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "切换到第",
                        modifier = Modifier.padding(end = 8.dp),
                        color = Color.Black,
                        style = MaterialTheme.typography.labelMedium
                    )

                    BasicTextField(
                        value = accountNumber, onValueChange = { newValue ->
                            // Only allow numeric input
                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                accountNumber = newValue.filter { it.isDigit() }
                            }
                        }, modifier = Modifier
                            .width(48.dp)
                            .background(
                                color = Color.White, shape = RoundedCornerShape(4.dp)
                            )
                            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                            .padding(4.dp)
                            .align(Alignment.CenterVertically),
                        singleLine = true
                    )

                    Text(
                        text = "个账号",
                        modifier = Modifier.padding(start = 8.dp),
                        color = Color.Black,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            // Keep the action buttons centered within the dialogue content area.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomButton(
                    text = "确认切号", onClick = {
                        scope.launch(Dispatchers.IO) {
                            GlobalVars.isSwitchingAccount = true
                            GlobalVars.updateWindowPosition = true
                            val savePathName = accountNumber.ifEmpty { "1" }
                            val versionKey = GLOBAL_SETTINGS.SWITCH_ACCOUNT_VERSION.key
                            val versionId = (GlobalVars.configStates[versionKey]?.value ?: "0").toIntOrNull() ?: 0
                            val launchVersion = if (versionId == 2) GameVersion.CN else GameVersion.fromId(versionId)

                            // Data-number mode only prepares the login data here; the post-launch bot loop is not used in manual switching.
                            val success = if (versionId == 2) {
                                RunShell.runNoOutput("am force-stop ${launchVersion.packageName}", false)
                                writeDataNumberFile(savePathName)
                            } else {
                                // Force-stop before writing to avoid file-in-use conflicts.
                                RunShell.runNoOutput("am force-stop ${launchVersion.packageName}", false)
                                writeGameFilesCore(
                                    packageName = launchVersion.packageName,
                                    savePathName = savePathName,
                                    folderName = launchVersion.folderName,
                                    subDirs = launchVersion.subDirs,
                                    killGame = false
                                )
                            }
                            if (!success) {
                                GlobalVars.isSwitchingAccount = false
                                return@launch
                            }
                            // Launch the corresponding game after the switch completes.
                            RunShell.runNoOutput("monkey -p ${launchVersion.packageName} -c android.intent.category.LAUNCHER 1", false)
                            RunShell.runNoOutput("am start -n ${launchVersion.launchComponent}", false)
                            GlobalVars.isSwitchingAccount = false
                            AppStateManager.setMode(AppMode.Run)
                            GlobalVars.isPlaying.value = false
                            GlobalVars.updateWindowPosition = true
                            withContext(Dispatchers.Main) {
                                accountMessage("切号完成", false)
                                onClose()
                            }
                        }
                    })
                CustomButton(
                    text = "关闭窗口", onClick = {
                        AppStateManager.setMode(AppMode.Run) //just to close the ui
                        GlobalVars.isPlaying.value = false
                        GlobalVars.updateWindowPosition = true
                        scope.launch {
                            onClose()
                        }
                    })
            }
        }
    }
}