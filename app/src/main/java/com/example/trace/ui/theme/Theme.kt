package com.example.trace.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val TraceColorScheme = darkColorScheme(
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

