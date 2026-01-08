package com.coc.zkqcode.ui.pages.single

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coc.zkqcode.ui.components.CustomButton
import com.coc.zkqcode.ui.components.InputRow
import com.coc.zkqcode.ui.database.Schema
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
fun LoginScreen(configStates: Map<String, MutableState<String>>) {
    val scope = rememberCoroutineScope()
    var isLoginButtonEnabled by remember { mutableStateOf(true) }
    var gemInfo by remember { mutableStateOf(configStates["gem_count"]?.value ?: "") }
    var showMessage by remember { mutableStateOf(gemInfo.toDouble() < 0.0001) }
    var failTimesCount by remember { mutableIntStateOf(0) }
    var formattedGem by remember { mutableStateOf("") }

    fun login(email: String, password: String) {
        isLoginButtonEnabled = false
        val url: String = if (failTimesCount % 2 == 0) {
//            "http://45.64.74.97:90/api/mobile-login"
            "https://db00dd37dd04.ngrok-free.app/api/mobile-login-new"
        } else {
            "https://zkqcoc.store/api/mobile-login"
        }
        scope.launch {
            if (!email.contains("@")) {
                // 更新UI显示错误信息
                gemInfo = "请输入正确的邮箱！"
                configStates["uid"]?.value = ""
                isLoginButtonEnabled = true
                showMessage = true
                return@launch // 退出函数
            }
            // 获取随机 nonce
            val nonce = NativeTools.generateNonce()
            // 构建POST数据
            val postData = "email=$email&password=$password&nonce=$nonce"
            // 创建 OkHttpClient 实例
            val client = OkHttpClient()
            // 创建请求体
            val requestBody =
                postData.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())
            // 创建请求
            val request = Request.Builder().url(url).post(requestBody).build()

            // 发送请求
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // 请求失败处理
                    gemInfo = "登录失败: ${e.message}"
                    showMessage = true
                    configStates["uid"]?.value = ""
                    configStates["gem_count"]?.value = ""
                    failTimesCount++
                    isLoginButtonEnabled = true
                }

                override fun onResponse(call: Call, response: Response) {
                    // 请求成功处理
                    if (response.isSuccessful) {
                        isLoginButtonEnabled = true
                        val responseBody = response.body?.string()
                        // 处理响应数据
                        if (responseBody != null) {
                            // 提取 hash 之前的内容
                            val hashIndex = responseBody.indexOf(",hash=")
                            if (hashIndex != -1) {
                                val contentBeforeHash = responseBody.take(hashIndex)
                                // 提取服务器返回的 hash 值
                                val serverHash =
                                    responseBody.substring(hashIndex + 6, hashIndex + 22)
                                println(serverHash)
                                // 对比哈希值和 Nonce，返回接近 10 的 Double
                                val verifyResult =
                                    NativeTools.verifyHash(contentBeforeHash, serverHash)
                                if (kotlin.math.abs(verifyResult - 10.0) < 0.001) {
                                    // 提取用户ID
                                    val idRegex = Regex("id=([a-f0-9]+)")
                                    val idMatch = idRegex.find(responseBody)
                                    val userId = idMatch?.groupValues?.get(1) ?: ""
                                    configStates["uid"]?.value = userId

                                    // 提取 gem 信息
                                    val gemRegex = """gem=([\d.]+)""".toRegex()
                                    val gemMatch = gemRegex.find(responseBody)
                                    val passwordRegex = """password=([a-zA-Z0-9]+)""".toRegex()
                                    val passwordHash =
                                        passwordRegex.find(responseBody)?.groupValues?.get(1)
                                    if (gemMatch != null && passwordHash != null && passwordHash.length == 16) {
                                        val gem = gemMatch.groupValues[1]
                                        formattedGem =
                                            String.format(Locale.US, "%.4f", gem.toDouble())
                                        gem.toDoubleOrNull()?.let {
                                            showMessage = (it < 0.000001) //如果有宝石，就不展示提示
                                        }
                                        configStates["gem_count"]?.value = gem
                                        configStates["passwordHash"]?.value = passwordHash
                                        gemInfo = "登录成功！卡班宝石数量 $formattedGem"
                                    } else {
                                        gemInfo = "登录成功，但无法解析数据，请联系作者"
                                        showMessage = true
                                        configStates["uid"]?.value = ""
                                        configStates["gem_count"]?.value = ""
                                    }
                                } else {
                                    gemInfo =
                                        "登录失败：响应数据格式错误。可能是因为开启了代理或抓包，请关闭后重试"
                                    showMessage = true
                                    configStates["uid"]?.value = ""
                                    configStates["gem_count"]?.value = ""
                                }
                            } else {
                                gemInfo =
                                    "登录失败：响应数据格式错误。可能是因为开启了代理或抓包，请关闭后重试"
                                showMessage = true
                                configStates["uid"]?.value = ""
                                configStates["gem_count"]?.value = ""
                            }
                        } else {
                            gemInfo = "登录失败：响应体为空"
                            showMessage = true
                            configStates["uid"]?.value = ""
                            configStates["gem_count"]?.value = ""
                        }
                    } else {
                        val responseBody = response.body?.string()
                        gemInfo = "登录失败：$responseBody"
                        showMessage = true
                        failTimesCount++
                        isLoginButtonEnabled = true
                        configStates["uid"]?.value = ""
                        configStates["gem_count"]?.value = ""
                    }
                }
            })
            gemInfo = "登录中，请稍后..."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp), horizontalAlignment = Alignment.Start
    ) {
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
            value = configStates["email"]?.value ?: "",
            onValueChange = { configStates["email"]?.value = it },
            key = "email"
        )
        InputRow(
            label = Schema.GLOBAL_SETTINGS.first { it.key == "password" }.displayName,
            value = configStates["password"]?.value ?: "",
            onValueChange = { configStates["password"]?.value = it },
            key = "password"
        )

        CustomButton(
            text = "登录", onClick = {
                login(
                    configStates["email"]?.value ?: "", configStates["password"]?.value ?: ""
                )
            }, enable = isLoginButtonEnabled
        )
    }
}
