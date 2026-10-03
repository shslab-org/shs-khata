package com.shslab.shskhata.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary        = Green700,
    onPrimary      = Color.White,
    primaryContainer     = Green100,
    onPrimaryContainer   = Green700,
    secondary      = Blue600,
    onSecondary      = Color.White,
    secondaryContainer = Blue50,
    onSecondaryContainer = Blue700,
    background     = BackgroundLight,
    onBackground   = OnBackgroundLight,
    surface        = SurfaceLight,
    onSurface      = OnSurfaceLight,
    error          = Red600,
    onError        = Color.White,
    errorContainer = Red50,
    onErrorContainer = Red700
)

private val DarkColors = darkColorScheme(
    primary        = Green400,
    onPrimary      = Color(0xFF1A1A1A),
    primaryContainer     = Green50Dark,
    onPrimaryContainer   = Green400,
    secondary      = Blue400,
    onSecondary      = Color(0xFF0A1A2E),
    secondaryContainer = Blue50.copy(alpha = 0.15f),
    onSecondaryContainer = Blue400,
    background     = BackgroundDark,
    onBackground   = OnBackgroundDark,
    surface        = SurfaceDark,
    onSurface      = OnSurfaceDark,
    error          = Red400,
    onError        = Color(0xFF3A0A0A),
    errorContainer = Red50Dark,
    onErrorContainer = Red400
)

@Composable
fun SHSKhataTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}
