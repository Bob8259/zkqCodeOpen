@file:Suppress("FunctionName")

package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.lazy.LazyListScope
import com.coc.zkqcode.jar.ui.pages.builderbase.BuilderBaseConfig
import com.coc.zkqcode.jar.ui.pages.mainbase.MainBaseConfig
import com.coc.zkqcode.jar.ui.pages.universal.UniversalConfig

fun LazyListScope.GameConfig(
    index: Int,
    isUniversalExpanded: Boolean,
    onToggleUniversalExpanded: () -> Unit,
    isMainExpanded: Boolean,
    onToggleMainExpanded: () -> Unit,
    isNightExpanded: Boolean,
    onToggleNightExpanded: () -> Unit,
    onNavigatePriority: (Int) -> Unit = {},
    onNavigateNightPriority: (Int) -> Unit = {},
    onScrollToBottom: () -> Unit = {}
) {
    // Universal settings header shown before per-mode configs
    UniversalConfig(
        index = index,
        isExpanded = isUniversalExpanded,
        onToggleExpanded = onToggleUniversalExpanded
    )
    MainBaseConfig(
        index = index,
        isExpanded = isMainExpanded,
        onToggleExpanded = onToggleMainExpanded,
        onNavigatePriority = onNavigatePriority
    )
    BuilderBaseConfig(
        index = index,
        isExpanded = isNightExpanded,
        onToggleExpanded = onToggleNightExpanded,
        onNavigateNightPriority = onNavigateNightPriority,
        onScrollToBottom = onScrollToBottom
    )
}