@file:Suppress("FunctionName")

package com.coc.zkqcode.jar.ui.pages.single

import android.os.Environment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.CustomNotificationWindow
import com.coc.zkqcode.jar.ui.components.SettingDropdown
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.jar.code.ShellHelper
import com.coc.zkqcode.core.util.fileactions.FileHelper
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Game variant enum that delegates package/folder/subDir metadata to [GameVersion],
 * keeping only the UI setting key specific to the extract page.
 */
private enum class GameVariant(
    val settingKey: String,
    val gameVersion: GameVersion
) {
    CN(settingKey = GLOBAL_SETTINGS.EXTRACT_CN.key, gameVersion = GameVersion.CN),
    GLOBAL(settingKey = GLOBAL_SETTINGS.EXTRACT_GLOBAL.key, gameVersion = GameVersion.GLOBAL)
}

fun LazyListScope.ExtractGameSave() {
    item {
        ExtractGameSaveContent()
    }
}

@Composable
fun ExtractGameSaveContent() {
    // State management
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Generic function to show dialogue, auto-dismiss after 2 seconds
    fun showMsg(msg: String) {
        dialogMessage = msg
        showDialog = true
        coroutineScope.launch {
            delay(1200)
            showDialog = false
        }
    }

    // Extract logic
    fun performExtract(variant: GameVariant) {
        coroutineScope.launch(Dispatchers.IO) {
            val suffix = GlobalVars.configStates[variant.settingKey]!!.value
            val sdPath = Environment.getExternalStorageDirectory().path

            val gameRootDir = "$sdPath/zkqFiles/${variant.gameVersion.folderName}"

            // Specific path for this save (e.g., /sdcard/zkqFiles/zkqCNGameSave/001)
            val targetSaveDir = "$gameRootDir/$suffix"

            // 1. Check if save already exists (use Shell to check shared_prefs folder)
            val checkExistCmd = "[ -d \"$targetSaveDir/shared_prefs\" ]"
            if (ShellHelper.runBlocking(checkExistCmd).isSuccess) {
                showMsg("提取失败，存档已存在")
                return@launch
            }

            showMsg("提取中...")

            // 2. Ensure directory structure exists
            // mkdir -p will recursively create directories: if zkqFiles doesn't exist it will be created, if zkqCNGameSave doesn't exist it will also be created
            ShellHelper.runBlockingNoOutput("mkdir -p \"$gameRootDir\"")

            // Create directory for this save
            ShellHelper.runBlockingNoOutput("mkdir -p \"$targetSaveDir\"")

            // Iterate and copy each subdirectory from game data into the save location
            variant.gameVersion.subDirs.forEach { dir ->
                val destPath = "$targetSaveDir/$dir"
                RunShell.runNoOutput("mkdir -p \"$destPath\"", false)
                RunShell.runNoOutput("cp -r /data/data/${variant.gameVersion.packageName}/$dir/* \"$destPath\"", false)
            }

            showMsg("提取成功！")
        }
    }

    // 删除逻辑
    fun performDelete(variant: GameVariant) {
        coroutineScope.launch(Dispatchers.IO) {
            val suffix = GlobalVars.configStates[variant.settingKey]!!.value

            if (suffix.isEmpty()) {
                showMsg("错误：未获取到路径序号")
                return@launch
            }

            val sdPath = Environment.getExternalStorageDirectory().path
            val targetPath = "$sdPath/zkqFiles/${variant.gameVersion.folderName}/$suffix"

            // 使用 Shell 检查目录是否存在
            val checkExistCmd = "[ -d \"$targetPath\" ]"
            if (!ShellHelper.runBlocking(checkExistCmd).isSuccess) {
                showMsg("删除失败，存档不存在")
                return@launch
            }

            showMsg("删除中...")

            // 执行删除
            val result = ShellHelper.runBlocking("rm -rf \"$targetPath\"")

            if (result.isSuccess) {
                showMsg("删除成功！")
            } else {
                showMsg("删除失败，请检查权限")
            }
        }
    }

    // Save the data-number entry into /loginData/data.json
    fun performSaveDataNumber() {
        coroutineScope.launch(Dispatchers.IO) {
            val slot = GlobalVars.configStates[GLOBAL_SETTINGS.EXTRACT_DATA_NUMBER.key]!!.value.trim()
            val content = GlobalVars.configStates[GLOBAL_SETTINGS.EXTRACT_DATA_CONTENT.key]!!.value.trim()

            if (slot.isEmpty()) {
                showMsg("错误：序号不能为空")
                return@launch
            }
            if (content.isEmpty()) {
                showMsg("错误：数据号内容不能为空")
                return@launch
            }

            val sdPath = Environment.getExternalStorageDirectory().path
            val dataPath = "$sdPath/loginData/data.json"

            // Read existing JSON, or start with an empty object
            val json = FileHelper.readJson(dataPath) ?: JsonObject()
            json.addProperty(slot, content)

            val success = FileHelper.writeJson(dataPath, json.toString())
            showMsg(if (success) "保存成功！" else "保存失败，请检查权限")
        }
    }

    // Delete the selected data-number entry from /loginData/data.json
    fun performDeleteDataNumber() {
        coroutineScope.launch(Dispatchers.IO) {
            val slot = GlobalVars.configStates[GLOBAL_SETTINGS.EXTRACT_DATA_NUMBER.key]!!.value.trim()

            if (slot.isEmpty()) {
                showMsg("错误：序号不能为空")
                return@launch
            }

            val sdPath = Environment.getExternalStorageDirectory().path
            val dataPath = "$sdPath/loginData/data.json"

            val json = FileHelper.readJson(dataPath)
            if (json == null || !json.has(slot)) {
                showMsg("删除失败，该序号数据不存在")
                return@launch
            }

            json.remove(slot)

            val success = if (json.size() == 0) {
                // No entries left; remove the file entirely
                FileHelper.deleteJson(dataPath)
            } else {
                FileHelper.writeJson(dataPath, json.toString())
            }
            showMsg(if (success) "删除成功！" else "删除失败，请检查权限")
        }
    }

    // Initialize and subscribe to version selector state so parent recomposes on change
    val versionState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.EXTRACT_VERSION.key) {
        mutableStateOf(Schema.getDefaultValue(GLOBAL_SETTINGS.EXTRACT_VERSION.key))
    }
    val selectedIndex = versionState.value.toIntOrNull() ?: 0
    val selectedVariant = GameVariant.entries.getOrElse(selectedIndex) { GameVariant.CN }


    // Show section content based on the selected index
    SettingSection {
        // Dropdown to switch between CN and Global versions
        if (selectedIndex == 2) {
            // For "数据号" mode, show the slot selector and data content inputs plus action buttons
            FlowRow {
                SettingDropdown(
                    key = GLOBAL_SETTINGS.EXTRACT_VERSION.key,
                    options = listOf("国服", "国际服", "数据号")
                )
                Row {
                    CustomButton(text = "▼", onClick = {
                        val key = GLOBAL_SETTINGS.EXTRACT_DATA_NUMBER.key
                        val current = GlobalVars.configStates[key]?.value?.toIntOrNull() ?: 1
                        if (current > 1) GlobalVars.configStates[key]?.value = (current - 1).toString()
                    })
                    CustomButton(text = "▲", onClick = {
                        val key = GLOBAL_SETTINGS.EXTRACT_DATA_NUMBER.key
                        val current = GlobalVars.configStates[key]?.value?.toIntOrNull() ?: 0
                        GlobalVars.configStates[key]?.value = (current + 1).toString()
                    })
                }
            }
            SettingInputRow(key = GLOBAL_SETTINGS.EXTRACT_DATA_NUMBER.key)
            SettingInputRow(key = GLOBAL_SETTINGS.EXTRACT_DATA_CONTENT.key)
            Row {
                CustomButton(text = "保存数据", onClick = { performSaveDataNumber() })
                CustomButton(text = "删除数据", onClick = { performDeleteDataNumber() })
            }
        } else {
            FlowRow {
                SettingDropdown(
                    key = GLOBAL_SETTINGS.EXTRACT_VERSION.key,
                    options = listOf("国服", "国际服", "数据号")
                )
                Row {
                    CustomButton(text = "▼", onClick = {
                        val current = GlobalVars.configStates[selectedVariant.settingKey]?.value?.toIntOrNull() ?: 1
                        if (current > 1) GlobalVars.configStates[selectedVariant.settingKey]?.value = (current - 1).toString()
                    })
                    CustomButton(text = "▲", onClick = {
                        val current = GlobalVars.configStates[selectedVariant.settingKey]?.value?.toIntOrNull() ?: 0
                        GlobalVars.configStates[selectedVariant.settingKey]?.value = (current + 1).toString()
                    })
                }
            }
            GameConfigSection(
                variant = selectedVariant,
                onExtract = { performExtract(selectedVariant) },
                onDelete = { performDelete(selectedVariant) }
            )
        }
    }
    // Show disclaimer when "数据号" (index 2) is selected
    if (selectedIndex == 2) {
        Text(
            text = "数据号功能源自第三方代码，与紫孔雀作者无关。该功能仅供学习研究使用，禁止用于其他用途。数据号以明文方式保存，请自行确保信息安全。",
            color = Color.Red,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 6.dp, start = 4.dp, end = 4.dp)
        )
    }


    // 全局弹窗组件
    if (showDialog) {
        CustomNotificationWindow(
            message = dialogMessage,
            onDismissRequest = { }
        )
    }
}

/**
 * Extracted reusable UI component
 */
@Composable
private fun GameConfigSection(
    variant: GameVariant,
    onExtract: () -> Unit,
    onDelete: () -> Unit
) {
    SettingInputRow(key = variant.settingKey)
    Row {
        val regionName = if (variant == GameVariant.CN) "国服" else "国际服"
        CustomButton(text = "提取${regionName}数据", onClick = onExtract)
        CustomButton(text = "删除现有数据", onClick = onDelete)
    }
}