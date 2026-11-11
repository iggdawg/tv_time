package com.example.tvtime.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TvTimeYellow,
    onPrimary = TvTimeOnYellow,
    primaryContainer = TvTimeYellowContainer,
    onPrimaryContainer = TvTimeYellow,
    secondary = TvTimeReturningBlue,
    onSecondary = Color(0xFF00354E),
    tertiary = TvTimeWatchedGreen,
    onTertiary = Color(0xFF003919),
    background = TvTimeBackground,
    onBackground = TvTimeTextPrimary,
    surface = TvTimeSurface,
    onSurface = TvTimeTextPrimary,
    surfaceVariant = TvTimeSurfaceVariant,
    onSurfaceVariant = TvTimeTextSecondary,
    outline = TvTimeBorder,
)

@Composable
fun TVTimeTheme(
    darkTheme: Boolean = true, // TV Time is famous for its dark UI
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
