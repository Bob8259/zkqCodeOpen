package com.coc.zkqcode.ui.database

import com.coc.zkqcode.ui.components.GlobalVars
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject

object SchemaExporter {

    /**
     * 将指定的 Schema 定义导出为 JSON 字符串（key-value 格式）
     * @param keys 需要导出的模块 Key 列表。如果为空，则默认导出全部。
     * @param accountCount 账户数量，用于导出账户配置
     */
    fun exportSchemasToJson(keys: List<String> = emptyList(), accountCount: Int = 0): String {
        val jsonObject = JsonObject()

        // 定义所有的映射关系
        val sourceMap = mapOf(
            "GLOBAL_SETTINGS" to Schema.GLOBAL_SETTINGS,
            "ACCOUNT_SETTINGS" to Schema.ACCOUNT_SETTINGS,
            "MAIN_BASE_SETTINGS" to Schema.MAIN_BASE_SETTINGS,
            "MAIN_BASE_TROOPS_AND_SPELLS" to Schema.MAIN_BASE_TROOPS_AND_SPELLS,
            "MAIN_BASE_PETS" to Schema.MAIN_BASE_PETS,
            "MAIN_BASE_BUILDINGS" to Schema.MAIN_BASE_BUILDINGS
        )

        val schemasToExport = if (keys.isEmpty()) {
            sourceMap.values.flatten()
        } else {
            keys.flatMap { key -> sourceMap[key] ?: emptyList() }
        }

        // 将每个 SettingDef 转换为 key-value 对
        schemasToExport.forEach { settingDef ->
            // 尝试从 GlobalVars.fileActions 获取当前值，如果没有则使用默认值
            val currentValue = GlobalVars.fileActions?.getValue(settingDef.key)
                ?: settingDef.defaultValue.toString()
            jsonObject.addProperty(settingDef.key, currentValue)
        }

        // 如果需要导出账户配置
        if (keys.contains("ACCOUNT_SETTINGS") && accountCount > 0) {
            for (i in 1..accountCount) {
                Schema.ACCOUNT_SETTINGS.forEach { settingDef ->
                    val key = "${settingDef.key}${i}"
                    val currentValue = GlobalVars.fileActions?.getValue(key)
                        ?: settingDef.defaultValue.toString()
                    jsonObject.addProperty(key, currentValue)
                }
            }
        }

        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(jsonObject)
    }

    /**
     * 通过 WebSocket 告知服务器写入文件
     * @param accountCount 账户数量，用于导出账户配置
     */
    fun saveSchemaViaServer(
        directory: String,
        fileName: String,
        keys: List<String> = emptyList(),
        accountCount: Int = 0
    ) {
        val jsonContent = exportSchemasToJson(keys, accountCount)
        val fullPath =
            if (directory.endsWith("/")) "$directory$fileName" else "$directory/$fileName"

        val writeAction = mapOf(
            "actionType" to "file_action",
            "subAction" to "write",
            "path" to fullPath,
            "content" to jsonContent
        )

        val connection = GlobalVars.fileActions?.getConnection()
        if (connection != null) {
            connection.sendAction(writeAction)
        } else {
            println("ERROR: ServerConnection 为空，请检查初始化")
        }
    }
}