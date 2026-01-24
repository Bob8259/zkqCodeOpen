@file:Suppress("unused")

package com.coc.zkqcode.jar.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.coc.zkqcode.interfaces.MainCode
import com.coc.zkqcode.jar.code.MainScript
import com.coc.zkqcode.jar.ui.pages.mainbase.MainBaseUpgradePriority
import com.coc.zkqcode.jar.ui.pages.nightbase.NightBaseUpgradePriority
import com.coc.zkqcode.jar.ui.pages.single.HomeScreen
import com.coc.zkqcode.jar.ui.pages.single.SwitchAccount
import com.coc.zkqcode.utils.state.AppMode
import com.coc.zkqcode.utils.state.AppStateManager

class EnterMainCode : MainCode {
    @Composable
    override fun ShowMainUI(context: Context, onClose: () -> Unit) {
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
        MainScript().runScript()
    }
}