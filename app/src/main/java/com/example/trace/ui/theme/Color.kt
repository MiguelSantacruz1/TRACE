package com.example.trace.ui.theme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/** Modo de la app (oscuro o claro). Es estado de Compose: al cambiarlo se redibuja todo. */
object ThemeMode {
    var dark by mutableStateOf(true)
        private set

    private const val PREFS = "trace_theme"
    private const val KEY = "dark"
    private var context: Context? = null

    fun init(ctx: Context) {
        if (context != null) return
        context = ctx.applicationContext
        dark = ctx.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY, true)
    }

    fun toggle() {
        dark = !dark
        context?.getSharedPreferences(PREFS, Context.MODE_PRIVATE)?.edit()?.putBoolean(KEY, dark)?.apply()
    }
}

private fun pick(dark: Color, light: Color): Color = if (ThemeMode.dark) dark else light

// TRACE: paleta neón sobre vacío (oscuro) y su versión sobre blanco (claro)
val Void: Color get() = pick(Color(0xFF04070A), Color(0xFFF4F8F6))   // fondo base
val Ink: Color get() = pick(Color(0xFF0A1311), Color(0xFFFFFFFF))    // superficies
val Panel: Color get() = pick(Color(0xFF111F1A), Color(0xFFE8F0EC))  // superficies elevadas

val NeonLime: Color get() = pick(Color(0xFFC6FF3D), Color(0xFF4D8F00))
val NeonMint: Color get() = pick(Color(0xFF19FFA8), Color(0xFF008F5A))
val NeonCyan: Color get() = pick(Color(0xFF2DE2FF), Color(0xFF0A7EA4))
val NeonViolet: Color get() = pick(Color(0xFFB66DFF), Color(0xFF7A35D6))
val NeonAmber: Color get() = pick(Color(0xFFFFB527), Color(0xFFC26A00))
val NeonRose: Color get() = pick(Color(0xFFFF3D77), Color(0xFFD3194F))

val TextHi: Color get() = pick(Color(0xFFF1FFF6), Color(0xFF0B1A14))
val TextMid: Color get() = pick(Color(0xFFA7BDB1), Color(0xFF475A51))
val TextLow: Color get() = pick(Color(0xFF64796F), Color(0xFF7C8D85))
val Hairline: Color get() = pick(Color(0xFF22332C), Color(0xFFD3DFD9))

/** Color neutro para velos y relieves: blanco sobre oscuro, negro sobre claro. */
val Tint: Color get() = pick(Color.White, Color.Black)

// Indicadores de score
val HighScoreGreen: Color get() = NeonMint
val MediumScoreAmber: Color get() = NeonAmber
val LowScoreRed: Color get() = NeonRose
