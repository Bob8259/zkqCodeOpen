package com.coc.zkqcode.jar.ui.pages.single

import android.content.Intent
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
import com.coc.zkqcode.utils.components.CustomButton
import com.coc.zkqcode.utils.components.GlobalVars
import com.coc.zkqcode.utils.components.InputRow
import com.coc.zkqcode.utils.database.Schema
import com.coc.zkqcode.zkqnative.NativeTools
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

@Suppress("AssignedValueIsNeverRead")
@Composable
fun LoginScreen() {
    val scope = rememberCoroutineScope()
    var isLoginButtonEnabled by remember { mutableStateOf(true) }
    var gemInfo by remember { mutableStateOf(GlobalVars.configStates["gem_count"]?.value ?: "") }
    var showMessage by remember {
        mutableStateOf(
            gemInfo.toDoubleOrNull()?.let { it < 0.0001 } ?: true
        )
    }
    var failTimesCount by remember { mutableIntStateOf(0) }
    var formattedGem by remember { mutableStateOf("") }
    val serverPublicKey = "171abec025499684b76daa59065c0c4e86b6707e7ed3502d95919a0c1dfa305d" //Hex
    
    val globalGemCount = GlobalVars.configStates["gem_count"]?.value
    LaunchedEffect(globalGemCount) {
        if (!globalGemCount.isNullOrEmpty() && gemInfo.isEmpty()) {
            gemInfo = globalGemCount
            globalGemCount.toDoubleOrNull()?.let {
                showMessage = (it < 0.0001)
            }
        }
    }
    fun login(email: String, password: String) {
        isLoginButtonEnabled = false
        val url: String = if (failTimesCount % 2 == 0) {
            "http://45.64.74.97:90/api/mobile-login-new"
        } else {
            "https://zkqcoc.store/api/mobile-login-new"
        }
        scope.launch {
            if (!email.contains("@")) {
                // 更新UI显示错误信息
                gemInfo = "请输入正确的邮箱！"
                isLoginButtonEnabled = true
                showMessage = true
                return@launch // 退出函数
            }

            // 1. Prepare Payload
            val timestamp = System.currentTimeMillis()

            val payload = "email=$email&password=$password&timestamp=$timestamp"
            // 2. Encrypt Payload via Native Layer
            // Returns: "my_public_key,nonce,ciphertext" (comma separated)
            val encryptionResult = try {
                NativeTools.encryptLoginPayload(payload, serverPublicKey)
            } catch (e: Exception) {
                gemInfo = "加密失败: ${e.message}"
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
            val postData = "public_key=$myPublicKey&nonce=$nonce&data=$ciphertext"
            val client = OkHttpClient()
            val requestBody =
                postData.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())
            val request = Request.Builder().url(url).post(requestBody).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    gemInfo = "登录失败: ${e.message}"
                    showMessage = true
                    GlobalVars.configStates["gem_count"]?.value = ""
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
                                NativeTools.decryptLoginResponse(responseBody)
                            } catch (e: Exception) {
                                "Error: Decryption exception: ${e.message}"
                            }

                            // 5. Parse Decrypted Result
                            // Expected format: gem=xxx OR Error message
                            if (decrypted.startsWith("Error")) {
                                gemInfo = "登录失败: $decrypted"
                                showMessage = true
                                GlobalVars.configStates["gem_count"]?.value = ""
                            } else {
                                val gemRegex = """gem=([\d.]+)""".toRegex()
                                val gemMatch = gemRegex.find(decrypted)

                                if (gemMatch != null) {
                                    val gem = gemMatch.groupValues[1]
                                    formattedGem = String.format(Locale.US, "%.4f", gem.toDouble())
                                    gem.toDoubleOrNull()?.let {
                                        showMessage = (it < 0.000001)
                                    }
                                    GlobalVars.configStates["gem_count"]?.value = gem
                                    gemInfo = "登录成功！卡班宝石数量 $formattedGem"
                                } else {
                                    gemInfo = "登录成功，但无法解析数据: $decrypted"
                                    showMessage = true
                                    GlobalVars.configStates["gem_count"]?.value = ""
                                }
                            }
                        } else {
                            gemInfo = "登录失败：响应体为空"
                            showMessage = true
                            GlobalVars.configStates["gem_count"]?.value = ""
                        }
                    } else {
                        val responseBody = response.body?.string()
                        gemInfo = "登录失败：$responseBody"
                        showMessage = true
                        failTimesCount++
                        isLoginButtonEnabled = true
                        GlobalVars.configStates["gem_count"]?.value = ""
                    }
                }
            })
            gemInfo = "登录中，请稍后..."
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

        InputRow(
            label = Schema.GLOBAL_SETTINGS.first { it.key == "email" }.displayName,
            value = GlobalVars.configStates["email"]?.value ?: "",
            onValueChange = { GlobalVars.configStates["email"]?.value = it },
        )
        var isPasswordVisible by remember { mutableStateOf(false) }
        Row {
            Text(
                text = Schema.GLOBAL_SETTINGS.first { it.key == "password" }.displayName,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .align(Alignment.CenterVertically),
                style = MaterialTheme.typography.labelMedium
            )
            BasicTextField(
                value = GlobalVars.configStates["password"]?.value ?: "",
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
                    GlobalVars.configStates["password"]?.value = it
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
                        GlobalVars.configStates["email"]?.value ?: "",
                        GlobalVars.configStates["password"]?.value ?: ""
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
                        GlobalVars.fileActions?.writeToConfigFile("gem_count", "")
                        GlobalVars.configStates["email"]?.value = ""
                        GlobalVars.configStates["password"]?.value = ""
                        GlobalVars.configStates["gem_count"]?.value = ""
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
