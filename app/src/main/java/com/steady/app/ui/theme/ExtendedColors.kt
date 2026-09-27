package com.steady.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class SteadyExtendedColors(
    val surfaceContainerGreen: Color,
    val surfaceContainerOrange: Color,
    val surfaceContainerOrangeAlt: Color,
    val onSurfaceMuted: Color,
    val outlineSubtle: Color,
    val premiumText: Color,
    val premiumBg: Color,
    val inverseSurface: Color,
)

val LocalSteadyExtendedColors = staticCompositionLocalOf {
    SteadyExtendedColors(
        surfaceContainerGreen = SteadySurfaceContainerGreen,
        surfaceContainerOrange = SteadySurfaceContainerOrange,
        surfaceContainerOrangeAlt = SteadySurfaceContainerOrangeAlt,
        onSurfaceMuted = SteadyOnSurfaceMuted,
        outlineSubtle = SteadyOutlineSubtle,
        premiumText = SteadyPremiumText,
        premiumBg = SteadyPremiumBg,
        inverseSurface = SteadyInverseSurface,
    )
}
