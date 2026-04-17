package com.coc.zkqcode.jar.ui.schema

import android.os.Environment
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.fileactions.FileHelper
import com.google.gson.JsonObject

object OldDataImporter {

    private suspend fun readTextViaServer(path: String): String? {
        val serverActions = GlobalVars.serverActions ?: return null
        val readAction = mapOf(
            "actionType" to "file_action",
            "subAction" to "read",
            "path" to path
        )
        val response = serverActions.sendActionSync(readAction)
        if (response != null && response.has("status") && response.get("status").asString == "success") {
            return response.get("data")?.asString
        }
        return null
    }

    suspend fun importOldData(): Boolean {
        var anyImported = false
        val sdPath = Environment.getExternalStorageDirectory().path
        val oldConfigDir = "$sdPath/紫孔雀配置"

        var accountCount = 3
        var configCount = 3
        val oldAcctCount = readTextViaServer("$oldConfigDir/多开账号数量.txt")
        if (oldAcctCount != null && oldAcctCount.toIntOrNull() != null) {
            accountCount = oldAcctCount.toInt()
            GlobalVars.configStates["account_count"]?.value = oldAcctCount
            anyImported = true
        }

        val oldCfgCount = readTextViaServer("$oldConfigDir/配置文件数量.txt")
        if (oldCfgCount != null && oldCfgCount.toIntOrNull() != null) {
            configCount = oldCfgCount.toInt()
            GlobalVars.configStates["config_count"]?.value = oldCfgCount
            anyImported = true
        }
        
        ConfigManager.expandAccountConfigs(accountCount.coerceAtMost(5000))
        ConfigManager.expandProfileConfigs(configCount.coerceAtMost(100))

        val jsonTokensToSave = mutableMapOf<String, String>()

        SchemaRegistry.ALL_MODULES.forEach { module ->
            when (module.scope) {
                Scope.GLOBAL -> {
                    module.settings.forEach { def ->
                        if (def.key != "account_count" && def.key != "config_count") {
                            val content = readTextViaServer("$oldConfigDir/${def.displayName}.txt")
                            if (content != null) {
                                GlobalVars.configStates[def.key]?.value = content
                                anyImported = true
                            }
                        }
                    }
                }

                Scope.ACCOUNT -> {
                    for (i in 1..accountCount) {
                        module.settings.forEach { def ->
                            val oldFileName = when (def.key) {
                                "cn_path" -> "国服路径$i.txt"
                                "global_path" -> "国际服路径$i.txt"
                                "data_content" -> "数据号内容$i.txt"
                                else -> "${def.displayName}$i.txt"
                            }
                            val content = readTextViaServer("$oldConfigDir/$oldFileName")
                            if (content != null) {
                                if (def.key == "data_content") {
                                    jsonTokensToSave[i.toString()] = content
                                    GlobalVars.configStates["${def.key}$i"]?.value = i.toString()
                                } else {
                                    GlobalVars.configStates["${def.key}$i"]?.value = content
                                }
                                anyImported = true
                            }
                        }
                    }
                }

                Scope.PROFILE -> {
                    for (i in 1..configCount) {
                        module.settings.forEach { def ->
                            val content = readTextViaServer("$oldConfigDir/${def.displayName}$i.txt")
                            if (content != null) {
                                GlobalVars.configStates["${def.key}_c$i"]?.value = content
                                anyImported = true
                            }
                        }
                    }
                }
            }
        }

        if (jsonTokensToSave.isNotEmpty()) {
            val dataPath = "$sdPath/loginData/data.json"
            val json = FileHelper.readJson(dataPath) ?: JsonObject()
            jsonTokensToSave.forEach { (key, value) ->
                json.addProperty(key, value)
            }
            FileHelper.writeJson(dataPath, json.toString())
        }
        
        return anyImported
    }
}
