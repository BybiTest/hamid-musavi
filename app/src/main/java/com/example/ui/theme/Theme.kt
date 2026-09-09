package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SunsetOrange,
    onPrimary = Color.White,
    primaryContainer = SunsetOrangeVariant,
    onPrimaryContainer = Color.White,
    secondary = ElectricPurple,
    onSecondary = Color.White,
    secondaryContainer = ElectricViolet,
    onSecondaryContainer = Color.White,
    tertiary = VipGold,
    onTertiary = Color.Black,
    background = DarkBg,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCardElevated,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceCardBorder,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun ReelsStudioTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
