package com.coc.zkqcode.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.coc.zkqcode.ui.database.Schema
import com.coc.zkqcode.ui.theme.AppColors
import com.coc.zkqcode.utils.fileactions.FileActions
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object GlobalVars {
    var isCounting: Boolean = true
    var isChangFromUpgradePriority: Boolean = false
    var fileActions: FileActions? = null
    val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    val saveJobs = mutableMapOf<String, Job>()
}

fun saveState(key: String, content: String, delayMillis: Long = 0L) {
    if (key.isBlank()) return
    GlobalVars.isCounting = false // Stop counting after user modifies configuration

    GlobalVars.saveJobs[key]?.cancel()
    GlobalVars.saveJobs[key] = GlobalVars.scope.launch {
        if (delayMillis > 0) {
            delay(delayMillis)
        }
 
        // Only save key-value pairs, not nested category structure
        GlobalVars.fileActions?.writeToConfigFile(key, content)
    }
}

/**
 * Serialize Schema to JSON and save
 */
fun saveSchemaToJson() {
    try {
        val rootJson = JsonObject()

        // Map all Schema lists
        val schemaMap = mapOf(
            "GLOBAL_SETTINGS" to Schema.GLOBAL_SETTINGS,
            "ACCOUNT_SETTINGS" to Schema.ACCOUNT_SETTINGS,
            "MAIN_BASE_SETTINGS" to Schema.MAIN_BASE_SETTINGS,
            "MAIN_BASE_TROOPS_AND_SPELLS" to Schema.MAIN_BASE_TROOPS_AND_SPELLS,
            "MAIN_BASE_PETS" to Schema.MAIN_BASE_PETS,
            "MAIN_BASE_BUILDINGS" to Schema.MAIN_BASE_BUILDINGS
        )

        schemaMap.forEach { (categoryName, list) ->
            val jsonArray = JsonArray()
            list.forEach { item ->
                val itemJson = JsonObject()
                itemJson.addProperty("key", item.key)
                itemJson.addProperty("displayName", item.displayName)
                itemJson.addProperty("defaultValue", item.defaultValue.toString())
                itemJson.addProperty("category", item.category)
                jsonArray.add(itemJson)
            }
            rootJson.add(categoryName, jsonArray)
        }

        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(rootJson)

        GlobalVars.fileActions?.writeToConfigFile("all_schemas_backup", jsonString)
        println("Schema saved to JSON")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Composable
fun InputRowWithCheckBox(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    checkedState: String,
    onCheckedChange: (Boolean) -> Unit,
    checkBoxPath: String,
    inputPath: String
) {
    val scope = rememberCoroutineScope()
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomCheckBox(
            text = label,
            checkedState = checkedState,
            onCheckStateChange = onCheckedChange,
            key = checkBoxPath
        )
        BasicTextField(
            value = value, modifier = Modifier
                .padding(end = 16.dp, top = 6.dp, start = 6.dp)
                .background(
                    color = Color.LightGray,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(4.dp)
                .align(Alignment.CenterVertically)
                .heightIn(max = 120.dp)
                .verticalScroll(rememberScrollState()), onValueChange = { newValue ->
                onValueChange(newValue)
                saveState(inputPath, newValue, 500L)
            })
    }
}

@Composable
fun FloatingDialog(message: String) {
    Popup(
        alignment = Alignment.Center,
        offset = IntOffset(0, 400)
    ) {
        Surface(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Box(
                modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center
            ) {
                Text(text = message, color = Color.Black)
            }
        }
    }
}

@Composable
fun InputRow(
    label: String, value: String, onValueChange: (String) -> Unit, key: String
) {
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier
                .padding(end = 16.dp)
                .align(Alignment.CenterVertically),
            style = MaterialTheme.typography.labelMedium
        )
        BasicTextField(
            value = value, modifier = Modifier
                .padding(end = 16.dp)
                .background(
                    color = Color.LightGray,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(4.dp)
                .align(Alignment.CenterVertically)
                .heightIn(max = 120.dp)
                .verticalScroll(rememberScrollState()), onValueChange = { newValue ->
                onValueChange(newValue)
                saveState(key, newValue, 500L)
            })
    }
}

@Composable
fun CustomButton(
    text: String, onClick: () -> Unit, enable: Boolean = true, explain: String? = null
) {
    val scope = rememberCoroutineScope()
    var showExplanation by remember { mutableStateOf(false) }
    Row {
        Button(
            onClick = {
                // Timer helper logic preserved from original code
//                scope.launch {
//                    saveState(
//                        "计时助手",
//                        (System.currentTimeMillis() % 50505 + Random.nextInt(1000)).toString()
//                    )
//                }
                onClick()
            },
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp)
                .height(32.dp),
            shape = RoundedCornerShape(8.dp),
            enabled = enable,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = AppColors.Azure
            )
        ) {
            Text(text, style = MaterialTheme.typography.labelMedium)
        }
        Column(Modifier.padding(top = 14.dp)) {
            explain?.let {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Notes",
                    modifier = Modifier
                        .height(18.dp)
                        .clickable { showExplanation = true }
                        .padding(top = 2.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))

                if (showExplanation) {
                    AlertDialog(
                        onDismissRequest = { showExplanation = false },
                        title = { Text(text = "注意事项") },
                        text = { Text(text = explain) },
                        confirmButton = {
                            TextButton(
                                onClick = { showExplanation = false }) {
                                Text("明白了")
                            }
                        })
                }
            }
        }
    }
}

@Composable
fun CustomCheckBox(
    text: String,
    checkedState: String,
    onCheckStateChange: (Boolean) -> Unit,
    key: String,
    explain: String? = null
) {
    val scope = rememberCoroutineScope()
    var showExplanation by remember { mutableStateOf(false) }

    Row(modifier = Modifier.padding(top = 6.dp)) {
        Checkbox(
            checked = checkedState == "1", onCheckedChange = { isChecked ->
                onCheckStateChange(isChecked)
                val content = if (isChecked) "1" else "0"
                scope.launch {
                    saveState(key, content)
                }
            }, modifier = Modifier
                .height(20.dp)
                .width(25.dp),
            colors = androidx.compose.material3.CheckboxDefaults.colors(
                checkedColor = AppColors.Azure,
                checkmarkColor = Color.White
            )
        )

        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                val newCheckedState = checkedState != "1"
                onCheckStateChange(newCheckedState)
                val content = if (newCheckedState) "1" else "0"
                scope.launch {
                    saveState(key, content)
                }
            }) {
            Text(
                text = text,
                modifier = Modifier.padding(top = if (explain != null) 2.dp else 4.dp),
                style = MaterialTheme.typography.labelMedium
            )

            explain?.let {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "注意事项",
                    modifier = Modifier
                        .height(18.dp)
                        .clickable { showExplanation = true }
                        .padding(end = 2.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))

                if (showExplanation) {
                    AlertDialog(
                        onDismissRequest = { showExplanation = false },
                        title = { Text(text = "注意事项") },
                        text = { Text(text = explain) },
                        confirmButton = {
                            TextButton(
                                onClick = { showExplanation = false }) {
                                Text("明白了")
                            }
                        })
                }
            }
        }
    }
}

@Composable
fun DropdownButton(
    index: Int,
    configStates: Map<String, MutableState<String>>,
    options: List<String>,
    key: String,
    label: String,
    onValueChange: (String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val state = configStates["${key}${index}"]
    val currentVersion = state?.value ?: "0"

    val selectedOption = remember(currentVersion, options) {
        val idx = currentVersion.toIntOrNull() ?: 0
        if (idx in options.indices) options[idx] else options.getOrElse(0) { "" }
    }

    var expanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(end = 10.dp),
            style = MaterialTheme.typography.labelMedium
        )

        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
            Button(
                onClick = { expanded = true },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(30.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = AppColors.Azure
                )
            ) {
                Text(selectedOption, style = MaterialTheme.typography.labelMedium)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEachIndexed { idx, option ->
                    DropdownMenuItem(text = {
                        Text(
                            option, style = MaterialTheme.typography.labelMedium
                        )
                    }, onClick = {
                        val newValue = idx.toString()
                        state?.value = newValue
                        onValueChange(newValue)
                        expanded = false
                        scope.launch {
                            saveState("${key}${index}", newValue)
                        }
                    }, modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun DropdownButton(
    configStates: Map<String, MutableState<String>>,
    options: List<String>,
    filePath: String,
    label: String,
    onValueChange: (String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val state = configStates[filePath]
    val currentVersion = state?.value ?: "0"
    
    val selectedOption = remember(currentVersion, options) {
        val idx = currentVersion.toIntOrNull() ?: 0
        if (idx in options.indices) options[idx] else options.getOrElse(0) { "" }
    }

    var expanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(end = 10.dp),
            style = MaterialTheme.typography.labelMedium
        )

        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
            Button(
                onClick = { expanded = true },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(30.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = AppColors.Azure
                )
            ) {
                Text(selectedOption, style = MaterialTheme.typography.labelMedium)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEachIndexed { idx, option ->
                    DropdownMenuItem(text = {
                        Text(
                            option, style = MaterialTheme.typography.labelMedium
                        )
                    }, onClick = {
                        val newValue = idx.toString()
                        state?.value = newValue
                        onValueChange(newValue)
                        expanded = false
                        scope.launch {
                            saveState(filePath, newValue)
                        }
                    }, modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}