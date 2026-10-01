package com.steady.app.ui.theme

import androidx.compose.ui.graphics.Color

// Light palette — from the Steady Stitch design system (projects/13915625241051555386, DESIGN.md).
val SteadyBackground = Color(0xFFFAF7F1)
val SteadySurface = Color(0xFFFFFFFF)
val SteadySurfaceContainer = Color(0xFFF3EEE3)
val SteadySurfaceContainerGreen = Color(0xFFEAF1EC)
val SteadySurfaceContainerOrange = Color(0xFFFDEAE2)
val SteadySurfaceContainerOrangeAlt = Color(0xFFF4E3D7)
val SteadyOutline = Color(0xFFEFEAE0)
val SteadyOutlineSubtle = Color(0xFFF3EFE6)
val SteadyOnSurface = Color(0xFF1E2420)
val SteadyOnSurfaceVariant = Color(0xFF7A7F74)
val SteadyOnSurfaceMuted = Color(0xFFA6ABA0)
val SteadyPrimary = Color(0xFF2F5D50)
val SteadyOnPrimary = Color(0xFFFFFFFF)
val SteadySecondary = Color(0xFFE2895A)
val SteadyOnSecondary = Color(0xFFFFFFFF)
val SteadyTertiary = Color(0xFF5B8AA6)
val SteadyError = Color(0xFFC0492E)
val SteadyPremiumText = Color(0xFFB8613A)
val SteadyPremiumBg = Color(0xFFFDEAE2)
val SteadyInverseSurface = Color(0xFF171C18)

// Dark palette — no Stitch dark spec exists yet; tonal derivation kept only so the
// existing ThemeMode.DARK setting keeps working. Replace once a dark design ships.
val SteadyBackgroundDark = Color(0xFF141815)
val SteadySurfaceDark = Color(0xFF1E2420)
val SteadySurfaceContainerDark = Color(0xFF262B26)
val SteadyOutlineDark = Color(0xFF3A403A)
val SteadyOnSurfaceDark = Color(0xFFE5E7E1)
val SteadyOnSurfaceVariantDark = Color(0xFFB7BCAF)
val SteadyPrimaryDark = Color(0xFFA0D1C0)
val SteadyOnPrimaryDark = Color(0xFF0A3128)
val SteadySecondaryDark = Color(0xFFF3AE85)
val SteadyOnSecondaryDark = Color(0xFF4A2411)
val SteadyTertiaryDark = Color(0xFFA9CBDD)
val SteadyErrorDark = Color(0xFFFFB4A3)

// Onboarding hero palette — a separate Stitch export for the welcome/scan/progress slides,
// distinct from the app-wide Steady* tokens above (deeper primary green, warmer background).
object WelcomeDesignColors {
    val Background = Color(0xFFFCF9F3)
    val Primary = Color(0xFF154539)
    val PrimaryContainer = Color(0xFF2F5D50)
    val PrimaryFixed = Color(0xFFBCEDDC)
    val Secondary = Color(0xFF934A21)
    val SecondaryContainer = Color(0xFFFD9F6E)
    val Tertiary = Color(0xFF06425C)
    val TertiaryContainer = Color(0xFF285A74)
    val OnSurfaceVariant = Color(0xFF404945)
    val ProgressTrack = Color(0xFFEBE8E2)
    val InactiveDot = Color(0xFFD5DDD2)
}
