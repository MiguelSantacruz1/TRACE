package com.example.trace.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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

