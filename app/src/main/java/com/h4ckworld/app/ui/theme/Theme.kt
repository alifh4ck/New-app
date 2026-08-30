package com.h4ckworld.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PrimaryGreen = Color(0xFF17C964)
private val DarkBg = Color(0xFF0B0F0D)

private val DarkColors = darkColorScheme(
    primary = PrimaryGreen,
    background = DarkBg,
    surface = Color(0xFF141815)
)

private val LightColors = lightColorScheme(
    primary = PrimaryGreen
)

@Composable
fun H4ckWorldTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, content = content)
}
