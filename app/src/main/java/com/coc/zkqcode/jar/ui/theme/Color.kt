package com.coc.zkqcode.jar.ui.theme

import androidx.compose.ui.graphics.Color

// App-wide custom colors (jar module) — Liquid Glass / Light-Blue palette
object AppColors {
    // Primary interactive blue — buttons, links, active states
    val Primary = Color(0xFF4299E1)

    // Lighter blue — hover states, gradients, toggle buttons, config cards
    val PrimaryLight = Color(0xFF63B3ED)

    // iOS-style green — toggle ON state
    val Success = Color(0xFF4CD964)

    // Light blue page background — fully opaque
    val Background = Color(0xFFE8F4FC)

    // More opaque white card surfaces (95% opacity) so SettingSection stands out against the image
    val CardBg = Color(0xF2FFFFFF)

    // Dark slate body text
    val TextPrimary = Color(0xFF2D3748)

    // Translucent light-blue border for glass cards
    val Border = Color(0x66ADD8E6)

    // Glass-specific border alias (same as Border, kept for semantic clarity)
    val GlassBorder = Border

    // Teal — expand/collapse buttons, status accents
    val Teal = Color(0xFF38B2AC)

    // Inactive nav tab text colour (matches HTML --nav-inactive)
    val NavInactive = Color(0xFF718096)
}
