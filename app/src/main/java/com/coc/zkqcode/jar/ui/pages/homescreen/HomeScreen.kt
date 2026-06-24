package com.coc.zkqcode.jar.ui.pages.homescreen

import android.content.Intent
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.fileactions.FileHelper
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.pages.mainbase.BackgroundImage
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SettingDropdown
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.pages.single.ConfigFileGrid
import com.coc.zkqcode.jar.ui.pages.single.ExtractGameSave
import com.coc.zkqcode.jar.ui.pages.single.accountSettings
import com.coc.zkqcode.jar.ui.pages.single.configSearchResults
import com.coc.zkqcode.jar.ui.pages.single.gameConfig
import com.coc.zkqcode.jar.ui.pages.single.homeSettings
import com.coc.zkqcode.jar.ui.pages.single.specialSettings
import com.coc.zkqcode.jar.ui.schema.ConfigManager
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.jar.ui.theme.AppColors
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onSaveSuccess: () -> Unit = {}, onNavigatePriority: (Int) -> Unit = {}, onNavigateBuilderBasePriority: (Int) -> Unit = {},
    onNavigateResearchPriority: (Int) -> Unit = {}, onNavigatePetPriority: (Int) -> Unit = {},
    onNavigateBuilderBaseResearchPriority: (Int) -> Unit = {}
) {

    // Ensure all keys are initialized if not already (safeguard)
    val actions = GlobalVars.serverActions
    if (actions != null && GlobalVars.configStates.isEmpty()) {
        ConfigManager.initializeAllConfigs(actions)
    }

    // Safety check for configuration state
    val configCountState = GlobalVars.configStates[GLOBAL_SETTINGS.CONFIG_COUNT.key]
    if (configCountState == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            BackgroundImage()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.Background)
                    .padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = Strings.initializing, color = Color.Gray)
            }
        }
        return
    }
    val configCountStr = configCountState.value


    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedConfigIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    var configSearchQuery by remember { mutableStateOf("") }

    // Reset config sub-navigation when switching away from the config tab
    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex != 3) {
            selectedConfigIndex = null
            configSearchQuery = ""
        }
    }

    // Reset search query when switching config files
    LaunchedEffect(selectedConfigIndex) {
        configSearchQuery = ""
    }

    // Hoisted expansion states keyed by config index (1-based)
    val generalExpandedStates = remember { mutableStateMapOf<Int, Boolean>() }
    val mainBaseExpandedStates = remember { mutableStateMapOf<Int, Boolean>() }
    val researchExpandedStates = remember { mutableStateMapOf<Int, Boolean>() }
    val buildExpandedStates = remember { mutableStateMapOf<Int, Boolean>() }
    val petExpandedStates = remember { mutableStateMapOf<Int, Boolean>() }
    val otherExpandedStates = remember { mutableStateMapOf<Int, Boolean>() }
    val clanExpandedStates = remember { mutableStateMapOf<Int, Boolean>() }
    val builderBaseExpandedStates = remember { mutableStateMapOf<Int, Boolean>() }

    val configCount = configCountStr.toIntOrNull() ?: 1
    val tabs = listOf(Strings.homeTab, Strings.accountTab, Strings.extractTab, Strings.configTab, Strings.specialTab)
    val sectionItems = listOf(
        Strings.sectionGeneral to 0,
        Strings.sectionCoreBattle to 2,
        Strings.sectionClan to 6,
        Strings.sectionResearch to 8,
        Strings.sectionBuild to 10,
        Strings.sectionPet to 12,
        Strings.sectionOther to 14,
        Strings.sectionBuilderBase to 19
    )

    val lazyListState = rememberLazyListState()
    val chipListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val activeSectionIndex by remember {
        derivedStateOf {
            val firstVisible = lazyListState.firstVisibleItemIndex
            sectionItems.indexOfLast { (_, itemIndex) -> itemIndex <= firstVisible }.coerceAtLeast(0)
        }
    }

    LaunchedEffect(activeSectionIndex) {
        chipListState.animateScrollToItem(activeSectionIndex)
    }
    val scrollToBottom: () -> Unit = {
        scope.launch {
            // Small delay to let AnimatedVisibility content measure
            delay(100L)
            lazyListState.animateScrollToItem(lazyListState.layoutInfo.totalItemsCount - 1)
        }
    }
    // Save configs and explicitly set the playing state before closing the config window
    val saveAndSetPlaying: (Boolean) -> Unit = { play ->
        scope.launch {
            GlobalVars.isPlaying.value = play
            ConfigManager.saveAndRun {
                onSaveSuccess()
            }
        }
    }

    // Floating notification state
    var showNotification by remember { mutableStateOf(false) }
    var notificationMessage by remember { mutableStateOf("") }

    // Show a floating notification and auto-dismiss after 1200ms
    val showMsg: (String) -> Unit = { msg ->
        notificationMessage = msg
        showNotification = true
        scope.launch {
            delay(1200)
            showNotification = false
        }
    }

    val cleanMemory = {
        scope.launch {
            val sdPath = Environment.getExternalStorageDirectory().path
            val memoryPath = "$sdPath/zkqFiles/memory.json"
            val success = FileHelper.deleteJson(memoryPath)
            showMsg(if (success) Strings.memoryCleanSuccess else Strings.memoryCleanFail)
        }
    }

    // Clean All Data Confirmation Dialogue State
    var showCleanAllConfirmation by rememberSaveable { mutableStateOf(false) }
    var showAnnouncement by remember { mutableStateOf(false) }
    var showImportOldDataConfirmation by rememberSaveable { mutableStateOf(false) }

    // Context for launching the image picker activity
    val context = LocalContext.current

    val cleanAllData = {
        showCleanAllConfirmation = true
    }

    // Auto-Run Timer Logic
    var autoRunCountdown by remember { mutableIntStateOf(InGameVars.autoRunTimer) }

    LaunchedEffect(GlobalVars.isAutoRunEnabled, InGameVars.autoRunTimer) {
        if (GlobalVars.isAutoRunEnabled) {
            autoRunCountdown = InGameVars.autoRunTimer
            while (autoRunCountdown > 0) {
                delay(1000L)
                autoRunCountdown--
            }
            saveAndSetPlaying(true)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
        ) {
        // Floating tab bar overlaying the content area
        Box(modifier = Modifier.weight(1f)) {
            // Layer 1: Scrollable content with top padding to avoid being hidden behind the tab bar
            LazyColumn(
                state = lazyListState, contentPadding = PaddingValues(top = if (selectedTabIndex == 3 && selectedConfigIndex != null) (if (configSearchQuery.isBlank()) 80.dp else 48.dp) else 55.dp), modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        homeSettings(
                            context = context,
                            autoRunCountdown = autoRunCountdown,
                            saveAndSetPlaying = saveAndSetPlaying,
                            showMsg = showMsg,
                            cleanMemory = { cleanMemory() },
                            cleanAllData = { cleanAllData() },
                            onSaveSuccess = onSaveSuccess,
                            onShowAnnouncement = { showAnnouncement = true },
                            onShowImportOldData = { showImportOldDataConfirmation = true }
                        )
                    }

                    1 -> {
                        // Display account configurations based on account_count
                        accountSettings()
                    }

                    2 -> {
                        // Display account configurations based on account_count
                        ExtractGameSave()
                    }

                    3 -> {
                        val cfgIdx = selectedConfigIndex
                        if (cfgIdx == null) {
                            // Show grid of config files
                            item {
                                ConfigFileGrid(
                                    configCount = configCount,
                                    onConfigSelected = { selectedConfigIndex = it },
                                    onShowMsg = showMsg
                                )
                            }
                        } else {
                            if (configSearchQuery.isNotBlank()) {
                                // Show filtered search results
                                configSearchResults(
                                    query = configSearchQuery,
                                    configIndex = cfgIdx
                                )
                            } else {
                                // State hoisting for expansion keyed by config index
                                val currentGeneralExpanded = generalExpandedStates[cfgIdx] ?: true
                                val currentMainExpanded = mainBaseExpandedStates[cfgIdx] ?: true
                                val currentClanExpanded = clanExpandedStates[cfgIdx] ?: true
                                val currentResearchExpanded = researchExpandedStates[cfgIdx] ?: true
                                val currentBuildExpanded = buildExpandedStates[cfgIdx] ?: true
                                val currentPetExpanded = petExpandedStates[cfgIdx] ?: true
                                val currentOtherExpanded = otherExpandedStates[cfgIdx] ?: true
                                val currentBuilderBaseExpanded = builderBaseExpandedStates[cfgIdx] ?: true

                                gameConfig(
                                    index = cfgIdx,
                                    isGeneralExpanded = currentGeneralExpanded,
                                    onToggleGeneralExpanded = { generalExpandedStates[cfgIdx] = !currentGeneralExpanded },
                                    isMainExpanded = currentMainExpanded,
                                    onToggleMainExpanded = { mainBaseExpandedStates[cfgIdx] = !currentMainExpanded },
                                    isClanExpanded = currentClanExpanded,
                                    onToggleClan = { clanExpandedStates[cfgIdx] = !currentClanExpanded },
                                    isResearchExpanded = currentResearchExpanded,
                                    onToggleResearch = { researchExpandedStates[cfgIdx] = !currentResearchExpanded },
                                    isBuildExpanded = currentBuildExpanded,
                                    onToggleBuild = { buildExpandedStates[cfgIdx] = !currentBuildExpanded },
                                    isPetExpanded = currentPetExpanded,
                                    onTogglePet = { petExpandedStates[cfgIdx] = !currentPetExpanded },
                                    isOtherExpanded = currentOtherExpanded,
                                    onToggleOther = { otherExpandedStates[cfgIdx] = !currentOtherExpanded },
                                    isBuilderBaseExpanded = currentBuilderBaseExpanded,
                                    onToggleBuilderBaseExpanded = {
                                        val newExpanded = !currentBuilderBaseExpanded
                                        builderBaseExpandedStates[cfgIdx] = newExpanded
                                        if (newExpanded) scrollToBottom()
                                    },
                                    onNavigatePriority = onNavigatePriority,
                                    onNavigateBuilderBasePriority = onNavigateBuilderBasePriority,
                                    onNavigateResearchPriority = onNavigateResearchPriority,
                                    onNavigatePetPriority = onNavigatePetPriority,
                                    onNavigateBuilderBaseResearchPriority = onNavigateBuilderBaseResearchPriority,
                                    onScrollToBottom = scrollToBottom
                                )
                            }
                        }
                    }

                    4 -> {
                        specialSettings()
                    }
                }
            }

            // Layer 2: Floating tab bar + optional back button overlay
            Column(modifier = Modifier.align(Alignment.TopStart)) {
                val isConfigDetail = selectedTabIndex == 3 && selectedConfigIndex != null
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp)
                        .then(if (!isConfigDetail) Modifier.padding(top = 6.dp) else Modifier)
                        .border(1.dp, AppColors.Border, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    color = AppColors.CardBg.copy(alpha = 0.9f),
                    shadowElevation = 0.dp
                ) {
                    if (isConfigDetail) {
                        // Back button + search field + section chips when a config file is selected
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CustomButton(
                                    text = Strings.back, onClick = {
                                        configSearchQuery = ""
                                        selectedConfigIndex = null
                                    }, marginTop = 0.dp
                                )
                                // Search input field
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp)
                                        .height(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AppColors.Background)
                                        .border(1.dp, AppColors.Border, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (configSearchQuery.isEmpty()) {
                                        Text(
                                            text = Strings.searchSettings,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.Gray
                                        )
                                    }
                                    androidx.compose.foundation.text.BasicTextField(
                                        value = configSearchQuery,
                                        onValueChange = { configSearchQuery = it },
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.labelMedium.copy(
                                            color = AppColors.TextPrimary
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                if (configSearchQuery.isNotEmpty()) {
                                    CustomButton(
                                        text = "✕", onClick = { configSearchQuery = "" }, marginTop = 0.dp, marginEnd = 0.dp
                                    )
                                }
                            }
                            // Section chips - only show when not searching
                            if (configSearchQuery.isBlank()) {
                                LazyRow(
                                    state = chipListState,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    sectionItems.forEachIndexed { chipIndex, (label, itemIndex) ->
                                        item {
                                            val isActive = chipIndex == activeSectionIndex
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(16.dp))
                                                    .background(if (isActive) AppColors.Primary else AppColors.Primary.copy(alpha = 0.15f))
                                                    .border(1.dp, AppColors.Primary, RoundedCornerShape(16.dp))
                                                    .clickable { scope.launch { lazyListState.animateScrollToItem(itemIndex) } }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = label,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = if (isActive) Color.White else AppColors.Primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Glassmorphism tab bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            tabs.forEachIndexed { index, title ->
                                val isActive = selectedTabIndex == index
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isActive) AppColors.Primary else AppColors.NavInactive,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedTabIndex = index }
                                        .padding(horizontal = 6.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        } // End Box

        // Announcement dialog
        if (showAnnouncement) {
            AnnouncementDialog(onDismiss = { showAnnouncement = false })
        }

        if (showImportOldDataConfirmation) {
            com.coc.zkqcode.jar.ui.components.CustomAlertDialog(
                onDismissRequest = { showImportOldDataConfirmation = false },
                title = { Text(Strings.importOldTitle, style = MaterialTheme.typography.titleMedium) },
                text = {
                    Text(
                        Strings.importOldMessage,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    Row {
                        androidx.compose.material3.TextButton(onClick = { showImportOldDataConfirmation = false }) { Text(Strings.cancel) }
                        androidx.compose.material3.TextButton(onClick = {
                            showImportOldDataConfirmation = false
                            scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                showMsg(Strings.scanningImport)
                                val success = com.coc.zkqcode.jar.ui.schema.OldDataImporter.importOldData()
                                if (success) {
                                    ConfigManager.saveConfigs()
                                    showMsg(Strings.importSuccess)
                                } else {
                                    showMsg(Strings.noOldData)
                                }
                            }
                        }) { Text(Strings.confirm) }
                    }
                }
            )
        }

        // 3. Bottom fixed button area + dialogs + notification
        HomeScreenBottomBar(
            scope = scope,
            saveAndSetPlaying = saveAndSetPlaying,
            showNotification = showNotification,
            notificationMessage = notificationMessage,
            onDismissNotification = {},
            showCleanAllConfirmation = showCleanAllConfirmation,
            onDismissCleanAll = { showCleanAllConfirmation = false })
        }
    }

}
