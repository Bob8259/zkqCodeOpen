package com.coc.zkqcode.jar.ui.pages.single

import android.content.Context
import android.content.Intent
import android.os.Environment
import com.coc.zkqcode.core.ui.floatingwindows.ImagePickerActivity
import com.coc.zkqcode.core.util.fileactions.resetCustomBackground
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
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.ui.LanguageManager
import com.coc.zkqcode.jar.ui.Strings
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
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp), horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = if (GlobalVars.isAutoRunEnabled) Strings.timerRunning(InGameVars.autoRunTimer) else Strings.timerStopped,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 16.dp, end = 6.dp)
                )
                CustomButton(
                    text = Strings.stopTimer, onClick = { GlobalVars.isAutoRunEnabled = false })
                CustomButton(
                    text = Strings.log, onClick = { onShowAnnouncement() })
                CustomButton(
                    text = LanguageManager.toggleButtonText,
                    onClick = { LanguageManager.toggleLanguage() },
                    containerColor = AppColors.Teal
                )
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
                title = Strings.manualSwitch, subtitle = Strings.clickSwitch, dotColor = Color(0xFFED8936), onClick = {
                    AppStateManager.setMode(AppMode.SwitchAccount)
                    onSaveSuccess()
                }, modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                title = Strings.foundBug, subtitle = Strings.clickFeedback, dotColor = AppColors.Teal, onClick = {
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
                title = Strings.customBg, subtitle = Strings.selectImage, dotColor = AppColors.PrimaryLight, onClick = {
                    saveAndSetPlaying(false)
                    val intent = Intent(context, ImagePickerActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }, modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                title = Strings.resetBg, subtitle = Strings.restoreDefault, dotColor = AppColors.NavInactive, onClick = {
                    val success = resetCustomBackground(context)
                    showMsg(if (success) Strings.bgResetSuccess else Strings.bgResetFail)
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
                    text = Strings.tipChangeDevice, style = MaterialTheme.typography.labelMedium, color = AppColors.TextPrimary
                )
            }
            // Two-tile grid
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(bottom = 8.dp)
            ) {
                TipsActionTile(
                    text = Strings.clearMemory,
                    explain = Strings.clearMemoryExplain,
                    onClick = { cleanMemory() },
                    modifier = Modifier.weight(1f)
                )
                TipsActionTile(
                    text = Strings.cleanAllTitle, onClick = { cleanAllData() }, modifier = Modifier.weight(1f)
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
                    key = GLOBAL_SETTINGS.AUTO_UPDATE.key, options = listOf(Strings.autoUpdateOff, Strings.autoUpdateOnLaunch, Strings.autoUpdateRealtime)
                )
                SettingSwitchIcon(key = GLOBAL_SETTINGS.AUTO_START.key)
                SettingSwitchIcon(
                    key = GLOBAL_SETTINGS.DISABLE_RESTART_GAME.key,
                    explain = Strings.disableRestartExplain
                )
                SettingDropdown(
                    key = GLOBAL_SETTINGS.AFTER_KICK_OPTION.key, options = listOf(Strings.afterKickReconnect, Strings.afterKickSwitch, Strings.afterKickWait),
                    afterChange = { selectedIndex ->
                        if (selectedIndex != 0) {
                            GlobalVars.configStates[GLOBAL_SETTINGS.BATCH_CREATE_ACCOUNT.key]?.value = "0"
                        }
                    })
                SettingInputRow(key = GLOBAL_SETTINGS.DEVICE_REMARK.key)
                CustomButton(
                    text = Strings.importOldData,
                    onClick = { onShowImportOldData() },
                    marginTop = 6.dp
                )
                CustomButton(
                    text = Strings.syncTime,
                    onClick = { autoSyncSystemTime { msg -> showMsg(msg) } },
                    marginTop = 6.dp
                )
            }
        }
    }
}
