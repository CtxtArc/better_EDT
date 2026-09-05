package com.example.better_edt.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = VibrantPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = DeepDarkSurfaceVariant,
    onPrimaryContainer = VibrantPrimaryLight,
    secondary = TdGreenLight,
    onSecondary = Color.White,
    secondaryContainer = DeepDarkSurfaceVariant,
    onSecondaryContainer = TdGreenLight,
    tertiary = TpOrangeLight,
    onTertiary = Color.White,
    tertiaryContainer = DeepDarkSurfaceVariant,
    onTertiaryContainer = TpOrangeLight,
    background = DeepDarkBase,
    surface = DeepDarkSurface,
    onSurface = TextHighEmphasisDark,
    surfaceVariant = DeepDarkSurfaceVariant,
    onSurfaceVariant = TextMediumEmphasisDark,
    outline = DeepDarkOutline,
    outlineVariant = DeepDarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = VibrantPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8F0FE),
    onPrimaryContainer = VibrantPrimary,
    secondary = TdGreenLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F5E9),
    onSecondaryContainer = TdGreenLight,
    tertiary = TpOrangeLight,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFF3E0),
    onTertiaryContainer = TpOrangeLight,
    background = LightBase,
    surface = LightSurface,
    onSurface = TextHighEmphasisLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextMediumEmphasisLight,
    outline = LightOutline,
    outlineVariant = LightOutline
)

@Composable
fun BetterEdtTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surfaceVariant.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
