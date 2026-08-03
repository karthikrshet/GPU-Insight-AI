package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CyberSkyBlue,
    onPrimary = DeepIndigo,
    primaryContainer = DeepIndigo,
    onPrimaryContainer = LavenderLight,
    secondary = ElectricTeal,
    onSecondary = ObsidianDark,
    tertiary = RoseError,
    background = ObsidianDark,
    onBackground = TextPrimary,
    surface = SurfaceSlate,
    onSurface = TextPrimary,
    surfaceVariant = CardBackground,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = RoseError,
    onError = ObsidianDark
)

@Composable
fun GpuInsightTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = ObsidianDark.toArgb()
            window.navigationBarColor = ObsidianDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
