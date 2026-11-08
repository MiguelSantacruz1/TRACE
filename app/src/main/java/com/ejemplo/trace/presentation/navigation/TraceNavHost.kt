package com.ejemplo.trace.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ejemplo.trace.presentation.chat.ChatScreen
import com.ejemplo.trace.presentation.compare.CompareScreen
import com.ejemplo.trace.presentation.history.HistoryScreen
import com.ejemplo.trace.presentation.home.HomeScreen
import com.ejemplo.trace.presentation.investigation.InvestigationScreen
import com.ejemplo.trace.presentation.result.ProductResultScreen
import com.ejemplo.trace.presentation.scanner.ScannerScreen

/**
 * Grafo central de navegación de TRACE.
 * Conecta las 6 pantallas principales: Home, Scanner, ProductResult, Investigation, Chat y Compare.
 * Todas las rutas están interconectadas y los botones realizan transiciones reales.
 */
@Composable
fun TraceNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // 1. Home
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToScanner = {
                    navController.navigate(Screen.Scanner.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToCompare = {
                    navController.navigate(Screen.Compare.route)
                }
            )
        }

        // 2. Scanner
        composable(Screen.Scanner.route) {
            ScannerScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onBarcodeDetected = { barcode ->
                    navController.navigate(Screen.Result.createRoute(barcode))
                }
            )
        }

        // 3. Product Result
        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("barcode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode") ?: "0000000000000"
            ProductResultScreen(
                barcode = barcode,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToInvestigation = { code ->
                    navController.navigate(Screen.Investigation.createRoute(code))
                },
                onNavigateToChat = { code ->
                    navController.navigate(Screen.Chat.createRoute(code))
                }
            )
        }

        // 4. Investigation (Detalle profundo)
        composable(
            route = Screen.Investigation.route,
            arguments = listOf(
                navArgument("barcode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode") ?: "0000000000000"
            InvestigationScreen(
                barcode = barcode,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 5. TRACE AI (Chat)
        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("barcode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode") ?: "0000000000000"
            ChatScreen(
                barcode = barcode,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 6. Historial de Consultas
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSelectItem = { barcode ->
                    navController.navigate(Screen.Result.createRoute(barcode))
                }
            )
        }

        // 7. Comparador de Productos
        composable(Screen.Compare.route) {
            CompareScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
