package com.ejemplo.trace.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ejemplo.trace.presentation.navigation.TraceNavHost
import com.ejemplo.trace.presentation.theme.TRACETheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Prueba instrumentada de navegación e interfaz Compose (Etapa 9).
 */
@RunWith(AndroidJUnit4::class)
class NavigationFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysMainActionsAndNavigatesToHistory() {
        composeTestRule.setContent {
            TRACETheme {
                val navController = rememberNavController()
                TraceNavHost(navController = navController)
            }
        }

        // Verificar elementos clave de la pantalla principal
        composeTestRule.onNodeWithText("Escanear Producto").assertIsDisplayed()
        composeTestRule.onNodeWithText("Historial").assertIsDisplayed()

        // Interactuar hacia la pantalla de historial
        composeTestRule.onNodeWithText("Historial").performClick()

        // Verificar que estamos en la pantalla de historial
        composeTestRule.onNodeWithText("Historial de Consultas").assertIsDisplayed()
    }
}
