package com.ejemplo.trace.presentation.navigation

/**
 * Destinos y rutas de navegación de TRACE.
 */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Scanner : Screen("scanner")
    data object Result : Screen("result/{barcode}") {
        fun createRoute(barcode: String) = "result/$barcode"
    }
    data object Investigation : Screen("investigation/{barcode}") {
        fun createRoute(barcode: String) = "investigation/$barcode"
    }
    data object History : Screen("history")
    data object Compare : Screen("compare")
    data object Chat : Screen("chat/{barcode}") {
        fun createRoute(barcode: String) = "chat/$barcode"
    }
}
