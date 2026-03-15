@file:Suppress("FunctionName")

package com.coc.zkqcode.jar.ui.pages.universal

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coc.zkqcode.jar.ui.components.CustomButton

fun LazyListScope.UniversalConfig(
    index: Int,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onNavigatePriority: (Int) -> Unit = {}
) {
    // Header
    item {
        FlowRow {
            Text(
                text = "以下是通用设置",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp),
                textAlign = TextAlign.Start
            )
            CustomButton(
                onClick = onToggleExpanded,
                text = if (isExpanded) "▼ 缩起通用设置" else "▶ 展开通用设置"
            )
        }
    }
}
