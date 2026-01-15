package com.coc.zkqcode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.coc.zkqcode.utils.CheckRootScreen
import com.coc.zkqcode.utils.components.GlobalVars
import com.coc.zkqcode.utils.database.Schema
import com.coc.zkqcode.utils.fileactions.FileActions
import com.coc.zkqcode.utils.websocket.ServerConnection

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isInitialized by remember { mutableStateOf(false) }

            // Initialize FileActions and store in GlobalVars if not already done
            LaunchedEffect(Unit) {
                if (GlobalVars.fileActions == null) {
                    val serverConnection = ServerConnection("ws://localhost:6839/zkq")
                    GlobalVars.fileActions = FileActions(serverConnection)
                }
            }

            // Initialize states from Schema
            LaunchedEffect(GlobalVars.fileActions?.configJson) {
                val actions = GlobalVars.fileActions
                if (actions != null) {

                    Schema.GLOBAL_SETTINGS.forEach { def ->
                        val savedValue = actions.getValue(def.key)
                        if (savedValue != null) {
                            GlobalVars.configStates[def.key]?.value = savedValue
                        } else if (!GlobalVars.configStates.containsKey(def.key)) {
                            GlobalVars.configStates[def.key] =
                                mutableStateOf(def.defaultValue.toString())
                        }
                    }

                    // Also initialize account settings if account_count is present
                    val accountCount = actions.getValue("account_count")?.toIntOrNull() ?: 3
                    for (i in 1..accountCount) {
                        Schema.ACCOUNT_SETTINGS.forEach { def ->
                            val key = "${def.key}${i}"
                            val savedValue = actions.getValue(key)
                            if (savedValue != null) {
                                GlobalVars.configStates[key]?.value = savedValue
                            } else {
                                val defaultValue =
                                    if (def.key.startsWith("global_path") || def.key.startsWith("cn_path")) {
                                        i.toString()
                                    } else {
                                        def.defaultValue.toString()
                                    }
                                GlobalVars.configStates[key] = mutableStateOf(defaultValue)
                            }
                        }
                    }

                    // Initialize MAIN_BASE_SETTINGS for each config
                    val configCount = actions.getValue("config_count")?.toIntOrNull() ?: 3
                    for (i in 1..configCount) {
                        Schema.MAIN_BASE_SETTINGS.forEach { def ->
                            val key = "${def.key}_c$i"
                            val savedValue = actions.getValue(key)
                            if (savedValue != null) {
                                GlobalVars.configStates[key]?.value = savedValue
                            } else if (!GlobalVars.configStates.containsKey(key)) {
                                GlobalVars.configStates[key] =
                                    mutableStateOf(def.defaultValue.toString())
                            }
                        }
                    }

                    isInitialized = true
                }
            }

            if (isInitialized) {
                CheckRootScreen()
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "加载配置文件中....",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
