package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.mutableStateOf
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingDropdown
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import android.os.Environment
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.coc.zkqcode.jar.code.ShellHelper
import com.coc.zkqcode.jar.code.auth.ServerChanHelper
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.core.util.basic.ShowMessage
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.jar.ui.theme.AppColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun LazyListScope.specialSettings() {
    item {
        SettingSection {
            BatchCreateAccount()
        }
    }
    item {
        SettingSection {
            RandomScheduling()
        }
    }
    item {
        SettingSection {
            val updateCnGameState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.UPDATE_CN_GAME.key) {
                mutableStateOf("0")
            }
            val isUpdateCnGameEnabled = updateCnGameState.value == "1"
            SettingSwitchIcon(key = GLOBAL_SETTINGS.UPDATE_CN_GAME.key)
            if (isUpdateCnGameEnabled) {
                SettingInputRow(key = GLOBAL_SETTINGS.MAX_DOWNLOAD_WAITING_TIME.key)
            }
        }
    }
    item {
        SettingSection {
            val tempState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.TEMP_PROTECTION.key) {
                mutableStateOf("0")
            }
            val isTempEnabled = tempState.value == "1"
            SettingSwitchIcon(key = GLOBAL_SETTINGS.TEMP_PROTECTION.key)
            if (isTempEnabled) {
                SettingInputRow(key = GLOBAL_SETTINGS.TEMP_PAUSE_THRESHOLD.key)
                SettingInputRow(key = GLOBAL_SETTINGS.TEMP_RESUME_THRESHOLD.key)
            }
        }
    }
    item {
        SettingSection {
            val restartState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.SCHEDULED_RESTART.key) {
                mutableStateOf("0")
            }
            val isRestartEnabled = restartState.value == "1"
            SettingSwitchIcon(key = GLOBAL_SETTINGS.SCHEDULED_RESTART.key)
            if (isRestartEnabled) {
                SettingInputRow(key = GLOBAL_SETTINGS.RESTART_INTERVAL.key)
            }
        }
    }
    // ── Push Notification (Server酱³) ────────────────────────────────────
    item {
        SettingSection {
            Row(modifier = Modifier.padding(bottom = 4.dp)) {
                Text(
                    text = Strings.serverchanSectionTitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = AppColors.TextPrimary
                )
            }
            SettingDropdown(
                key = GLOBAL_SETTINGS.PUSH_PROVIDER.key,
                options = listOf(Strings.pushProviderServerChan, Strings.pushProviderWxPusher, Strings.pushProviderPushPlus, Strings.pushProviderTelegram)
            )
            // Provider-specific credential fields
            val selectedProvider = GlobalVars.configStates[GLOBAL_SETTINGS.PUSH_PROVIDER.key]?.value ?: "0"
            when (selectedProvider) {
                "0" -> SettingInputRow(key = GLOBAL_SETTINGS.SERVERCHAN_SENDKEY.key)
                "1" -> {
                    SettingInputRow(key = GLOBAL_SETTINGS.WXPUSHER_APP_TOKEN.key)
                    SettingInputRow(key = GLOBAL_SETTINGS.WXPUSHER_UID.key)
                }
                "2" -> SettingInputRow(key = GLOBAL_SETTINGS.PUSHPLUS_TOKEN.key)
                "3" -> {
                    SettingInputRow(key = GLOBAL_SETTINGS.TELEGRAM_BOT_TOKEN.key)
                    SettingInputRow(key = GLOBAL_SETTINGS.TELEGRAM_CHAT_ID.key)
                }
            }
            SettingSwitchIcon(key = GLOBAL_SETTINGS.SERVERCHAN_LOGIN_NOTIFY.key)
            SettingSwitchIcon(key = GLOBAL_SETTINGS.SERVERCHAN_KICK_NOTIFY.key)
            SettingSwitchIcon(key = GLOBAL_SETTINGS.SERVERCHAN_WAR_NOTIFY.key)
            SettingSwitchIcon(key = GLOBAL_SETTINGS.SERVERCHAN_LEAGUE_NOTIFY.key)
            SettingSwitchIcon(key = GLOBAL_SETTINGS.SERVERCHAN_UPGRADE_NOTIFY.key)
            SettingInputRow(key = GLOBAL_SETTINGS.SERVERCHAN_UPGRADE_ADVANCE.key)
            val scope = rememberCoroutineScope()
            CustomButton(
                text = Strings.serverchanTestBtn,
                marginTop = 6.dp,
                onClick = {
                    val pushProvider = GlobalVars.configStates[GLOBAL_SETTINGS.PUSH_PROVIDER.key]?.value ?: "0"
                    when (pushProvider) {
                        "0" -> {
                            val sendKey = GlobalVars.configStates[GLOBAL_SETTINGS.SERVERCHAN_SENDKEY.key]?.value.orEmpty()
                            if (sendKey.isBlank()) {
                                ShowMessage(Strings.serverchanSendKeyEmpty, isChecking = false)
                                return@CustomButton
                            }
                        }
                        "1" -> {
                            val token = GlobalVars.configStates[GLOBAL_SETTINGS.WXPUSHER_APP_TOKEN.key]?.value.orEmpty()
                            val uid = GlobalVars.configStates[GLOBAL_SETTINGS.WXPUSHER_UID.key]?.value.orEmpty()
                            if (token.isBlank() || uid.isBlank()) {
                                ShowMessage("请先填写 WxPusher AppToken 和 UID", isChecking = false)
                                return@CustomButton
                            }
                        }
                        "2" -> {
                            val token = GlobalVars.configStates[GLOBAL_SETTINGS.PUSHPLUS_TOKEN.key]?.value.orEmpty()
                            if (token.isBlank()) {
                                ShowMessage("请先填写 PushPlus Token", isChecking = false)
                                return@CustomButton
                            }
                        }
                        "3" -> {
                            val token = GlobalVars.configStates[GLOBAL_SETTINGS.TELEGRAM_BOT_TOKEN.key]?.value.orEmpty()
                            val chatId = GlobalVars.configStates[GLOBAL_SETTINGS.TELEGRAM_CHAT_ID.key]?.value.orEmpty()
                            if (token.isBlank() || chatId.isBlank()) {
                                ShowMessage("请先填写 Telegram BotToken 和 ChatID", isChecking = false)
                                return@CustomButton
                            }
                        }
                    }
                    scope.launch {
                        val providerLabel = when (pushProvider) {
                            "1" -> "WxPusher"
                            "2" -> "PushPlus"
                            "3" -> "Telegram"
                            else -> "Server酱³"
                        }
                        val err = ServerChanHelper.sendNotification(
                            title = Strings.serverchanTestBtn,
                            desp = "This is a test message from ZKQ Assistant."
                        )
                        ShowMessage(
                            if (err == null) "${providerLabel} 推送测试成功 ✓" else "${providerLabel} 推送失败: $err",
                            isChecking = false
                        )
                    }
                }
            )
        }
    }
    legalDisclaimers()
    item {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        CustomButton(
            text = Strings.copyLogs,
            onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    val srcDir = "${context.filesDir.absolutePath}/logs"
                    val sdPath = Environment.getExternalStorageDirectory().path
                    val destDir = "$sdPath/zkqFiles"
                    val copyCmd = "mkdir -p \"$destDir\" && cp -rf \"$srcDir\" \"$destDir/\""
                    ShellHelper.runBlockingNoOutput(copyCmd)
                }
            }
        )
    }
}
