package com.ejemplo.trace

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase Application principal de TRACE anotada con @HiltAndroidApp
 * para iniciar la inyección de dependencias a nivel de aplicación.
 */
@HiltAndroidApp
class TraceApp : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}
