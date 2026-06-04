package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SocximaDarkColorScheme = darkColorScheme(
    primary = ColorMistral,
    secondary = ColorCommandR,
    tertiary = ColorDeepseek,
    background = SocximaFondo,
    surface = SocximaPanel,
    surfaceVariant = SocximaPanel,
    onPrimary = SocximaFondo,
    onSecondary = SocximaFondo,
    onTertiary = Color.White,
    onBackground = SocximaLetra,
    onSurface = SocximaLetra,
    outline = SocximaLinea
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme to lock SOCXIMA palette
    dynamicColor: Boolean = false, // Set to false to avoid system style overrides
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SocximaDarkColorScheme,
        typography = Typography,
        content = content
    )
}
