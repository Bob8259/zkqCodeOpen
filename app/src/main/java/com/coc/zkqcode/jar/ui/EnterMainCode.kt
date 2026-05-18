@file:Suppress("unused")

package com.coc.zkqcode.jar.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.LanguageManager
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.interfaces.MainCode
import com.coc.zkqcode.jar.code.runMainScript
import com.coc.zkqcode.jar.ui.pages.homescreen.BackgroundImage
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.pages.builderbase.BuilderBaseUpgradePriority
import com.coc.zkqcode.jar.ui.pages.mainbase.MainBaseUpgradePriority
import com.coc.zkqcode.jar.ui.pages.single.BugReport
import com.coc.zkqcode.jar.ui.pages.homescreen.HomeScreen
import com.coc.zkqcode.jar.ui.pages.single.SwitchAccount
import com.coc.zkqcode.jar.ui.schema.ConfigManager
import com.coc.zkqcode.jar.ui.theme.AppColors
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EnterMainCode : MainCode {
    @Composable
    override fun ShowMainUI(context: Context, onClose: () -> Unit) {
        // Initialize states from Schema
        var isConfigInitialized by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            // Detect device language on first UI launch
            LanguageManager.detectDeviceLanguage()

            if (GlobalVars.isConfigLoaded) {
                // The UI service can be closed and recreated while configStates stays in memory.
                // Re-apply the language value so reopened UI keeps the user's current choice.
                LanguageManager.syncWithConfig()
                isConfigInitialized = true
                return@LaunchedEffect
            }

            // Wait for serverActions to become available (may be null if UIWindowService
            // was restarted by Android before CheckRoot finishes setting up serverActions)
            val actions = GlobalVars.serverActions ?: run {
                snapshotFlow { GlobalVars.serverActions }.first { it != null }!!
            }

            // Wait for configs to load
            snapshotFlow { actions.isLoading }.first { !it }
            ConfigManager.initializeAllConfigs(actions)
            // Override auto-detected language with the current config value
            LanguageManager.syncWithConfig()
            isConfigInitialized = true
            GlobalVars.isConfigLoaded = true
        }
        if (!isConfigInitialized) {
            ConfigLoadingScreen()
            return
        }

        // Route to the appropriate page based on current mode
        when (AppStateManager.currentMode) {
            AppMode.SwitchAccount -> {
                SwitchAccount(onClose = onClose)
                return
            }

            AppMode.BugReport -> {
                BugReport(onClose = onClose)
                return
            }

            AppMode.Main -> {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(onSaveSuccess = onClose, onNavigatePriority = { index ->
                            navController.navigate("priority/$index")
                        }, onNavigateBuilderBasePriority = { index ->
                            navController.navigate("builder_base_priority/$index")
                        })
                    }
                    composable("priority/{index}") { backStackEntry ->
                        val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 1
                        MainBaseUpgradePriority(
                            index = index, onSaveSuccess = {
                                navController.popBackStack()
                            })
                    }
                    composable("builder_base_priority/{index}") { backStackEntry ->
                        val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 1
                        BuilderBaseUpgradePriority(
                            index = index, onSaveSuccess = {
                                navController.popBackStack()
                            })
                    }
                }
            }

            else -> {}
        }
    }

    override suspend fun runBot() {
        runMainScript()
    }
}

@Composable
private fun ConfigLoadingScreen() {
    var countdown by remember { mutableIntStateOf(20) }

    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000L)
            countdown--
        }
        // Skip waitForPlay check — force-stop should execute immediately
        RunShell.runNoOutput("am force-stop com.coc.zkqcode >>/dev/null 2>&1", checkIsPlaying = false)
    }

    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background),
            contentAlignment = Alignment.Center
        ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.CardBg)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                CircularProgressIndicator(
                    color = AppColors.TextPrimary,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = Strings.configLoadingMessage(countdown),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                    color = AppColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(20.dp))
                CustomButton(
                    text = Strings.manualCloseAssistant, onClick = {
                        // Launch on IO dispatcher to avoid blocking the main thread (ANR)
                        scope.launch(Dispatchers.IO) {
                            RunShell.runNoOutput("am force-stop com.coc.zkqcode >>/dev/null 2>&1", checkIsPlaying = false)
                        }
                    })
            }
        }
        }
    }
}