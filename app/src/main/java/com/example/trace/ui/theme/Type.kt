package com.example.trace.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Sans pesada para titulares, monoespaciada para etiquetas "de terminal". */
val TraceMono = FontFamily.Monospace
private val Sans = FontFamily.SansSerif

val TraceTypography = Typography(
    displayMedium = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Black,
        fontSize = 48.sp, lineHeight = 52.sp, letterSpacing = 4.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Black,
        fontSize = 28.sp, lineHeight = 34.sp, letterSpacing = (-0.3).sp
    ),
    titleLarge = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp, lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Bold,
        fontSize = 17.sp, lineHeight = 22.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Bold,
        fontSize = 14.sp, lineHeight = 18.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Normal,
