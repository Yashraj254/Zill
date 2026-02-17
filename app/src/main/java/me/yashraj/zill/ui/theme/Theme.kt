package me.yashraj.zill.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val IcyNightDark = ColorScheme(
    // ─── Primary ─────────────────────────────
    primary = IcyAccent,                    // soft ice blue
    onPrimary = IcyBgBottom,
    primaryContainer = Color(0xFF1A2A4A),
    onPrimaryContainer = IcyPrimary,
    inversePrimary = Color(0xFF6F8FFF),

    // ─── Secondary ───────────────────────────
    secondary = IcySecondary,
    onSecondary = IcyBgBottom,
    secondaryContainer = Color(0xFF1B2438),
    onSecondaryContainer = IcyPrimary,

    // ─── Tertiary ────────────────────────────
    tertiary = Color(0xFFB6C9FF),
    onTertiary = IcyBgBottom,
    tertiaryContainer = Color(0xFF22315A),
    onTertiaryContainer = IcyPrimary,

    // ─── Background & Surface ────────────────
    background = IcyBgBottom,
    onBackground = IcyPrimary,

    surface = IcySurface,
    onSurface = IcyPrimary,

    surfaceVariant = ControlSurface,
    onSurfaceVariant = IcySecondary,
    surfaceTint = IcyAccent,

    // ─── Inverse ─────────────────────────────
    inverseSurface = IcyPrimary,
    inverseOnSurface = IcyBgBottom,

    // ─── Error ───────────────────────────────
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    // ─── Outline & Scrim ─────────────────────
    outline = Color(0xFF4B5A7A),
    outlineVariant = Color(0xFF2A3552),
    scrim = Color(0xFF000000),

    // ─── Surface Elevation ───────────────────
    surfaceDim = IcyBgBottom,
    surfaceBright = IcyBgTop,

    surfaceContainerLowest = IcyBgBottom,
    surfaceContainerLow = ControlSurfaceAlt,
    surfaceContainer = ControlSurface,
    surfaceContainerHigh = IcySurface,
    surfaceContainerHighest = Color(0xFF1A2642),

    // ─── Fixed Colors (used by dynamic UI) ───
    primaryFixed = Color(0xFFDCE6FF),
    primaryFixedDim = IcyAccent,
    onPrimaryFixed = IcyBgBottom,
    onPrimaryFixedVariant = Color(0xFF2A3E7A),

    secondaryFixed = Color(0xFFD5DCF2),
    secondaryFixedDim = IcySecondary,
    onSecondaryFixed = IcyBgBottom,
    onSecondaryFixedVariant = Color(0xFF2F3A55),

    tertiaryFixed = Color(0xFFE1E6FF),
    tertiaryFixedDim = Color(0xFFB6C9FF),
    onTertiaryFixed = IcyBgBottom,
    onTertiaryFixedVariant = Color(0xFF303E75),
)


@Composable
fun ZillTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = IcyNightDark

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}