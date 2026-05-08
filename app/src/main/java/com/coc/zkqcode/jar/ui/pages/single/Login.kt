package com.coc.zkqcode.jar.ui.pages.single

import android.content.Intent
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
import com.coc.zkqcode.jar.ui.theme.AppColors
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.nativehelper.RustTools
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.Locale
import com.coc.zkqcode.core.util.crypto.solvePoW
import com.coc.zkqcode.jar.code.universal.network.UrlFallbackUtil
import com.coc.zkqcode.jar.code.auth.getNetworkTimestamp
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(onAdFreeClick: () -> Unit = {}) {
    val scope = rememberCoroutineScope()
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
                gemInfo = "请输入正确的邮箱！"
                isLoginButtonEnabled = true
                showMessage = true
                return@launch // Exit function
            }

            // 0. Fetch PoW Challenge with fallback
            gemInfo = "登录中，请稍后..."

            var powNonce: String

            try {
                val responseStr = withContext(Dispatchers.IO) {
                    UrlFallbackUtil.executeGetWithFallbackAndReadBody("api/pow/challenge")
                }
                val json = JSONObject(responseStr)
                powNonce = json.getString("nonce")
            } catch (e: Exception) {
                gemInfo = "验证获取失败: ${e.message}"
                isLoginButtonEnabled = true
                showMessage = true
                return@launch
            }

            // 0.5 Solve PoW
            val powSalt: String = solvePoW(powNonce)

            // 1. Prepare Payload
            val timestamp = getNetworkTimestamp()

            val payload = "email=$email&password=$password&timestamp=$timestamp"
            // 2. Encrypt Payload via Native Layer
            // Returns: "my_public_key,nonce,ciphertext" (comma separated)
            val encryptionResult = try {
                RustTools.encryptLoginPayload(payload)
            } catch (e: Exception) {
                gemInfo = "准备信息失败: ${e.message}"
                isLoginButtonEnabled = true
                showMessage = true
                return@launch
            }
            val parts = encryptionResult.split(",")
            if (parts.size != 3) {
                gemInfo = "传输信息失败，请重试"
                isLoginButtonEnabled = true
                showMessage = true
                return@launch
            }
            val myPublicKey = parts[0]
            val nonce = parts[1]
            val ciphertext = parts[2]

            // 3. Send Request with fallback
            // Assuming server expects: public_key, nonce, data (ciphertext)
            val postData =
                "public_key=$myPublicKey&nonce=$nonce&data=$ciphertext&pow_nonce=$powNonce&pow_salt=$powSalt"
            val requestBody =
                postData.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())

            try {
                val responseBody = withContext(Dispatchers.IO) {
                    UrlFallbackUtil.executeWithFallbackAndReadBody(
                        endpoint = "api/mobile-login",
                        requestBuilder = { baseUrl ->
                            Request.Builder()
                                .url("${baseUrl}api/mobile-login")
                                .post(requestBody)
                                .build()
                        }
                    )
                }

                isLoginButtonEnabled = true
                // 4. Decrypt Response via Native Layer
                val decrypted = try {
                    RustTools.decryptLoginResponse(responseBody)
                } catch (e: Exception) {
                    "Error: Decryption exception: ${e.message}"
                }

                // 5. Parse Decrypted Result
                // Expected format: gem=xxx OR Error message
                if (decrypted.startsWith("Error")) {
                    gemInfo = "登录失败: $decrypted"
                    showMessage = true
                    GlobalVars.configStates[GLOBAL_SETTINGS.GEM_COUNT.key]!!.value = ""
                } else {
                    val gemRegex = """gem=([\d.]+)""".toRegex()
                    val gemMatch = gemRegex.find(decrypted)

                    if (gemMatch != null) {
                        val gem = gemMatch.groupValues[1]
                        formattedGem = String.format(Locale.US, "%.4f", gem.toDouble())
                        gem.toDoubleOrNull()?.let {
                            showMessage = (it < 0.000001)
                        }
                        GlobalVars.configStates[GLOBAL_SETTINGS.GEM_COUNT.key]!!.value = gem
                        gemInfo = "登录成功！卡班积分数量 $formattedGem"
                    } else {
                        gemInfo = "登录成功，但无法解析数据: $decrypted"
                        showMessage = true
                        GlobalVars.configStates[GLOBAL_SETTINGS.GEM_COUNT.key]!!.value = ""
                    }
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: ""
                gemInfo = if (errorMsg.contains("邮箱或密码错误")) {
                    "邮箱或密码错误，请检查后重试"
                } else {
                    "登录失败: $errorMsg"
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
                text = "登录后免广告，运行更高效！不登陆也可以运行。免广告每天花费0.15卡班积分，用多久扣多久。",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = "免费辅助制作不易，希望大家多多赞助。你的赞助是紫孔雀继续发展的唯一动力！",
                color = Color.Red,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = (MaterialTheme.typography.labelMedium.fontSize.value + 2).sp
                )
            )
        } else {
            Text(
                text = "欢迎回来，感谢赞助！紫孔雀因你而变得更好！",
                style = MaterialTheme.typography.labelMedium
            )
        }

        if (gemInfo.isNotEmpty()) {
            val gemValue = gemInfo.toDoubleOrNull()
            if (gemValue != null) {
                Text(
                    text = "当前宝石数量：%.5f".format(gemValue),
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
                text = GLOBAL_SETTINGS.PASSWORD.displayName,
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
                text = "登录", marginTop = 0.dp, onClick = {
                    login(
                        GlobalVars.configStates[GLOBAL_SETTINGS.EMAIL.key]!!.value,
                        GlobalVars.configStates[GLOBAL_SETTINGS.PASSWORD.key]!!.value
                    )
                }, enable = isLoginButtonEnabled
            )
            val context = LocalContext.current
            CustomButton(
                text = "捐赠", marginTop = 0.dp, onClick = {
                    gemInfo = "前往官网即可注册捐赠，官网链接https://zkqcoc.store\n"
                    val url = UrlFallbackUtil.getPrimaryUrl()
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    // Required when startActivity is called from a non-Activity context
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    onAdFreeClick()
                }, enable = isLoginButtonEnabled
            )
            CustomButton(
                text = "退出", marginTop = 0.dp, onClick = {
                    showMessage = true
                    scope.launch {
                        GlobalVars.serverActions?.writeToConfigFile(
                            GLOBAL_SETTINGS.GEM_COUNT.key,
                            ""
                        )
                        GlobalVars.configStates[GLOBAL_SETTINGS.EMAIL.key]!!.value = ""
                        GlobalVars.configStates[GLOBAL_SETTINGS.PASSWORD.key]!!.value = ""
                        GlobalVars.configStates[GLOBAL_SETTINGS.GEM_COUNT.key]!!.value = ""
                    }
                    gemInfo = "退出成功"
                }, enable = isLoginButtonEnabled
            )
        }
    }
}
