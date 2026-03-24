package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager
import com.coc.zkqcode.core.data.database.GlobalVars
import kotlinx.coroutines.launch

// Placeholder bug report page; content to be filled in later
@Composable
fun BugReport(onClose: () -> Unit) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Bug反馈（开发中）",
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            CustomButton(
                text = "关闭窗口",
                onClick = {
                    AppStateManager.setMode(AppMode.Run)
                    GlobalVars.isPlaying.value = false
                    GlobalVars.updateWindowPosition = true
                    scope.launch {
                        onClose()
                    }
                }
            )
        }
    }
}
