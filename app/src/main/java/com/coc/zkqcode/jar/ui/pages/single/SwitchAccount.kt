package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.utils.components.CustomButton

@Composable
fun SwitchAccount(onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White.copy(alpha = 0.9f), shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "手动切号模式已启动",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
            Text(
                text = "请切换至目标账号后关闭此窗口",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.Gray
            )
            CustomButton(
                text = "关闭窗口",
                onClick = onClose
            )
        }
    }
}