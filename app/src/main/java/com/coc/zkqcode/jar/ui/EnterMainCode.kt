@file:Suppress("unused")

package com.coc.zkqcode.jar.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.coc.zkqcode.interfaces.MainCode
import com.coc.zkqcode.jar.code.MainScript
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.pages.mainbase.MainBaseUpgradePriority
import com.coc.zkqcode.jar.ui.pages.builderbase.BuilderBaseUpgradePriority
import com.coc.zkqcode.jar.ui.pages.single.HomeScreen
import com.coc.zkqcode.jar.ui.pages.single.SwitchAccount
import com.coc.zkqcode.jar.ui.schema.ConfigManager
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class EnterMainCode : MainCode {
    @Composable
    override fun ShowMainUI(context: Context, onClose: () -> Unit) {
        // Initialize states from Schema
        var isConfigInitialized by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            showDebugInfo("[EnterMainCode] LaunchedEffect started, isConfigLoaded=${GlobalVars.isConfigLoaded}")
            if (GlobalVars.isConfigLoaded) {
                showDebugInfo("[EnterMainCode] Config already loaded, skipping init")
                isConfigInitialized = true
                return@LaunchedEffect
            }

            // Wait for serverActions to become available (may be null if UIWindowService
            // was restarted by Android before CheckRoot finishes setting up serverActions)
            val actions = GlobalVars.serverActions ?: run {
                showDebugInfo("[EnterMainCode] serverActions is NULL, waiting for it to be set...")
                snapshotFlow { GlobalVars.serverActions }.first { it != null }!!
            }
            showDebugInfo("[EnterMainCode] serverActions ready, isLoading=${actions.isLoading}")

            // Wait for configs to load
            snapshotFlow { actions.isLoading }.first { !it }
            showDebugInfo("[EnterMainCode] isLoading became false, calling initializeAllConfigs")
            try {
                ConfigManager.initializeAllConfigs(actions)
                showDebugInfo("[EnterMainCode] initializeAllConfigs completed successfully")
            } catch (e: Exception) {
                showDebugInfo("[EnterMainCode] initializeAllConfigs FAILED: ${e.message}")
            }
            isConfigInitialized = true
            GlobalVars.isConfigLoaded = true
            showDebugInfo("[EnterMainCode] Config initialization done")
        }
        if (!isConfigInitialized) {
            Column {
                Text("正在初始化配置文件...\n若长时间卡在此界面，请取消初始化后重启辅助。")
                CustomButton(
                    text = "取消初始化",
                    onClick = {
                        runBlocking(Dispatchers.IO) {
                            RunShell.runNoOutput("am force-stop com.coc.zkqcode >>/dev/null 2>&1")// got some errors, otherwise the configs can be loaded.
                        }
                    }
                )
            }
            return
        }

        if (AppStateManager.currentMode == AppMode.SwitchAccount) {
            SwitchAccount(onClose = onClose)
            return
        }

        if (AppStateManager.currentMode == AppMode.Main) {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(
                        onSaveSuccess = onClose,
                        onNavigatePriority = { index ->
                            navController.navigate("priority/$index")
                        },
                        onNavigateNightPriority = { index ->
                            navController.navigate("night_priority/$index")
                        }
                    )
                }
                composable("priority/{index}") { backStackEntry ->
                    val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 1
                    MainBaseUpgradePriority(
                        index = index,
                        onSaveSuccess = {
                            navController.popBackStack()
                        }
                    )
                }
                composable("night_priority/{index}") { backStackEntry ->
                    val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 1
                    BuilderBaseUpgradePriority(
                        index = index,
                        onSaveSuccess = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }

    override suspend fun runBot() {
        MainScript.runMainScript()
    }
}