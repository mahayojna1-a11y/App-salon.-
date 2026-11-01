package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val IhwColorScheme = lightColorScheme(
    primary = IhwMaroon,
    onPrimary = Color.White,
    primaryContainer = IhwMaroonTint,
    onPrimaryContainer = IhwDarkMaroon,
    secondary = IhwGold,
    onSecondary = IhwNavyText,
    secondaryContainer = IhwGoldLight,
    onSecondaryContainer = IhwGoldDark,
    tertiary = IhwBookingMaroon,
    onTertiary = Color.White,
    background = IhwCreamBg,
    onBackground = IhwNavyText,
    surface = IhwSurface,
    onSurface = IhwNavyText,
    surfaceVariant = IhwCreamCard,
    onSurfaceVariant = IhwMutedText,
    outline = IhwGoldBorder
)

@Composable
fun IhwTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = IhwColorScheme,
        typography = Typography,
        content = content
    )
}

// Backwards compatibility alias
@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    IhwTheme(content = content)
}
