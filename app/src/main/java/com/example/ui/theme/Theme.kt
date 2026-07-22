package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SaffronLight,
    onPrimary = Color(0xFF451A03),
    primaryContainer = SaffronPrimary,
    onPrimaryContainer = Color(0xFFFEF3C7),
    secondary = IndigoLight,
    onSecondary = Color.White,
    secondaryContainer = IndigoSecondary,
    onSecondaryContainer = IndigoContainer,
    tertiary = SacredOrange,
    background = MandirDarkBackground,
    surface = MandirDarkSurface,
    surfaceVariant = MandirDarkSurfaceVariant,
    onBackground = Color(0xFFF3F4F6),
    onSurface = Color(0xFFF3F4F6)
)

private val LightColorScheme = lightColorScheme(
    primary = SaffronPrimary,
    onPrimary = Color.White,
    primaryContainer = SaffronContainer,
    onPrimaryContainer = OnSaffronContainer,
    secondary = IndigoSecondary,
    onSecondary = Color.White,
    secondaryContainer = IndigoContainer,
    onSecondaryContainer = OnIndigoContainer,
    tertiary = SacredOrange,
    background = MandirCreamBackground,
    surface = MandirSurfaceLight,
    surfaceVariant = MandirSurfaceVariant,
    onBackground = Color(0xFF1C1917),
    onSurface = Color(0xFF1C1917)
)

@Composable
fun ShivaMandirTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
