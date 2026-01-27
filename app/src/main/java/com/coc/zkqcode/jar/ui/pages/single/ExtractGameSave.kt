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
import com.coc.zkqcode.core.ui.components.CustomButton
import com.coc.zkqcode.core.ui.components.CustomNotificationWindow
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.ui.components.SettingInputRow
import com.coc.zkqcode.core.data.database.Schema.GLOBAL_SETTINGS
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 游戏版本枚举
 * 集中管理不同版本（国服/国际服）的配置Key、包名、文件夹名称和需要提取的子目录
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
    // 状态管理
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // 通用显示弹窗函数
    fun showMsg(msg: String) {
        dialogMessage = msg
        showDialog = true
    }

    // 提取逻辑
    fun performExtract(variant: GameVariant) {
        coroutineScope.launch(Dispatchers.IO) {
            val suffix = GlobalVars.configStates[variant.settingKey]!!.value
            val sdPath = Environment.getExternalStorageDirectory().path

            // 【修改点】基础目录增加了一层 zkqFiles
            // 路径变为: /sdcard/zkqFiles/zkqCNGameSave
            val gameRootDir = "$sdPath/zkqFiles/${variant.folderName}"

            // 本次存档的具体路径 (e.g., /sdcard/zkqFiles/zkqCNGameSave/001)
            val targetSaveDir = "$gameRootDir/$suffix"

            // 1. 检查存档是否已存在 (使用 Shell 检查 shared_prefs 文件夹)
            val checkExistCmd = "[ -d \"$targetSaveDir/shared_prefs\" ]"
            if (Shell.cmd(checkExistCmd).exec().isSuccess) {
                showMsg("提取失败，存档已存在")
                return@launch
            }

            showMsg("提取中...")

            // 2. 确保目录结构存在
            // mkdir -p 会递归创建目录：如果 zkqFiles 不存在会创建，如果 zkqCNGameSave 不存在也会创建
            Shell.cmd("mkdir -p \"$gameRootDir\"").exec()

            // 创建本次存档的目录
            Shell.cmd("mkdir -p \"$targetSaveDir\"").exec()

            // 3. 遍历并复制目录
            variant.targetSubDirs.forEach { dir ->
                val destPath = "$targetSaveDir/$dir"
                // 创建目标子目录
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
 * 提取出的复用 UI 组件
 */
@Composable
private fun GameConfigSection(
    variant: GameVariant,
    onExtract: () -> Unit,
    onDelete: () -> Unit
) {
    val settingSchema = GLOBAL_SETTINGS.all.firstOrNull { it.key == variant.settingKey }
    val displayName = settingSchema?.displayName ?: variant.settingKey

    SettingInputRow(key = variant.settingKey)

    Row {
        val regionName = if (variant == GameVariant.CN) "国服" else "国际服"
        CustomButton(text = "提取${regionName}数据", onClick = onExtract)
        CustomButton(text = "删除现有数据", onClick = onDelete)
    }
}