package com.steady.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightColorScheme = lightColorScheme(
    primary = SteadyPrimary,
    onPrimary = SteadyOnPrimary,
    secondary = SteadySecondary,
    onSecondary = SteadyOnSecondary,
    tertiary = SteadyTertiary,
    background = SteadyBackground,
    onBackground = SteadyOnSurface,
    surface = SteadySurface,
    onSurface = SteadyOnSurface,
    surfaceVariant = SteadySurfaceContainer,
    onSurfaceVariant = SteadyOnSurfaceVariant,
    surfaceContainerLowest = SteadySurface,
    surfaceContainerLow = SteadySurfaceContainer,
    surfaceContainer = SteadySurfaceContainer,
    surfaceContainerHigh = SteadySurfaceContainerOrangeAlt,
    surfaceContainerHighest = SteadySurfaceContainerOrange,
    outline = SteadyOutline,
    outlineVariant = SteadyOutlineSubtle,
    error = SteadyError,
    inverseSurface = SteadyInverseSurface,
)

private val DarkColorScheme = darkColorScheme(
    primary = SteadyPrimaryDark,
    onPrimary = SteadyOnPrimaryDark,
    secondary = SteadySecondaryDark,
    onSecondary = SteadyOnSecondaryDark,
    tertiary = SteadyTertiaryDark,
    background = SteadyBackgroundDark,
    onBackground = SteadyOnSurfaceDark,
    surface = SteadySurfaceDark,
    onSurface = SteadyOnSurfaceDark,
    onSurfaceVariant = SteadyOnSurfaceVariantDark,
    surfaceContainer = SteadySurfaceContainerDark,
    outline = SteadyOutlineDark,
    error = SteadyErrorDark,
)

private val darkExtendedColors = SteadyExtendedColors(
    surfaceContainerGreen = SteadySurfaceContainerDark,
    surfaceContainerOrange = SteadySurfaceContainerDark,
    surfaceContainerOrangeAlt = SteadySurfaceContainerDark,
    onSurfaceMuted = SteadyOnSurfaceVariantDark,
    outlineSubtle = SteadyOutlineDark,
    premiumText = SteadySecondaryDark,
    premiumBg = SteadySurfaceContainerDark,
    inverseSurface = SteadyOnSurfaceDark,
)

private val lightExtendedColors = SteadyExtendedColors(
    surfaceContainerGreen = SteadySurfaceContainerGreen,
    surfaceContainerOrange = SteadySurfaceContainerOrange,
    surfaceContainerOrangeAlt = SteadySurfaceContainerOrangeAlt,
    onSurfaceMuted = SteadyOnSurfaceMuted,
    outlineSubtle = SteadyOutlineSubtle,
    premiumText = SteadyPremiumText,
    premiumBg = SteadyPremiumBg,
    inverseSurface = SteadyInverseSurface,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) darkExtendedColors else lightExtendedColors

    CompositionLocalProvider(
        LocalAppSpacing provides AppSpacing(),
        LocalSteadyExtendedColors provides extendedColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}
