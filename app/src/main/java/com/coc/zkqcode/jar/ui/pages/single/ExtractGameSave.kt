@file:Suppress("FunctionName")

package com.coc.zkqcode.jar.ui.pages.single

import android.os.Environment
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.CustomNotificationWindow
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Game version enum
 * Centralized management of config keys, package names, folder names and subdirectories to extract for different versions (CN/Global)
 */
private enum class GameVariant(
    val settingKey: String,
    val folderName: String,
    val packageName: String,
    val targetSubDirs: List<String>
) {
    CN(
        settingKey = GLOBAL_SETTINGS.EXTRACT_CN.key,
        folderName = "zkqCNGameSave",
        packageName = "com.tencent.tmgp.supercell.clashofclans",
        targetSubDirs = listOf("shared_prefs", "databases")
    ),
    GLOBAL(
        settingKey = GLOBAL_SETTINGS.EXTRACT_GLOBAL.key,
        folderName = "zkqGlobalGameSave",
        packageName = "com.supercell.clashofclans",
        targetSubDirs = listOf("shared_prefs")
    )
}

fun LazyListScope.ExtractGameSave() {
    item {
        ExtractGameSaveContent()
    }
}

@Composable
private fun ExtractGameSaveContent() {
    // State management
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Generic function to show dialog, auto-dismiss after 2 seconds
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

            // [Modification] Base directory added a layer of zkqFiles
            // Path becomes: /sdcard/zkqFiles/zkqCNGameSave
            val gameRootDir = "$sdPath/zkqFiles/${variant.folderName}"

            // Specific path for this save (e.g., /sdcard/zkqFiles/zkqCNGameSave/001)
            val targetSaveDir = "$gameRootDir/$suffix"

            // 1. Check if save already exists (use Shell to check shared_prefs folder)
            val checkExistCmd = "[ -d \"$targetSaveDir/shared_prefs\" ]"
            if (Shell.cmd(checkExistCmd).exec().isSuccess) {
                showMsg("提取失败，存档已存在")
                return@launch
            }

            showMsg("提取中...")

            // 2. Ensure directory structure exists
            // mkdir -p will recursively create directories: if zkqFiles doesn't exist it will be created, if zkqCNGameSave doesn't exist it will also be created
            Shell.cmd("mkdir -p \"$gameRootDir\"").exec()

            // Create directory for this save
            Shell.cmd("mkdir -p \"$targetSaveDir\"").exec()

            // 3. Iterate and copy directories
            variant.targetSubDirs.forEach { dir ->
                val destPath = "$targetSaveDir/$dir"
                // Create target subdirectory
                Shell.cmd("mkdir -p \"$destPath\"").exec()

                // 使用 Root 权限复制文件
                // 路径加引号防止空格问题
                val cmd = "cp -r /data/data/${variant.packageName}/$dir/* \"$destPath\""
                Shell.cmd(cmd).exec()
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
            // 【修改点】删除路径也同步增加 zkqFiles
            val targetPath = "$sdPath/zkqFiles/${variant.folderName}/$suffix"

            // 使用 Shell 检查目录是否存在
            val checkExistCmd = "[ -d \"$targetPath\" ]"
            if (!Shell.cmd(checkExistCmd).exec().isSuccess) {
                showMsg("删除失败，存档不存在")
                return@launch
            }

            showMsg("删除中...")

            // 执行删除
            val result = Shell.cmd("rm -rf \"$targetPath\"").exec()

            if (result.isSuccess) {
                showMsg("删除成功！")
            } else {
                showMsg("删除失败，请检查权限")
            }
        }
    }

    Column(modifier = Modifier.padding(6.dp)) {
        // 遍历枚举生成 UI
        GameVariant.entries.forEach { variant ->
            GameConfigSection(
                variant = variant,
                onExtract = { performExtract(variant) },
                onDelete = { performDelete(variant) }
            )
        }
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