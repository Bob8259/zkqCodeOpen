@file:Suppress("unused")

package com.coc.zkqcode.jar.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.system.checkpermissions.FullScreenMessage
import com.coc.zkqcode.interfaces.MainCode
import com.coc.zkqcode.jar.code.MainScript
import com.coc.zkqcode.jar.ui.pages.mainbase.MainBaseUpgradePriority
import com.coc.zkqcode.jar.ui.pages.nightbase.NightBaseUpgradePriority
import com.coc.zkqcode.jar.ui.pages.single.HomeScreen
import com.coc.zkqcode.jar.ui.pages.single.SwitchAccount
import com.coc.zkqcode.jar.ui.schema.ConfigManager
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager

class EnterMainCode : MainCode {
    @Composable
    override fun ShowMainUI(context: Context, onClose: () -> Unit) {
        // Initialize states from Schema
        var isConfigInitialized by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            val actions = GlobalVars.serverActions
            if (actions != null) {
                // Wait for configs to load
                ConfigManager.initializeAllConfigs(actions)
                isConfigInitialized = true
                GlobalVars.isConfigLoaded = true
            }

        }
        if (!isConfigInitialized) {
            FullScreenMessage("正在初始化配置文件...")
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
                    NightBaseUpgradePriority(
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