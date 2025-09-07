package com.example.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary       = NvidiaGreen,
    onPrimary     = Color.Black,
    background    = DeepNavy,
    surface       = SurfaceDark,
    onSurface     = TextPrimary,
    secondary     = AccentBlue,
    error         = CriticalRed
)

@Composable
fun GPUInsightTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else lightColorScheme(),
        typography  = GPUTypography,
        content     = content
    )
}
