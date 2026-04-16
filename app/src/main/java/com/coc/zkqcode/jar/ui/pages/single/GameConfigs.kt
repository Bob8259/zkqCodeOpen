package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.lazy.LazyListScope
import com.coc.zkqcode.jar.ui.pages.builderbase.builderBaseConfig
import com.coc.zkqcode.jar.ui.pages.mainbase.mainBaseConfig

fun LazyListScope.gameConfig(
    index: Int,
    isMainExpanded: Boolean,
    onToggleMainExpanded: () -> Unit,
    isClanExpanded: Boolean,
    onToggleClan: () -> Unit,
    isResearchExpanded: Boolean,
    onToggleResearch: () -> Unit,
    isBuildExpanded: Boolean,
    onToggleBuild: () -> Unit,
    isPetExpanded: Boolean,
    onTogglePet: () -> Unit,
    isBuilderBaseExpanded: Boolean,
    onToggleBuilderBaseExpanded: () -> Unit,
    onNavigatePriority: (Int) -> Unit = {},
    onNavigateBuilderBasePriority: (Int) -> Unit = {},
    onScrollToBottom: () -> Unit = {}
) {
    mainBaseConfig(
        index = index,
        isExpanded = isMainExpanded,
        onToggleExpanded = onToggleMainExpanded,
        isClanExpanded = isClanExpanded,
        onToggleClan = onToggleClan,
        isResearchExpanded = isResearchExpanded,
        onToggleResearch = onToggleResearch,
        isBuildExpanded = isBuildExpanded,
        onToggleBuild = onToggleBuild,
        isPetExpanded = isPetExpanded,
        onTogglePet = onTogglePet,
        onNavigatePriority = onNavigatePriority
    )
    builderBaseConfig(
        index = index,
        isExpanded = isBuilderBaseExpanded,
        onToggleExpanded = onToggleBuilderBaseExpanded,
        onNavigateBuilderBasePriority = onNavigateBuilderBasePriority,
        onScrollToBottom = onScrollToBottom
    )
}
