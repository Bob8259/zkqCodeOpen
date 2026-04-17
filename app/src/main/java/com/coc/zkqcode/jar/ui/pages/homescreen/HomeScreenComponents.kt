package com.coc.zkqcode.jar.ui.pages.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.jar.ui.components.ExplainIcon
import com.coc.zkqcode.jar.ui.theme.AppColors

// Mirrors the HTML .quick-btn glass card with dot + title + subtitle + arrow.
@Composable
internal fun QuickActionCard(
    title: String, subtitle: String, dotColor: Color, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .border(1.5.dp, AppColors.GlassBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }, shape = RoundedCornerShape(12.dp), color = AppColors.CardBg, shadowElevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(dotColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium, color = AppColors.TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subtitle, style = MaterialTheme.typography.bodySmall, color = AppColors.NavInactive
                )
                Text(
                    text = "›", style = MaterialTheme.typography.bodyMedium, color = AppColors.NavInactive
                )
            }
        }
    }
}

// Mirrors the HTML .tips-item tile: light-blue bg, centered text, optional inline explain icon.
@Composable
internal fun TipsActionTile(
    text: String, onClick: () -> Unit, modifier: Modifier = Modifier, explain: String? = null
) {
    Surface(
        modifier = modifier
            .heightIn(min = 70.dp)
            .border(1.dp, AppColors.Primary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = AppColors.Primary.copy(alpha = 0.12f),
        shadowElevation = 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium, color = AppColors.Primary, textAlign = TextAlign.Center
                )
                explain?.let {
                    Spacer(modifier = Modifier.width(4.dp))
                    ExplainIcon(explain = it)
                }
            }
        }
    }
}
