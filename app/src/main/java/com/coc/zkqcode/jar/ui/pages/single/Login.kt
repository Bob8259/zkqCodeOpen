package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.coc.zkqcode.jar.ui.theme.AppColors
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.jar.code.auth.performSessionLogin
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun LoginScreen(onAdFreeClick: () -> Unit = {}) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isLoginButtonEnabled by remember { mutableStateOf(true) }
    var gemInfo by remember { mutableStateOf(GlobalVars.configStates[GLOBAL_SETTINGS.GEM_COUNT.key]!!.value) }
    var showMessage by remember {
        mutableStateOf(
            gemInfo.toDoubleOrNull()?.let { it < 0.0001 } ?: true
        )
    }
    var failTimesCount by remember { mutableIntStateOf(0) }
    var formattedGem by remember { mutableStateOf("") }

    val globalGemCount = GlobalVars.configStates[GLOBAL_SETTINGS.GEM_COUNT.key]!!.value
    LaunchedEffect(globalGemCount) {
        if (globalGemCount.isNotEmpty() && gemInfo.isEmpty()) {
            gemInfo = globalGemCount
            globalGemCount.toDoubleOrNull()?.let {
                showMessage = (it < 0.0001)
            }
        }
    }
    fun login(email: String, password: String) {
        isLoginButtonEnabled = false

        scope.launch {
            if (!email.contains("@")) {
                // Update UI to show error message
                gemInfo = Strings.invalidEmail
                isLoginButtonEnabled = true
                showMessage = true
                return@launch // Exit function
            }

            gemInfo = Strings.loggingIn

            try {
                val result = performSessionLogin(email, password)

                isLoginButtonEnabled = true

                // Store session key in SharedPreferences
                try {
                    context.getSharedPreferences("auth_prefs", android.content.Context.MODE_PRIVATE)
                        .edit().putString("session_key", result.sessionKey).apply()
                } catch (_: Exception) { }

                // Update gem display
                val gem = result.gem
                formattedGem = String.format(Locale.US, "%.4f", gem.toDouble())
                gem.toDoubleOrNull()?.let {
                    showMessage = (it < 0.000001)
                }
                GlobalVars.configStates[GLOBAL_SETTINGS.GEM_COUNT.key]!!.value = gem
                gemInfo = Strings.loginSuccess(formattedGem)
            } catch (e: Exception) {
                val errorMsg = e.message ?: ""
                gemInfo = if (errorMsg.contains("邮箱或密码错误")) {
                    Strings.emailPasswordError
                } else if (errorMsg.startsWith("Error")) {
                    Strings.loginFailed(errorMsg)
                } else {
                    Strings.loginFailed(errorMsg)
                }
                showMessage = true
                GlobalVars.configStates[GLOBAL_SETTINGS.GEM_COUNT.key]!!.value = ""
                failTimesCount++
                isLoginButtonEnabled = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(), horizontalAlignment = Alignment.Start
    ) {
        if (showMessage) {
            Text(
                text = Strings.adFreeMessage,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = Strings.sponsorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = (MaterialTheme.typography.labelMedium.fontSize.value + 2).sp
                )
            )
        } else {
            Text(
                text = Strings.welcomeBack,
                style = MaterialTheme.typography.labelMedium
            )
        }

        if (gemInfo.isNotEmpty()) {
            val gemValue = gemInfo.toDoubleOrNull()
            if (gemValue != null) {
                Text(
                    text = Strings.currentGems(gemValue),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Black
                )
            } else {
                Text(
                    text = gemInfo,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (gemInfo.contains("成功")) AppColors.PrimaryLight else Color.Red
                )
            }
        }

        SettingInputRow(key = GLOBAL_SETTINGS.EMAIL.key, minWidth = 120.dp)
        var isPasswordVisible by remember { mutableStateOf(false) }
        Row {
            Text(
                text = Strings.password,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .align(Alignment.CenterVertically),
                style = MaterialTheme.typography.labelMedium
            )
            BasicTextField(
                value = GlobalVars.configStates[GLOBAL_SETTINGS.PASSWORD.key]!!.value,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .widthIn(min = 120.dp)
                    .padding(end = 16.dp)
                    .widthIn(min = 50.dp)
                    .width(IntrinsicSize.Min)
                    .background(color = AppColors.CardBg, shape = RoundedCornerShape(4.dp))
                    .border(1.dp, AppColors.Border, RoundedCornerShape(4.dp))
                    .padding(4.dp)
                    .heightIn(max = 120.dp)
                    .verticalScroll(rememberScrollState()),
                onValueChange = {
                    GlobalVars.isAutoRunEnabled = false
                    GlobalVars.configStates[GLOBAL_SETTINGS.PASSWORD.key]!!.value = it
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
            )
            IconButton(
                onClick = { isPasswordVisible = !isPasswordVisible },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                )
            }
        }

        Row(Modifier.padding(bottom = 4.dp)) {
            CustomButton(
                text = Strings.login, marginTop = 0.dp, onClick = {
                    login(
                        GlobalVars.configStates[GLOBAL_SETTINGS.EMAIL.key]!!.value,
                        GlobalVars.configStates[GLOBAL_SETTINGS.PASSWORD.key]!!.value
                    )
                }, enable = isLoginButtonEnabled
            )
            CustomButton(
                text = Strings.donate, marginTop = 0.dp, onClick = {
                    gemInfo = Strings.donateInfo
                }, enable = isLoginButtonEnabled
            )
            CustomButton(
                text = Strings.logout, marginTop = 0.dp, onClick = {
                    showMessage = true
                    scope.launch {
                        GlobalVars.serverActions?.writeToConfigFile(
                            GLOBAL_SETTINGS.GEM_COUNT.key,
                            ""
                        )
                        GlobalVars.configStates[GLOBAL_SETTINGS.EMAIL.key]!!.value = ""
                        GlobalVars.configStates[GLOBAL_SETTINGS.PASSWORD.key]!!.value = ""
                        GlobalVars.configStates[GLOBAL_SETTINGS.GEM_COUNT.key]!!.value = ""
                        // Clear session key from SharedPreferences
                        try {
                            context.getSharedPreferences("auth_prefs", android.content.Context.MODE_PRIVATE)
                                .edit().remove("session_key").apply()
                        } catch (_: Exception) { }
                    }
                    gemInfo = Strings.logoutSuccess
                }, enable = isLoginButtonEnabled
            )
        }
    }
}
