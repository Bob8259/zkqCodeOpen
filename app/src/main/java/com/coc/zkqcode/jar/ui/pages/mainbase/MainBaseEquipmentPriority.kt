package com.coc.zkqcode.jar.ui.pages.mainbase

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.ui.reorderable.ReorderableItem
import com.coc.zkqcode.core.ui.reorderable.rememberReorderableLazyGridState
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.pages.homescreen.BackgroundImage
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.jar.ui.schema.SettingDef
import com.coc.zkqcode.jar.ui.schema.Schema.MAIN_BASE_EQUIPMENTS
import com.coc.zkqcode.jar.ui.schema.details.MainBaseEquipmentPriorities
import com.coc.zkqcode.jar.ui.theme.AppColors
import kotlinx.coroutines.launch

private val equipmentPriorityHeroOrder = listOf(
    "barbarian_king",
    "archer_queen",
    "minion_prince",
    "grand_warden",
    "royal_champion",
    "dragon_duke"
)

@Composable
fun MainBaseEquipmentPriority(index: Int, onSaveSuccess: () -> Unit) {
    val groupsByHero = MAIN_BASE_EQUIPMENTS.heroGroups.associate { it.first to it.second }
    val heroGroups = equipmentPriorityHeroOrder.mapNotNull { heroKey ->
        groupsByHero[heroKey]?.let { heroKey to it }
    }
    var selectedHeroKey by remember(index) { mutableStateOf(heroGroups.firstOrNull()?.first.orEmpty()) }
    val equipmentOrders = remember(index) {
        mutableStateMapOf<String, List<SettingDef>>().apply {
            heroGroups.forEach { (heroKey, equipments) ->
                put(heroKey, equipments.sortedBy { equipment -> priorityFor(equipment, index) })
            }
        }
    }
    val currentEquipmentOrder = equipmentOrders[selectedHeroKey].orEmpty()
    val lazyGridState = rememberLazyGridState()
    val reorderableState = rememberReorderableLazyGridState(lazyGridState) { from, to ->
        val reordered = currentEquipmentOrder.toMutableList().apply {
            val fromIndex = indexOfFirst { it.key == from.key }
            val toIndex = indexOfFirst { it.key == to.key }
            if (fromIndex != -1 && toIndex != -1) {
                add(toIndex, removeAt(fromIndex))
            }
        }
        equipmentOrders[selectedHeroKey] = reordered
    }
    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .background(AppColors.Background)
        ) {
            HeroPriorityHeader(
                heroGroups = heroGroups,
                selectedHeroKey = selectedHeroKey,
                onHeroSelected = { selectedHeroKey = it }
            )

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 112.dp),
                modifier = Modifier.weight(1f),
                state = lazyGridState,
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(currentEquipmentOrder, key = { it.key }) { equipment ->
                    ReorderableItem(reorderableState, key = equipment.key) { _ ->
                        EquipmentPriorityButton(
                            equipment = equipment,
                            index = index,
                            modifier = Modifier.longPressDraggableHandle()
                        )
                    }
                }
            }

            CustomButton(
                text = if (isSaving) Strings.savingWait else Strings.done,
                enable = !isSaving,
                onClick = {
                    isSaving = true
                    scope.launch {
                        equipmentOrders.forEach { (_, equipments) ->
                            equipments.forEachIndexed { itemIndex, equipment ->
                                val priorityKey = "${MainBaseEquipmentPriorities.keyFor(equipment.key)}_c$index"
                                GlobalVars.configStates.getOrPut(priorityKey) {
                                    mutableStateOf((itemIndex + 1).toString())
                                }.value = (itemIndex + 1).toString()
                            }
                        }
                        isSaving = false
                        onSaveSuccess()
                    }
                },
                containerColor = AppColors.Teal,
                marginStart = 8.dp
            )
        }
    }
}

@Composable
private fun HeroPriorityHeader(
    heroGroups: List<Pair<String, List<SettingDef>>>,
    selectedHeroKey: String,
    onHeroSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, AppColors.Border),
        colors = CardDefaults.cardColors(containerColor = AppColors.CardBg)
    ) {
        LazyRow(
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            heroGroups.forEach { (heroKey, _) ->
                item(key = heroKey) {
                    HeroPriorityTab(
                        label = Strings.getHeroName(heroKey),
                        selected = heroKey == selectedHeroKey,
                        onClick = { onHeroSelected(heroKey) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroPriorityTab(label: String, selected: Boolean, onClick: () -> Unit) {
    val borderColor = if (selected) AppColors.Primary else AppColors.PrimaryLight
    Button(
        onClick = onClick,
        modifier = Modifier.height(32.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) AppColors.Primary else AppColors.PrimaryLight.copy(alpha = 0.14f),
            contentColor = if (selected) Color.White else AppColors.TextPrimary
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
    ) {
        Text(
            text = label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun EquipmentPriorityButton(equipment: SettingDef, index: Int, modifier: Modifier) {
    val enableKey = "${equipment.key}_c$index"
    val enableState = GlobalVars.configStates.getOrPut(enableKey) {
        mutableStateOf(Schema.getDefaultValue(enableKey))
    }
    val isEnabled = enableState.value == "1"
    val equipmentColor = if (equipment.isEpic) AppColors.Epic else AppColors.PrimaryLight

    Card(
        onClick = {
            GlobalVars.isAutoRunEnabled = false
            enableState.value = if (isEnabled) "0" else "1"
        },
        modifier = modifier.height(42.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, equipmentColor),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) equipmentColor else AppColors.CardBg
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = Schema.getDisplayName(equipment.key),
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = if (isEnabled) Color.White else AppColors.TextPrimary
            )
        }
    }
}

private fun priorityFor(equipment: SettingDef, index: Int): Int =
    GlobalVars.configStates["${MainBaseEquipmentPriorities.keyFor(equipment.key)}_c$index"]?.value?.toIntOrNull()
        ?: Int.MAX_VALUE
