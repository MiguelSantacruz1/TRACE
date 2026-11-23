package com.example.trace.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private fun traceColorScheme(): ColorScheme = if (ThemeMode.dark) {
    darkColorScheme(
        primary = NeonLime,
        onPrimary = Void,
        primaryContainer = Color(0xFF1E3A12),
        onPrimaryContainer = NeonLime,
        secondary = NeonCyan,
        onSecondary = Void,
        secondaryContainer = Color(0xFF0B3340),
        onSecondaryContainer = NeonCyan,
        tertiary = NeonViolet,
        onTertiary = Void,
        background = Void,
        onBackground = TextHi,
        surface = Ink,
        onSurface = TextHi,
        surfaceVariant = Panel,
        onSurfaceVariant = TextMid,
        outline = TextLow,
        outlineVariant = Hairline,
        error = NeonRose
    )
} else {
    lightColorScheme(
        primary = NeonLime,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFDDF3C4),
        onPrimaryContainer = NeonLime,
        secondary = NeonCyan,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFD6EEF7),
        onSecondaryContainer = NeonCyan,
        tertiary = NeonViolet,
        onTertiary = Color.White,
        background = Void,
        onBackground = TextHi,
        surface = Ink,
        onSurface = TextHi,
        surfaceVariant = Panel,
        onSurfaceVariant = TextMid,
        outline = TextLow,
        outlineVariant = Hairline,
        error = NeonRose
    )
}

/** Tema oscuro o claro según [ThemeMode]; sin color dinámico, para mantener la identidad TRACE. */
@Composable
fun TRACETheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = traceColorScheme(),
        typography = TraceTypography
    ) {
        CompositionLocalProvider(LocalContentColor provides TextHi, content = content)
    }
}
