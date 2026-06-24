package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.lazy.LazyListScope
import com.coc.zkqcode.jar.ui.pages.builderbase.builderBaseConfig
import com.coc.zkqcode.jar.ui.pages.mainbase.mainBaseConfig

fun LazyListScope.gameConfig(
    index: Int,
    isGeneralExpanded: Boolean,
    onToggleGeneralExpanded: () -> Unit,
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
    isOtherExpanded: Boolean,
    onToggleOther: () -> Unit,
    isBuilderBaseExpanded: Boolean,
    onToggleBuilderBaseExpanded: () -> Unit,
    onNavigatePriority: (Int) -> Unit = {},
    onNavigateBuilderBasePriority: (Int) -> Unit = {},
    onNavigateResearchPriority: (Int) -> Unit = {},
    onNavigatePetPriority: (Int) -> Unit = {},
    onNavigateBuilderBaseResearchPriority: (Int) -> Unit = {},
    onScrollToBottom: () -> Unit = {}
) {
    mainBaseConfig(
        index = index,
        isGeneralExpanded = isGeneralExpanded,
        onToggleGeneralExpanded = onToggleGeneralExpanded,
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
        isOtherExpanded = isOtherExpanded,
        onToggleOther = onToggleOther,
        onNavigatePriority = onNavigatePriority,
        onNavigateResearchPriority = onNavigateResearchPriority,
        onNavigatePetPriority = onNavigatePetPriority
    )
    builderBaseConfig(
        index = index,
        isExpanded = isBuilderBaseExpanded,
        onToggleExpanded = onToggleBuilderBaseExpanded,
        onNavigateBuilderBasePriority = onNavigateBuilderBasePriority,
        onNavigateBuilderBaseResearchPriority = onNavigateBuilderBaseResearchPriority,
        onScrollToBottom = onScrollToBottom
    )
}
