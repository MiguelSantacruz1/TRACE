package com.example.trace.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Scanner : Screen("scanner")
    data object ProductResult : Screen("product_result/{productId}") {
        fun createRoute(productId: String) = "product_result/$productId"
    }
    data object Investigation : Screen("investigation/{productId}") {
        fun createRoute(productId: String) = "investigation/$productId"
    }
    data object TraceAi : Screen("trace-ai/{productId}") {
        fun createRoute(productId: String) = "trace-ai/$productId"
    }
}

@Composable
fun TRACEApp(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                onScanClick = {
                    navController.navigate(Screen.Scanner.route)
                },
                onProductClick = { product ->
                    navController.navigate(Screen.ProductResult.createRoute(product.id))
                }
            )
        }

        // Scanner Screen
        composable(Screen.Scanner.route) {
            ScannerScreen(
                onScanSuccess = { productId ->
                    navController.navigate(Screen.ProductResult.createRoute(productId)) {
                        popUpTo(Screen.Scanner.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Product Result Screen
        composable(
            route = Screen.ProductResult.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: "1"
            ProductResultScreen(
                productId = productId,
                onViewInvestigation = {
                    navController.navigate(Screen.Investigation.createRoute(productId))
                },
                onAskAiClick = {
                    navController.navigate(Screen.TraceAi.createRoute(productId))
                },
                onScanAnother = {
                    navController.navigate(Screen.Scanner.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onGoHome = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
            )
        }

        // Investigation Screen
        composable(
            route = Screen.Investigation.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: "1"
            InvestigationScreen(
                productId = productId,
                onAskAiClick = {
                    navController.navigate(Screen.TraceAi.createRoute(productId))
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onGoHome = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
            )
        }

        // Trace AI Screen
        composable(
            route = Screen.TraceAi.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: "1"
            TraceAiScreen(
                productId = productId,
                onBackToInvestigation = {
                    navController.popBackStack()
                }
            )
        }
    }
}
