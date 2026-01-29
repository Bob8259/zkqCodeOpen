package com.coc.zkqcode.jar.ui.pages.single

import android.content.Intent
import com.coc.zkqcode.BuildConfig
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.coc.zkqcode.core.ui.components.CustomButton
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.nativehelper.RustTools
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.Locale
import java.security.MessageDigest
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Suppress("KotlinUnreachableCode")
private suspend fun solvePoW(nonce: String): String = withContext(Dispatchers.Default) {
    var salt = 0
    val md = MessageDigest.getInstance("SHA-256")
    while (true) {
        val saltStr = salt.toString()
        val data = (nonce + saltStr).toByteArray()
        val hashBytes = md.digest(data)

        // Check for 0000 (first 2 bytes are 0) and 5th char < 3 (high nibble of 3rd byte < 3)
        if (hashBytes[0] == 0.toByte() && hashBytes[1] == 0.toByte()) {
            val highNibble = (hashBytes[2].toInt() and 0xFF) ushr 4
            if (highNibble < 3) {
                return@withContext saltStr
            }
        }
        salt++
    }
    return@withContext "" // Should not reach here
}

@Suppress("AssignedValueIsNeverRead")
@Composable
fun LoginScreen() {
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
    val serverPublicKey = BuildConfig.SERVER_PUBLIC_KEY

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
        val baseURL = "https://replit.aitutor.ink/"

        scope.launch {
            if (!email.contains("@")) {
                // 更新UI显示错误信息
                gemInfo = "请输入正确的邮箱！"
                isLoginButtonEnabled = true
                showMessage = true
                return@launch // 退出函数
            }

            // 0. Fetch PoW Challenge
            gemInfo = "登录中，请稍后..."

            val httpClient = OkHttpClient()
            var powNonce: String

            try {
                val challengeRequest =
                    Request.Builder().url("${baseURL}api/pow/challenge").get().build()
                val responseStr = withContext(Dispatchers.IO) {
                    httpClient.newCall(challengeRequest).execute().use { response ->
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")
                        response.body?.string() ?: ""
                    }
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
            val timestamp = System.currentTimeMillis()

            val payload = "email=$email&password=$password&timestamp=$timestamp"
            // 2. Encrypt Payload via Native Layer
            // Returns: "my_public_key,nonce,ciphertext" (comma separated)
            val encryptionResult = try {
                RustTools.encryptLoginPayload(payload, serverPublicKey)
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

            // 3. Send Request
            // Assuming server expects: public_key, nonce, data (ciphertext)
            val postData =
                "public_key=$myPublicKey&nonce=$nonce&data=$ciphertext&pow_nonce=$powNonce&pow_salt=$powSalt"
            val client = OkHttpClient()
            val requestBody =
                postData.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())
            val request =
                Request.Builder().url("${baseURL}api/mobile-login").post(requestBody).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    gemInfo = "登录失败: ${e.message}"
                    showMessage = true
                    GlobalVars.configStates[GLOBAL_SETTINGS.GEM_COUNT.key]!!.value = ""
                    failTimesCount++
                    isLoginButtonEnabled = true
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        isLoginButtonEnabled = true
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
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
                                    GlobalVars.configStates[GLOBAL_SETTINGS.GEM_COUNT.key]!!.value =
                                        gem
                                    gemInfo = "登录成功！卡班宝石数量 $formattedGem"
                                } else {
                                    gemInfo = "登录成功，但无法解析数据: $decrypted"
                                    showMessage = true
                                    GlobalVars.configStates[GLOBAL_SETTINGS.GEM_COUNT.key]!!.value =
                                        ""
                                }
                            }
                        } else {
                            gemInfo = "登录失败：响应体为空"
                            showMessage = true
                            GlobalVars.configStates[GLOBAL_SETTINGS.GEM_COUNT.key]!!.value = ""
                        }
                    } else {
                        val responseBody = response.body?.string()
                        gemInfo = "登录失败：$responseBody"
                        showMessage = true
                        failTimesCount++
                        isLoginButtonEnabled = true
                        GlobalVars.configStates[GLOBAL_SETTINGS.GEM_COUNT.key]!!.value = ""
                    }
                }
            })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(), horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "欢迎使用新版紫孔雀1.00\nQQ群:729404054\n使用前请仔细阅读官网教程和注意事项。",
            style = MaterialTheme.typography.labelMedium
        )
        if (showMessage) {
            Text(
                text = "登录后免广告，运行更高效！不登陆也可以运行。免广告每天花费0.2卡班宝石，用多久扣多久。",
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
                    color = if (gemInfo.contains("成功")) Color(0xFF2196F3) else Color.Red
                )
            }
        }

        SettingInputRow(key = GLOBAL_SETTINGS.EMAIL.key)
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
                    .background(
                        color = Color.LightGray,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(4.dp)
                    .align(Alignment.CenterVertically)
                    .heightIn(max = 120.dp),
                onValueChange = {
                    GlobalVars.isAutoRunEnabled = false
                    GlobalVars.configStates[GLOBAL_SETTINGS.PASSWORD.key]!!.value = it
                },
                singleLine = true,
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
                text = "注册", marginTop = 0.dp, onClick = {
                    gemInfo = "前往官网即可注册，官网链接zkq.netlify.app。\n如有疑问请加群咨询。"
                    val url = "https://zkq.netlify.app/signup"
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    context.startActivity(intent)
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
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f)
        )
    }
}
