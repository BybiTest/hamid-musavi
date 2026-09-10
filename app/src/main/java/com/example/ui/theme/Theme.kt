package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp

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

private val LightColorScheme = lightColorScheme(
    primary = SunsetOrange,
    onPrimary = Color.White,
    primaryContainer = SunsetOrangeVariant,
    onPrimaryContainer = Color.White,
    secondary = ElectricPurple,
    onSecondary = Color.White,
    secondaryContainer = ElectricViolet,
    onSecondaryContainer = Color.White,
    tertiary = VipGoldDark,
    onTertiary = Color.White,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextSecondary,
    outline = LightSurfaceBorder,
    error = ErrorRed,
    onError = Color.White
)

fun getScaledTypography(fontScale: Float = 1.0f): androidx.compose.material3.Typography {
    return androidx.compose.material3.Typography(
        bodyLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (16 * fontScale).sp,
            lineHeight = (24 * fontScale).sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (14 * fontScale).sp,
            lineHeight = (20 * fontScale).sp
        ),
        bodySmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (12 * fontScale).sp,
            lineHeight = (16 * fontScale).sp
        ),
        titleLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = (22 * fontScale).sp,
            lineHeight = (28 * fontScale).sp
        ),
        titleMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = (18 * fontScale).sp,
            lineHeight = (24 * fontScale).sp
        ),
        titleSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = (15 * fontScale).sp,
            lineHeight = (20 * fontScale).sp
        ),
        labelSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (11 * fontScale).sp,
            lineHeight = (14 * fontScale).sp
        )
    )
}

@Composable
fun ReelsStudioTheme(
    isDarkMode: Boolean = true,
    fontScale: Float = 1.0f,
    content: @Composable () -> Unit
) {
    val colors = if (isDarkMode) DarkColorScheme else LightColorScheme
    val originalDensity = LocalDensity.current
    val customDensity = Density(
        density = originalDensity.density,
        fontScale = originalDensity.fontScale * fontScale
    )

    CompositionLocalProvider(LocalDensity provides customDensity) {
        MaterialTheme(
            colorScheme = colors,
            typography = getScaledTypography(fontScale),
            content = content
        )
    }
}

