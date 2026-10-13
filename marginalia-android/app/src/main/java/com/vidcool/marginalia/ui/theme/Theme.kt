package com.vidcool.marginalia.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MargeBlue = Color(0xFF1E3A5F)
private val MargeAccent = Color(0xFF4A90D9)

private val LightColors = lightColorScheme(
    primary = MargeBlue,
    onPrimary = Color.White,
    secondary = MargeAccent,
    surface = Color(0xFFF5F7FA),
    onSurface = Color(0xFF1A1A1A),
)

private val DarkColors = darkColorScheme(
    primary = MargeAccent,
    onPrimary = Color(0xFF0D1B2A),
    secondary = Color(0xFF7EB6FF),
    surface = Color(0xFF121820),
    onSurface = Color(0xFFE8EEF5),
)

@Composable
fun MarginaliaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
