package com.coc.zkqcode.jar.ui.pages.homescreen

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.coc.zkqcode.jar.ui.components.CustomAlertDialog
import com.coc.zkqcode.jar.ui.theme.AppColors

// Announcement popup reusing the "注意事项" dialog pattern from Components.kt
@Composable
fun AnnouncementDialog(onDismiss: () -> Unit) {
    CustomAlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "公告",
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.TextPrimary
            )
        },
        text = {
            Text(
                text = "这里是公告内容的占位文本。",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("明白了", color = AppColors.Primary)
            }
        }
    )
}
