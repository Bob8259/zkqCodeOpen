package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.components.SectionBanner
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.jar.ui.theme.AppColors

// Data class holding the 5 config key names for one time group
private data class GroupKeys(
    val battleRatio: String,
    val stopProb: String,
    val continueMin: String,
    val continueMax: String,
    val noWorker: String,
    val fullRes: String,
    val accounts: String
)

private val GROUP_KEYS = listOf(
    GroupKeys(
        battleRatio = GLOBAL_SETTINGS.RS_BATTLE_RATIO_DAWN.key,
        stopProb = GLOBAL_SETTINGS.RS_STOP_PROB_DAWN.key,
        continueMin = GLOBAL_SETTINGS.RS_CONTINUE_MIN_DAWN.key,
        continueMax = GLOBAL_SETTINGS.RS_CONTINUE_MAX_DAWN.key,
        noWorker = GLOBAL_SETTINGS.RS_NO_WORKER_DAWN.key,
        fullRes = GLOBAL_SETTINGS.RS_FULL_RES_DAWN.key,
        accounts = GLOBAL_SETTINGS.RS_ACCOUNTS_DAWN.key
    ),
    GroupKeys(
        battleRatio = GLOBAL_SETTINGS.RS_BATTLE_RATIO_MORNING.key,
        stopProb = GLOBAL_SETTINGS.RS_STOP_PROB_MORNING.key,
        continueMin = GLOBAL_SETTINGS.RS_CONTINUE_MIN_MORNING.key,
        continueMax = GLOBAL_SETTINGS.RS_CONTINUE_MAX_MORNING.key,
        noWorker = GLOBAL_SETTINGS.RS_NO_WORKER_MORNING.key,
        fullRes = GLOBAL_SETTINGS.RS_FULL_RES_MORNING.key,
        accounts = GLOBAL_SETTINGS.RS_ACCOUNTS_MORNING.key
    ),
    GroupKeys(
        battleRatio = GLOBAL_SETTINGS.RS_BATTLE_RATIO_AFTERNOON.key,
        stopProb = GLOBAL_SETTINGS.RS_STOP_PROB_AFTERNOON.key,
        continueMin = GLOBAL_SETTINGS.RS_CONTINUE_MIN_AFTERNOON.key,
        continueMax = GLOBAL_SETTINGS.RS_CONTINUE_MAX_AFTERNOON.key,
        noWorker = GLOBAL_SETTINGS.RS_NO_WORKER_AFTERNOON.key,
        fullRes = GLOBAL_SETTINGS.RS_FULL_RES_AFTERNOON.key,
        accounts = GLOBAL_SETTINGS.RS_ACCOUNTS_AFTERNOON.key
    ),
    GroupKeys(
        battleRatio = GLOBAL_SETTINGS.RS_BATTLE_RATIO_NIGHT.key,
        stopProb = GLOBAL_SETTINGS.RS_STOP_PROB_NIGHT.key,
        continueMin = GLOBAL_SETTINGS.RS_CONTINUE_MIN_NIGHT.key,
        continueMax = GLOBAL_SETTINGS.RS_CONTINUE_MAX_NIGHT.key,
        noWorker = GLOBAL_SETTINGS.RS_NO_WORKER_NIGHT.key,
        fullRes = GLOBAL_SETTINGS.RS_FULL_RES_NIGHT.key,
        accounts = GLOBAL_SETTINGS.RS_ACCOUNTS_NIGHT.key
    )
)

private val GROUP_TITLES = listOf(
    "凌晨组  0:00 - 6:00",
    "上午组  6:00 - 12:00",
    "下午组  12:00 - 18:00",
    "夜晚组  18:00 - 24:00"
)

@Composable
fun RandomScheduling() {
    val rsState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.RANDOM_SCHEDULING.key) {
        mutableStateOf("0")
    }
    val isEnabled = rsState.value == "1"

    SettingSwitchIcon(
        key = GLOBAL_SETTINGS.RANDOM_SCHEDULING.key,
        explain = "启用随机上号后，脚本将根据当前时间段，从对应组的账号中随机选取顺序依次运行。每个账号运行完毕后，根据设定的概率决定是否继续对战或直接下线，并按对战时间比例计算等待时间后再运行下一个账号。\n\n随机上号与批量创号不能同时启用。",
        afterChange = { checked ->
            if (checked) {
                // Mutual exclusion: disable batch create when random scheduling is enabled
                GlobalVars.configStates[GLOBAL_SETTINGS.BATCH_CREATE_ACCOUNT.key]?.value = "0"
            }
        }
    )

    AnimatedVisibility(visible = isEnabled) {
        Column {
            GROUP_KEYS.forEachIndexed { index, keys ->
                TimeGroupSection(
                    title = GROUP_TITLES[index],
                    keys = keys
                )
            }
        }
    }
}

@Composable
private fun TimeGroupSection(title: String, keys: GroupKeys) {
    var expanded by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(top = 4.dp)) {
        SectionBanner(title = title, isExpanded = expanded, onClick = { expanded = !expanded })

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)) {
                // Battle time ratio
                SettingInputRow(key = keys.battleRatio, suffix = "%")

                // Stop probability row
                FlowRow(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    SettingInputRow(key = keys.stopProb, topPadding = 0.dp, suffix = "%")
                }

                // Continue battle range row
                FlowRow(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Text(
                        text = "剩余概率随机持续对战",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .align(Alignment.CenterVertically)
                    )
                    SettingInputRow(key = keys.continueMin, topPadding = 0.dp)
                    Text(
                        text = "-",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .align(Alignment.CenterVertically)
                    )
                    SettingInputRow(key = keys.continueMax, topPadding = 0.dp)
                    Text(
                        text = "分钟后下线",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .align(Alignment.CenterVertically)
                    )
                }

                // No worker / full resources toggles
                SettingSwitchIcon(key = keys.noWorker)
                SettingSwitchIcon(key = keys.fullRes)

                // Account selector
                AccountGroupSelector(accountsKey = keys.accounts)
            }
        }
    }
}

@Composable
private fun AccountGroupSelector(accountsKey: String) {
    val accountsState = GlobalVars.configStates.getOrPut(accountsKey) { mutableStateOf("") }
    val accountCountStr = GlobalVars.configStates[GLOBAL_SETTINGS.ACCOUNT_COUNT.key]?.value ?: "2"
    val accountCount = accountCountStr.toIntOrNull()?.coerceAtLeast(1) ?: 2

    // Parse current selected IDs from comma-separated string
    fun parseSelected(): Set<Int> {
        return accountsState.value
            .split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()
    }

    fun toggleAccount(id: Int) {
        GlobalVars.isAutoRunEnabled = false
        val current = parseSelected().toMutableSet()
        if (id in current) current.remove(id) else current.add(id)
        accountsState.value = current.sorted().joinToString(",")
    }

    Column(modifier = Modifier.padding(top = 6.dp)) {
        Text(
            text = "选择加入本组的账号：",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val selected = parseSelected()
            for (id in 1..accountCount) {
                val isSelected = id in selected
                val bgColor = if (isSelected) AppColors.Primary else AppColors.Primary.copy(alpha = 0.10f)
                val borderColor = if (isSelected) AppColors.Primary else AppColors.PrimaryLight
                val textColor = if (isSelected) Color.White else AppColors.TextPrimary

                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .background(bgColor, RoundedCornerShape(8.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                        .clickable { toggleAccount(id) }
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = id.toString(),
                        color = textColor,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}
