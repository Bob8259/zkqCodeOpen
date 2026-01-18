package com.coc.zkqcode.jar.ui.pages.mainbase

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coc.zkqcode.utils.components.CustomButton

@Composable
fun NightBaseConfig(index: Int) {
    var isNightBaseExpanded by remember { mutableStateOf(true) }
    FlowRow {
        Text(
            text = "以下是夜世界设置",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(10.dp),
            textAlign = TextAlign.Start
        )
        // 添加一个按钮来控制缩放
        CustomButton(
            onClick = { isNightBaseExpanded = !isNightBaseExpanded },
            text = if (isNightBaseExpanded) "▼ 缩起主世界设置" else "▶ 展开主世界设置"
        )
    }
    if (isNightBaseExpanded) {
        
    }
}