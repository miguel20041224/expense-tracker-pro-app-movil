package com.finpulse.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = FinEmerald,
    onPrimary = Color.White,
    secondary = FinSlate800,
    background = FinSlate50,
    surface = Color.White,
    onBackground = FinSlate900,
    onSurface = FinSlate900,
    error = FinRose,
)

private val DarkColors = darkColorScheme(
    primary = FinEmerald,
    onPrimary = Color.White,
    secondary = FinSlate100,
    background = FinSlate950,
    surface = FinSlate900,
    onBackground = FinSlate100,
    onSurface = FinSlate100,
    error = FinRose,
)

@Composable
fun FinPulseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = FinTypography,
        content = content,
    )
}
