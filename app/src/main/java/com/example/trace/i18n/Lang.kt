package com.example.trace.i18n

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Idioma de la app (español o inglés). Es estado de Compose: al cambiarlo se redibuja todo. */
object Lang {
    var en by mutableStateOf(false)
        private set

    private const val PREFS = "trace_lang"
    private const val KEY = "en"
    private var context: Context? = null

    fun init(ctx: Context) {
        if (context != null) return
        context = ctx.applicationContext
        val prefs = ctx.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        en = if (prefs.contains(KEY)) prefs.getBoolean(KEY, false)
        else java.util.Locale.getDefault().language == "en"
    }

    fun toggle() {
        en = !en
        context?.getSharedPreferences(PREFS, Context.MODE_PRIVATE)?.edit()?.putBoolean(KEY, en)?.apply()
    }
}

/** Devuelve el texto en el idioma activo. */
fun tr(es: String, en: String): String = if (Lang.en) en else es
