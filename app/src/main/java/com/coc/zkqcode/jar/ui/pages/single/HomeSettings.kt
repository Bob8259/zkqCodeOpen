package com.coc.zkqcode.jar.ui.pages.single

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SettingDropdown
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.pages.homescreen.QuickActionCard
import com.coc.zkqcode.jar.ui.pages.homescreen.TipsActionTile
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.jar.ui.theme.AppColors
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager

fun LazyListScope.homeSettings(
    context: Context,
    saveAndSetPlaying: (Boolean) -> Unit,
    showMsg: (String) -> Unit,
    cleanMemory: () -> Unit,
    cleanAllData: () -> Unit,
    onSaveSuccess: () -> Unit,
    onShowAnnouncement: () -> Unit,
    onShowImportOldData: () -> Unit
) {
    item {
        SettingSection {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (GlobalVars.isAutoRunEnabled) "${GlobalVars.autoRunTimer}秒后自动运行" else "计时已停止",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp, end = 6.dp)
                )
                CustomButton(
                    text = "停止计时", onClick = { GlobalVars.isAutoRunEnabled = false })
                CustomButton(
                    text = "公告", onClick = { onShowAnnouncement() })
            }
            LoginScreen(
                onAdFreeClick = {
                    saveAndSetPlaying(false)
                })
        }
        // Quick-action two-card row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                title = "手动切号？", subtitle = "点击切号", dotColor = Color(0xFFED8936), onClick = {
                    AppStateManager.setMode(AppMode.SwitchAccount)
                    onSaveSuccess()
                }, modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                title = "发现问题？", subtitle = "点击反馈", dotColor = AppColors.Teal, onClick = {
                    AppStateManager.setMode(AppMode.BugReport)
                    onSaveSuccess()
                }, modifier = Modifier.weight(1f)
            )
        }
        // Background customization row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                title = "自定义背景", subtitle = "选择图片", dotColor = AppColors.PrimaryLight, onClick = {
                    try {
                        saveAndSetPlaying(false)
                        val intent = Intent(context, com.coc.zkqcode.core.ui.floatingwindows.ImagePickerActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    } catch (e: Throwable) {
                        showMsg("当前版本不支持此功能，请更新主程序")
                    }
                }, modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                title = "重置背景", subtitle = "恢复默认", dotColor = AppColors.NavInactive, onClick = {
                    try {
                        val success = com.coc.zkqcode.core.util.fileactions.resetCustomBackground(context)
                        showMsg(if (success) "背景已重置" else "重置失败")
                    } catch (e: Throwable) {
                        showMsg("当前版本不支持此功能，请更新主程序")
                    }
                }, modifier = Modifier.weight(1f)
            )
        }

        SettingSection {
            // Tips section header
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFFED8936), CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "换机前务必清除数据 保护账号安全", style = MaterialTheme.typography.labelMedium, color = AppColors.TextPrimary
                )
            }
            // Two-tile grid
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(bottom = 8.dp)
            ) {
                TipsActionTile(
                    text = "清除账号记忆",
                    explain = "辅助会缓存账号的运行状态，例如当前账号是否已完成突袭、部落竞赛等任务。切换账号后若未清除缓存，辅助将沿用先前账号的状态信息，可能导致异常操作。",
                    onClick = { cleanMemory() },
                    modifier = Modifier.weight(1f)
                )
                TipsActionTile(
                    text = "清除全部数据", onClick = { cleanAllData() }, modifier = Modifier.weight(1f)
                )
            }
            // Settings FlowRow
            FlowRow(
                itemVerticalAlignment = Alignment.CenterVertically
            ) {
                SettingInputRow(key = GLOBAL_SETTINGS.DELAY_MULTIPLIER.key)
                SettingInputRow(key = GLOBAL_SETTINGS.ENTER_GAME_TIMER.key)
                SettingSwitchIcon(key = GLOBAL_SETTINGS.RECORD_PROGRESS.key)

                SettingDropdown(
                    key = GLOBAL_SETTINGS.AUTO_UPDATE.key, options = listOf("关闭自动更新", "仅启动时更新", "实时自动更新")
                )
                SettingSwitchIcon(key = GLOBAL_SETTINGS.AUTO_START.key)
                SettingSwitchIcon(
                    key = GLOBAL_SETTINGS.DISABLE_RESTART_GAME.key,
                    explain = "勾选后，所有重启游戏的设定将失效，可能会导致卡界面。建议谨慎使用。"
                )
                SettingDropdown(
                    key = GLOBAL_SETTINGS.AFTER_KICK_OPTION.key, options = listOf("立刻重连", "切换账号", "原地等待"),
                    afterChange = { selectedIndex ->
                        if (selectedIndex != 0) {
                            GlobalVars.configStates[GLOBAL_SETTINGS.BATCH_CREATE_ACCOUNT.key]?.value = "0"
                        }
                    })
                SettingInputRow(key = GLOBAL_SETTINGS.DEVICE_REMARK.key)
                CustomButton(
                    text = "导入旧版数据",
                    onClick = { onShowImportOldData() },
                    marginTop = 6.dp
                )
            }
        }
    }
}
