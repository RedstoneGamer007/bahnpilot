package de.bahnpilot.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFEC0000),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEC0000).copy(alpha = 0.2f),
    secondary = Color(0xFFC8FF00),
    background = Color(0xFF07070A),
    surface = Color(0xFF14141A),
    surfaceVariant = Color(0xFF1C1C22),
    onBackground = Color(0xFFF5F5F5),
    onSurface = Color(0xFFF5F5F5),
    error = Color(0xFFEC0000)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFEC0000),
    onPrimary = Color.White,
    secondary = Color(0xFF0A0A0F),
    background = Color(0xFFF8F8F8),
    surface = Color.White
)

@Composable
fun BahnpilotTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
