package com.coc.zkqcode.test

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

class MyPluginImpl : PluginUI {
    @Composable
    override fun ShowIcon(imagePath: String) {
        val bitmap = BitmapFactory.decodeFile(imagePath)
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "加载自私有目录",
                modifier = Modifier.size(200.dp)
            )
        } else {
            Text("图片加载失败")
        }
    }
}