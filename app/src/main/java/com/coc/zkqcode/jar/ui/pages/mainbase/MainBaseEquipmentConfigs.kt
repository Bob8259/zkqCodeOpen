package com.coc.zkqcode.jar.ui.pages.mainbase

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SettingToggleButton
import com.coc.zkqcode.jar.ui.theme.AppColors
import com.coc.zkqcode.jar.ui.schema.Schema.MAIN_BASE_GEARS

@Composable
fun MainBaseGearConfigs(index: Int) {
    // Render gear toggles grouped by hero so new gears appear automatically.
    val heroGroups = MAIN_BASE_GEARS.heroGroups
    val isExpanded = remember { mutableStateOf(false) }

    Column {
        Row {
            CustomButton(
                text = if (isExpanded.value) Strings.collapseList else Strings.expandList,
                onClick = { isExpanded.value = !isExpanded.value },
                containerColor = AppColors.Teal
            )
        }

        AnimatedVisibility(visible = isExpanded.value) {
            Column {
                heroGroups.forEach { (heroKey, gears) ->
                    // Hero group header
                    Text(
                        text = Strings.getHeroName(heroKey),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    FlowRow(
                        modifier = Modifier
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        gears.forEach { gear ->
                            SettingToggleButton(key = "${gear.key}_c${index}", isEpic = gear.isEpic)
                        }
                    }
                }
            }
        }
    }
}
