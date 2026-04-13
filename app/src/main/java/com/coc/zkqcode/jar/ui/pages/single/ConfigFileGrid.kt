@file:Suppress("FunctionName")

package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.jar.ui.theme.AppColors
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.schema.ConfigManager
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS

// Grid of clickable cards for each config file
@Composable
fun ConfigFileGrid(
    configCount: Int,
    onConfigSelected: (Int) -> Unit
) {
    SettingSection {
        Column {
            SettingInputRow(
                key = GLOBAL_SETTINGS.CONFIG_COUNT.key,
                afterChange = { newValue ->
                    val newCount = newValue.toIntOrNull()
                    if (newCount != null) {
                        ConfigManager.expandProfileConfigs(newCount)
                    }
                },
            )
            Text(
                text = "点击方格调整配置文件。调整完成后，回到“账号设置”，给每个账号分配所需的配置文件。",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                for (i in 1..configCount) {
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp, top = 8.dp)
                            .widthIn(min = 90.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColors.PrimaryLight)
                            .clickable { onConfigSelected(i) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "配置文件$i",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
