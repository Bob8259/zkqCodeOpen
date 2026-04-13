package com.coc.zkqcode.jar.ui.theme

import androidx.compose.ui.graphics.Color

// App-wide custom colors (jar module) — Google Material-inspired palette
object AppColors {
    // Primary interactive blue — buttons, links, active states
    val Primary = Color(0xFF1a73e8)
    // Lighter blue — hover states, gradients, toggle buttons, config cards
    val PrimaryLight = Color(0xFF4285f4)
    // Green — toggle ON state, collapse/expand buttons
    val Success = Color(0xFF34a853)
    // Red — destructive actions
    val Danger = Color(0xFFea4335)
    // Light grey page background
    val Background = Color(0xFFF8F9FA)
    // White card surfaces
    val CardBg = Color(0xFFFFFFFF)
    // Near-black body text
    val TextPrimary = Color(0xFF202124)
    // Grey secondary/hint text
    val TextSecondary = Color(0xFF5f6368)
    // Light grey borders and inactive states
    val Border = Color(0xFFdadce0)

    // Teal is actively used across config pages as the expand/collapse button color
    val Teal = Success
}
