package com.ejemplo.trace.presentation.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pruebas unitarias de rutas y destinos de navegación.
 */
class NavigationTest {

    @Test
    fun verifyHomeRoute_isCorrect() {
        assertEquals("home", Screen.Home.route)
    }

    @Test
    fun verifyScannerRoute_isCorrect() {
        assertEquals("scanner", Screen.Scanner.route)
    }

    @Test
    fun verifyResultRoute_buildsCorrectPath() {
        val testBarcode = "3017620422003"
        val expected = "result/3017620422003"
        assertEquals(expected, Screen.Result.createRoute(testBarcode))
    }

    @Test
    fun verifyInvestigationRoute_buildsCorrectPath() {
        val testBarcode = "5449000000996"
        val expected = "investigation/5449000000996"
        assertEquals(expected, Screen.Investigation.createRoute(testBarcode))
    }

    @Test
    fun verifyChatRoute_buildsCorrectPath() {
        val testBarcode = "7701234567890"
        val expected = "chat/7701234567890"
        assertEquals(expected, Screen.Chat.createRoute(testBarcode))
    }

    @Test
    fun verifyHistoryAndCompareRoutes_areCorrect() {
        assertEquals("history", Screen.History.route)
        assertEquals("compare", Screen.Compare.route)
    }
}
