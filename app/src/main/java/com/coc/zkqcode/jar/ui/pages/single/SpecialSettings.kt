package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.ShellHelper
import com.coc.zkqcode.jar.code.universal.cloud.manualResetCloudExpiry
import com.coc.zkqcode.jar.code.universal.push.PushMessage
import com.coc.zkqcode.jar.code.universal.push.PushRouter
import com.coc.zkqcode.jar.ui.LanguageManager
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SettingDropdown
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.jar.ui.theme.AppColors
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun LazyListScope.specialSettings(onOpenWebDavManager: () -> Unit = {}) {
    item { SettingSection { BatchCreateAccount() } }
    item {
        SettingSection {
            val parentState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.UPGRADE_TIME_SCHEDULING.key) { mutableStateOf("0") }
            val parentEnabled = parentState.value == "1"
            SettingSwitchIcon(
                key = GLOBAL_SETTINGS.UPGRADE_TIME_SCHEDULING.key,
                explain = Strings.upgradeTimeSchedulingExplain,
                afterChange = { checked ->
                    if (checked) {
                        GlobalVars.configStates[GLOBAL_SETTINGS.RANDOM_SCHEDULING.key]?.value = "0"
                        GlobalVars.configStates[GLOBAL_SETTINGS.BATCH_CREATE_ACCOUNT.key]?.value = "0"
                    }
                })
            if (parentEnabled) {
                SettingInputRow(key = GLOBAL_SETTINGS.UTS_WAIT_POLL_SECONDS.key, suffix = Strings.secondUnit, numericOnly = true)
            }
        }
    }
    item { SettingSection { RandomScheduling() } }
    item {
        SettingSection {
            val parentState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.UPDATE_CN_GAME.key) { mutableStateOf("0") }
            val parentEnabled = parentState.value == "1"
            SettingSwitchIcon(key = GLOBAL_SETTINGS.UPDATE_CN_GAME.key, afterChange = { checked ->
                if (!checked) {
                    GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.ENHANCED_CN_UPDATE.key) { mutableStateOf("0") }.value = "0"
                }
            })
            if (parentEnabled) {
                SettingInputRow(key = GLOBAL_SETTINGS.MAX_DOWNLOAD_WAITING_TIME.key)

                val enhancedState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.ENHANCED_CN_UPDATE.key) { mutableStateOf("0") }
                val enhancedEnabled = enhancedState.value == "1"
                SettingSwitchIcon(key = GLOBAL_SETTINGS.ENHANCED_CN_UPDATE.key)
                if (enhancedEnabled) {
                    Text(
                        text = Strings.enhancedUpdateHint,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextPrimary.copy(alpha = 0.75f),
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                    SettingDropdown(
                        key = GLOBAL_SETTINGS.ENHANCED_CN_UPDATE_SOURCE.key,
                        options = listOf("无", Strings.enhancedSourceAppStore, Strings.enhancedSourceTencent)
                    )
                }
            }
        }
    }
    item {
        SettingSection {
            val state = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.TEMP_PROTECTION.key) { mutableStateOf("0") }
            val enabled = state.value == "1"
            SettingSwitchIcon(key = GLOBAL_SETTINGS.TEMP_PROTECTION.key)
            if (enabled) {
                SettingInputRow(key = GLOBAL_SETTINGS.TEMP_PAUSE_THRESHOLD.key)
                SettingInputRow(key = GLOBAL_SETTINGS.TEMP_RESUME_THRESHOLD.key)
            }
        }
    }
    // ── Scheduled Restart (定时重启) ─────────────────────────────────────────
    item {
        SettingSection {
            val restartState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.SCHEDULED_RESTART.key) {
                mutableStateOf("0")
            }
            val isRestartEnabled = restartState.value == "1"
            SettingSwitchIcon(
                key = GLOBAL_SETTINGS.SCHEDULED_RESTART.key,
                explain = Strings.scheduledRestartExplain
            )
            if (isRestartEnabled) {
                SettingInputRow(key = GLOBAL_SETTINGS.RESTART_INTERVAL.key)
            }
        }
    }
    // ── Push Notification ─────────────────────────────────────────────────
    item {
        var pushSectionExpanded by remember { mutableStateOf(false) } // collapsed by default
        // Single unified card: header row on top, content expands inside the same card
        SettingSection {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { pushSectionExpanded = !pushSectionExpanded },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = Strings.serverchanSectionTitle, style = MaterialTheme.typography.labelMedium, color = AppColors.TextPrimary)
                    Text(
                        text = if (pushSectionExpanded) "▾" else "▸",
                        fontSize = 20.sp,
                        color = AppColors.TextPrimary.copy(alpha = 0.6f),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
                Text(
                    text = Strings.serverchanSectionDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextPrimary.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                AnimatedVisibility(visible = pushSectionExpanded) {
                    Column {
                        // ── Platform credentials + test connection ──
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(1.dp, AppColors.GlassBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Column {
                                SettingDropdown(key = GLOBAL_SETTINGS.PUSH_PROVIDER.key, options = listOf(Strings.pushProviderServerChan, Strings.pushProviderWxPusher, Strings.pushProviderPushPlus, Strings.pushProviderTelegram, Strings.pushProviderAstrBot))
                                val provider = GlobalVars.configStates[GLOBAL_SETTINGS.PUSH_PROVIDER.key]?.value ?: "0"
                                when (provider) {
                                    "0" -> SettingInputRow(key = GLOBAL_SETTINGS.SERVERCHAN_SENDKEY.key)
                                    "1" -> { SettingInputRow(key = GLOBAL_SETTINGS.WXPUSHER_APP_TOKEN.key); SettingInputRow(key = GLOBAL_SETTINGS.WXPUSHER_UID.key) }
                                    "2" -> SettingInputRow(key = GLOBAL_SETTINGS.PUSHPLUS_TOKEN.key)
                                    "3" -> { SettingInputRow(key = GLOBAL_SETTINGS.TELEGRAM_BOT_TOKEN.key); SettingInputRow(key = GLOBAL_SETTINGS.TELEGRAM_CHAT_ID.key) }
                                    "4" -> {
                                        SettingInputRow(key = GLOBAL_SETTINGS.ASTRBOT_BASE_URL.key)
                                        SettingInputRow(key = GLOBAL_SETTINGS.ASTRBOT_API_KEY.key)
                                        SettingInputRow(key = GLOBAL_SETTINGS.ASTRBOT_TARGET_ID.key)
                                        SettingDropdown(key = GLOBAL_SETTINGS.ASTRBOT_MSG_TYPE.key, options = listOf(Strings.astrbotMsgTypePrivate, Strings.astrbotMsgTypeGroup))
                                        Text(
                                            text = Strings.astrbotHint,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = AppColors.TextPrimary.copy(alpha = 0.75f),
                                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                        )
                                        // Plugin communication: App only configures connection info (§13.5)
                                        SettingSwitchIcon(key = GLOBAL_SETTINGS.ZKQ_ENABLED.key)
                                        if (GlobalVars.configStates[GLOBAL_SETTINGS.ZKQ_ENABLED.key]?.value != "0") {
                                            SettingInputRow(key = GLOBAL_SETTINGS.ZKQ_SERVER_URL.key)
                                            SettingInputRow(key = GLOBAL_SETTINGS.ZKQ_TOKEN.key)
                                        }
                                    }
                                }
                                // ── Test connection (verifies the push platform credentials are configured) ──
                                val scope = rememberCoroutineScope()
                                CustomButton(
                                    text = Strings.serverchanTestBtn,
                                    marginTop = 6.dp,
                                    onClick = {
                                        scope.launch {
                                            ShowMessage(Strings.serverchanTestSending, isChecking = false)
                                            val error = withContext(Dispatchers.IO) {
                                                PushRouter.send(
                                                    PushMessage(
                                                        title = Strings.serverchanTestBtn,
                                                        desp = if (LanguageManager.isChinese) "这是一条测试通知" else "This is a test notification"
                                                    )
                                                )
                                            }
                                            if (error == null) {
                                                ShowMessage(Strings.serverchanTestSuccess, isChecking = false)
                                            } else {
                                                ShowMessage(Strings.serverchanTestFailed(error), isChecking = false)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                        // ── Notification switches + cloud expiry ──
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(1.dp, AppColors.GlassBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Column {
                                SettingSwitchIcon(key = GLOBAL_SETTINGS.SERVERCHAN_LOGIN_NOTIFY.key)
                                SettingSwitchIcon(key = GLOBAL_SETTINGS.SERVERCHAN_KICK_NOTIFY.key)
                                SettingSwitchIcon(key = GLOBAL_SETTINGS.SERVERCHAN_WAR_NOTIFY.key)
                                val warNotifyOn = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.SERVERCHAN_WAR_NOTIFY.key) { mutableStateOf("0") }.value == "1"
                                if (warNotifyOn) {
                                    SettingDropdown(
                                        key = GLOBAL_SETTINGS.SERVERCHAN_WAR_NOTIFY_MODE.key,
                                        options = listOf(Strings.notifyModeManual, Strings.notifyModeAuto)
                                    )
                                }
                                SettingSwitchIcon(key = GLOBAL_SETTINGS.SERVERCHAN_LEAGUE_NOTIFY.key)
                                val leagueNotifyOn = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.SERVERCHAN_LEAGUE_NOTIFY.key) { mutableStateOf("0") }.value == "1"
                                if (leagueNotifyOn) {
                                    SettingDropdown(
                                        key = GLOBAL_SETTINGS.SERVERCHAN_LEAGUE_NOTIFY_MODE.key,
                                        options = listOf(Strings.notifyModeManual, Strings.notifyModeAuto)
                                    )
                                }
                                SettingSwitchIcon(key = GLOBAL_SETTINGS.SERVERCHAN_UPGRADE_NOTIFY.key)
                                val scheduleState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.SERVERCHAN_UPGRADE_SCHEDULE_NOTIFY.key) { mutableStateOf("0") }
                                val scheduleEnabled = scheduleState.value == "1"
                                SettingSwitchIcon(key = GLOBAL_SETTINGS.SERVERCHAN_UPGRADE_SCHEDULE_NOTIFY.key)
                                if (scheduleEnabled) { SettingInputRow(key = GLOBAL_SETTINGS.SERVERCHAN_UPGRADE_ADVANCE.key) }
                                val cloudState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.CLOUD_EXPIRE_NOTIFY.key) { mutableStateOf("0") }
                                val cloudEnabled = cloudState.value == "1"
                                SettingSwitchIcon(key = GLOBAL_SETTINGS.CLOUD_EXPIRE_NOTIFY.key)
                                if (cloudEnabled) {
                                    val dateState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.CLOUD_EXPIRE_DATE.key) { mutableStateOf("") }
                                    val parts = dateState.value.split("-")
                                    val yearSel = remember { mutableStateOf(parts.getOrNull(0)?.toIntOrNull() ?: 0) }
                                    val monthSel = remember { mutableStateOf(parts.getOrNull(1)?.toIntOrNull() ?: 0) }
                                    val daySel = remember { mutableStateOf(parts.getOrNull(2)?.toIntOrNull() ?: 0) }
                                    // Keep dropdowns in sync when the date changes programmatically (manual/auto reset)
                                    LaunchedEffect(dateState.value) {
                                        val p = dateState.value.split("-")
                                        yearSel.value = p.getOrNull(0)?.toIntOrNull() ?: 0
                                        monthSel.value = p.getOrNull(1)?.toIntOrNull() ?: 0
                                        daySel.value = p.getOrNull(2)?.toIntOrNull() ?: 0
                                    }
                                    fun writeDate() {
                                        if (yearSel.value > 0 && monthSel.value > 0 && daySel.value > 0)
                                            dateState.value = "${yearSel.value}-${monthSel.value.toString().padStart(2,'0')}-${daySel.value.toString().padStart(2,'0')}"
                                    }
                                    Row(modifier = Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = Schema.getDisplayName(GLOBAL_SETTINGS.CLOUD_EXPIRE_DATE.key) + "：", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(end = 4.dp))
                                        SimpleDropdown(sel = if (yearSel.value > 0) "${yearSel.value}" else "-", opts = (2025..2040).map { it.toString() }, onSel = { yearSel.value = it.toIntOrNull() ?: yearSel.value; writeDate() })
                                        Text(text = "年", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 2.dp))
                                        SimpleDropdown(sel = if (monthSel.value > 0) "${monthSel.value}" else "-", opts = (1..12).map { it.toString() }, onSel = { monthSel.value = it.toIntOrNull() ?: monthSel.value; writeDate() })
                                        Text(text = "月", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 2.dp))
                                        SimpleDropdown(sel = if (daySel.value > 0) "${daySel.value}" else "-", opts = (1..31).map { it.toString() }, onSel = { daySel.value = it.toIntOrNull() ?: daySel.value; writeDate() })
                                        Text(text = "日", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 2.dp))
                                    }
                                    // Auto reset switch: user decides whether expired dates roll forward automatically
                                    SettingSwitchIcon(key = GLOBAL_SETTINGS.CLOUD_EXPIRE_AUTO_RESET.key)
                                    val autoResetOn = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.CLOUD_EXPIRE_AUTO_RESET.key) { mutableStateOf("1") }.value == "1"
                                    if (autoResetOn) {
                                        // Reset days: 1-7 kept, above that only payment-cycle lengths (7/30/90/180/365)
                                        Row(modifier = Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = Schema.getDisplayName(GLOBAL_SETTINGS.CLOUD_EXPIRE_RESET_DAYS.key) + "：", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(end = 4.dp))
                                            val resetDaysState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.CLOUD_EXPIRE_RESET_DAYS.key) { mutableStateOf("30") }
                                            val resetOpts = (1..7).map { it.toString() } + listOf("30", "90", "180", "365")
                                            val resetDaysSel = resetDaysState.value.takeIf { it in resetOpts } ?: "-"
                                            SimpleDropdown(sel = resetDaysSel, opts = resetOpts, onSel = { resetDaysState.value = it })
                                        }
                                    }
                                    SettingInputRow(key = GLOBAL_SETTINGS.CLOUD_EXPIRE_ADVANCE_DAYS.key, numericOnly = true)
                                    val cloudScope = rememberCoroutineScope()
                                    CustomButton(
                                        text = Strings.cloudExpireResetBtn,
                                        marginTop = 6.dp,
                                        onClick = {
                                            cloudScope.launch {
                                                val ok = withContext(Dispatchers.IO) { manualResetCloudExpiry() }
                                                ShowMessage(
                                                    if (ok) Strings.cloudExpireResetDone else Strings.cloudExpireResetFailed,
                                                    isChecking = false
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    // ── WebDAV File Manager ──────────────────────────────────────────────────
    item {
        SettingSection {
            Row(modifier = Modifier.padding(bottom = 4.dp)) {
                Text(text = Strings.webdavSectionTitle, style = MaterialTheme.typography.labelMedium, color = AppColors.TextPrimary)
            }
            Text(
                text = Strings.webdavSectionDesc,
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextPrimary.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            CustomButton(text = Strings.webdavOpenFileManager, marginTop = 6.dp, onClick = onOpenWebDavManager)
        }
    }
    // ── Screen Resolution ─────────────────────────────────────────────────────
    item {
        SettingSection {
            Row(modifier = Modifier.padding(bottom = 4.dp)) {
                Text(text = Strings.screenResolutionSectionTitle, style = MaterialTheme.typography.labelMedium, color = AppColors.TextPrimary)
            }
            Text(
                text = Strings.screenResolutionWarning,
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextPrimary.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            val scope = rememberCoroutineScope()
            var statusMessage by remember { mutableStateOf("") }
            CustomButton(
                text = Strings.screenResolutionSetBtn,
                marginTop = 6.dp,
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        ShellHelper.runBlockingNoOutput("wm size 720x1280")
                        ShellHelper.runBlockingNoOutput("wm density 320")
                        statusMessage = Strings.screenResolutionSetSuccess
                    }
                }
            )
            CustomButton(
                text = Strings.screenResolutionResetBtn,
                marginTop = 6.dp,
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        ShellHelper.runBlockingNoOutput("wm size reset")
                        ShellHelper.runBlockingNoOutput("wm density reset")
                        statusMessage = Strings.screenResolutionResetSuccess
                    }
                }
            )
            if (statusMessage.isNotEmpty()) {
                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.Success,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
    legalDisclaimers()
    item {
        val context = LocalContext.current; val cr = rememberCoroutineScope()
        CustomButton(text = Strings.copyLogs, onClick = {
            cr.launch(Dispatchers.IO) {
                val src = "${context.filesDir.absolutePath}/logs"; val sd = Environment.getExternalStorageDirectory().path; val dst = "$sd/zkqFiles"
                ShellHelper.runBlockingNoOutput("mkdir -p \"$dst\" && cp -rf \"$src\" \"$dst/\"")
            }
        })
    }
}

@Composable
private fun SimpleDropdown(sel: String, opts: List<String>, onSel: (String) -> Unit, mod: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = mod.wrapContentSize(Alignment.TopStart)) {
        Button(onClick = { expanded = true }, shape = RoundedCornerShape(6.dp), modifier = Modifier.height(26.dp), contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp), colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)) {
            Text(sel, style = MaterialTheme.typography.labelSmall, maxLines = 1)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.height(16.dp))
        }
        if (expanded) {
            Popup(onDismissRequest = { expanded = false }, properties = PopupProperties(focusable = true)) {
                Surface(shape = RoundedCornerShape(8.dp), color = AppColors.CardBg, shadowElevation = 4.dp, modifier = Modifier.heightIn(max = 200.dp)) {
                    Column(modifier = Modifier.padding(4.dp).verticalScroll(rememberScrollState())) {
                        opts.forEach { opt ->
                            Text(text = opt, style = MaterialTheme.typography.labelSmall, color = if (opt == sel) AppColors.Primary else AppColors.TextPrimary, modifier = Modifier.clickable { onSel(opt); expanded = false }.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                    }
                }
            }
        }
    }
}
