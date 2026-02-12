@file:Suppress("FunctionName")

package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.lazy.LazyListScope
import com.coc.zkqcode.jar.ui.pages.mainbase.MainBaseConfig
import com.coc.zkqcode.jar.ui.pages.nightbase.NightBaseConfig

fun LazyListScope.GameConfig(
    index: Int,
    isMainExpanded: Boolean,
    onToggleMainExpanded: () -> Unit,
    isNightExpanded: Boolean,
    onToggleNightExpanded: () -> Unit,
    onNavigatePriority: (Int) -> Unit = {},
    onNavigateNightPriority: (Int) -> Unit = {}
) {
    MainBaseConfig(
        index = index,
        isExpanded = isMainExpanded,
        onToggleExpanded = onToggleMainExpanded,
        onNavigatePriority = onNavigatePriority
    )
    NightBaseConfig(
        index = index,
        isExpanded = isNightExpanded,
        onToggleExpanded = onToggleNightExpanded,
        onNavigateNightPriority = onNavigateNightPriority
    )
}