package com.coc.zkqcode.jar.ui.pages.mainbase

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SettingToggleButton
import com.coc.zkqcode.jar.ui.schema.Schema.MAIN_BASE_SETTINGS
import com.coc.zkqcode.jar.ui.schema.SettingDef
import com.coc.zkqcode.jar.ui.theme.AppColors

/**
 * Raid & League Medal purchase item list.
 * Each expands/collapses independently.
 */
@Composable
fun MainBaseShopConfigs(index: Int) {
    val raidItems = listOf(
        MAIN_BASE_SETTINGS.BUY_GOLD_ORE_WITH_RAID_MEDAL,
        MAIN_BASE_SETTINGS.BUY_PURPLE_ORE_WITH_RAID_MEDAL,
        MAIN_BASE_SETTINGS.BUY_CLOCK_TOWER_POTION_WITH_RAID_MEDAL,
        MAIN_BASE_SETTINGS.BUY_RING_OF_WALL_WITH_RAID_MEDAL,
        MAIN_BASE_SETTINGS.BUY_RESEARCH_POTION_WITH_RAID_MEDAL,
        MAIN_BASE_SETTINGS.BUY_RESOURCE_POTION_WITH_RAID_MEDAL
    )
    val leagueItems = listOf(
        MAIN_BASE_SETTINGS.BUY_RESEARCH_POTION_WITH_LEAGUE_MEDAL,
        MAIN_BASE_SETTINGS.BUY_BUILDER_POTION_WITH_LEAGUE_MEDAL
    )

    Column {
        // ==== Raid Medal Purchases ====
        ShopSection(
            title = Strings.raidMedalShop,
            items = raidItems,
            index = index
        )

        // ==== League Medal Purchases ====
        ShopSection(
            title = Strings.leagueMedalShop,
            items = leagueItems,
            index = index
        )
    }
}

@Composable
private fun ShopSection(
    title: String,
    items: List<SettingDef>,
    index: Int
) {
    val isExpanded = remember { mutableStateOf(false) }

    // Select all for this section
    val selectAll = {
        items.forEach { item ->
            val key = "${item.key}_c${index}"
            if (!GlobalVars.configStates.containsKey(key)) {
                GlobalVars.configStates[key] = mutableStateOf("1")
            } else {
                GlobalVars.configStates[key]!!.value = "1"
            }
        }
    }

    // Invert selections for this section
    val invertSelection = {
        items.forEach { item ->
            val key = "${item.key}_c${index}"
            val currentState = GlobalVars.configStates[key]?.value ?: "1"
            val newValue = if (currentState == "1") "0" else "1"
            if (!GlobalVars.configStates.containsKey(key)) {
                GlobalVars.configStates[key] = mutableStateOf(newValue)
            } else {
                GlobalVars.configStates[key]!!.value = newValue
            }
        }
    }

    Column(modifier = Modifier.padding(top = 4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row {
            CustomButton(
                text = if (isExpanded.value) Strings.collapse(title) else Strings.expand(title),
                onClick = { isExpanded.value = !isExpanded.value },
                containerColor = AppColors.Teal
            )
            AnimatedVisibility(visible = isExpanded.value) {
                Row {
                    CustomButton(text = Strings.selectAll, onClick = selectAll)
                    CustomButton(text = Strings.invertSelection, onClick = invertSelection)
                }
            }
        }

        AnimatedVisibility(visible = isExpanded.value) {
            FlowRow(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                items.forEach { setting ->
                    SettingToggleButton(key = "${setting.key}_c${index}")
                }
            }
        }
    }
}
