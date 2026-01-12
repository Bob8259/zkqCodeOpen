package com.coc.zkqcode.jar.ui.pages.single

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.utils.components.CustomButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SwitchAccount(onClose: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var accountNumber by remember { mutableStateOf("1") }

    val imageBitmap = remember {
        try {
            context.assets.open("switch_explain.png").use {
                BitmapFactory.decodeStream(it).asImageBitmap()
            }
        } catch (e: Exception) {
            null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White.copy(alpha = 0.9f), shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Top Text aligned to start
            Text(
                text = "小提示：在悬浮窗点击此按钮，即可回到切号工具",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Image
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Switch Explain",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            } else {
                Text(
                    text = "Image 'switch_explain.png' not found",
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Divider
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.Gray
            )

            // Account Selection Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {


                Text(
                    text = "切换到第",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Color.Black
                )

                BasicTextField(
                    value = accountNumber,
                    onValueChange = { newValue ->
                        // Only allow numeric input
                        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                            accountNumber = newValue.filter { it.isDigit() }
                        }
                    },
                    modifier = Modifier
                        .width(60.dp)
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(4.dp)
                        .height(24.dp), // Adjust height to match typical text field
                    singleLine = true
                )

                Text(
                    text = "个账号",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Color.Black
                )


            }
            Row {
                CustomButton(text = "▼", onClick = {
                    val current = accountNumber.toIntOrNull() ?: 1
                    if (current > 1) {
                        accountNumber = (current - 1).toString()
                    }
                }, marginTop = 0.dp)
                CustomButton(text = "▲", onClick = {
                    val current = accountNumber.toIntOrNull() ?: 0
                    accountNumber = (current + 1).toString()
                }, marginTop = 0.dp)
            }
            // Confirm Button aligned to start
            CustomButton(
                text = "确认切号",
                onClick = {
                    scope.launch {
                        delay(3000)
                        onClose()
                    }
                }
            )
        }
    }
}