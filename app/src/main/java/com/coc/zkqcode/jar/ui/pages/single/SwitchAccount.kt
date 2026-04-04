package com.coc.zkqcode.jar.ui.pages.single

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.universal.smalltools.writeGameFilesCore
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SettingDropdown
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager
import com.topjohnwu.superuser.Shell
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

            // Divider
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp, color = Color.Gray
            )
            FlowRow {
                SettingDropdown(
                    key = GLOBAL_SETTINGS.SWITCH_ACCOUNT_VERSION.key,
                    options = listOf("国服", "国际服")
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
                // Account Selection Row
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "切换到第", modifier = Modifier.padding(horizontal = 8.dp), color = Color.Black
                    )

                    BasicTextField(
                        value = accountNumber, onValueChange = { newValue ->
                            // Only allow numeric input
                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                accountNumber = newValue.filter { it.isDigit() }
                            }
                        }, modifier = Modifier
                            .width(60.dp)
                            .background(
                                color = Color.White, shape = RoundedCornerShape(4.dp)
                            )
                            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                            .padding(4.dp)
                            .height(24.dp), // Adjust height to match typical text field
                        singleLine = true
                    )

                    Text(
                        text = "个账号", modifier = Modifier.padding(horizontal = 8.dp), color = Color.Black
                    )
                }
            }
            FlowRow(horizontalArrangement = Arrangement.Center) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp, color = Color.Gray
                )
                ExtractGameSaveContent()
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp, color = Color.Gray
                )
                Row {
                    // Confirm Button aligned to start
                    CustomButton(
                        text = "确认切号", onClick = {
                            scope.launch(Dispatchers.IO) {
                                GlobalVars.isSwitchingAccount = true
                                GlobalVars.updateWindowPosition = true
                                val accNum = accountNumber.ifEmpty { "1" }
                                // Use SWITCH_ACCOUNT_VERSION dropdown to determine game version directly
                                val versionKey = GLOBAL_SETTINGS.SWITCH_ACCOUNT_VERSION.key
                                val versionStr = GlobalVars.configStates[versionKey]?.value ?: "0"
                                val version = versionStr.toIntOrNull() ?: 0
                                val sdPath = Environment.getExternalStorageDirectory().path
                                // Use accountNumber directly as the save path name
                                val savePathName = accNum

                                if (version == 0) {
                                    // CN Version
                                    val sourceDir = "$sdPath/zkqFiles/zkqCNGameSave/$savePathName"
                                    // Check existence
                                    if (!Shell.cmd("[ -d \"$sourceDir\" ]").exec().isSuccess) {
                                        ShowMessage("存档文件不存在！\n请仔细检查存档路径以及游戏版本！")
                                        GlobalVars.isSwitchingAccount = false
                                        return@launch
                                    }

                                    // Force-stop before writing to avoid file-in-use conflicts
                                    RunShell.runNoOutput("am force-stop com.tencent.tmgp.supercell.clashofclans", false)
                                    writeGameFilesCore(
                                        packageName = "com.tencent.tmgp.supercell.clashofclans", savePathName = savePathName, folderName = "zkqCNGameSave", subDirs = listOf("shared_prefs", "databases"), killGame = false
                                    )
                                    // Launch the game after write completes
                                    RunShell.runNoOutput("monkey -p com.tencent.tmgp.supercell.clashofclans -c android.intent.category.LAUNCHER 1", false)
                                    RunShell.runNoOutput("am start -n com.tencent.tmgp.supercell.clashofclans/com.supercell.titan.tencent.GameAppTencent", false)
                                } else {
                                    // Global Version
                                    val sourceDir = "$sdPath/zkqFiles/zkqGlobalGameSave/$savePathName"

                                    // Check existence
                                    if (!Shell.cmd("[ -d \"$sourceDir\" ]").exec().isSuccess) {
                                        ShowMessage("存档文件不存在！\n请仔细检查存档路径以及游戏版本！")
                                        GlobalVars.isSwitchingAccount = false
                                        return@launch
                                    }

                                    // Force-stop before writing to avoid file-in-use conflicts
                                    RunShell.runNoOutput("am force-stop com.supercell.clashofclans", false)
                                    writeGameFilesCore(
                                        packageName = "com.supercell.clashofclans", savePathName = savePathName, folderName = "zkqGlobalGameSave", subDirs = listOf("shared_prefs"), killGame = false
                                    )
                                    // Launch the game after write completes
                                    RunShell.runNoOutput("monkey -p com.supercell.clashofclans -c android.intent.category.LAUNCHER 1", false)
                                    RunShell.runNoOutput("am start -n com.supercell.clashofclans/com.supercell.titan.GameApp", false)
                                }
                                GlobalVars.isSwitchingAccount = false
                                AppStateManager.setMode(AppMode.Run) //just to close the ui
                                GlobalVars.isPlaying.value = false
                                GlobalVars.updateWindowPosition = true
                                withContext(Dispatchers.Main) {
                                    ShowMessage("切号完成", false)
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
}