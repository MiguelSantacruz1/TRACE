package com.ejemplo.trace.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.ejemplo.trace.presentation.navigation.TraceNavHost
import com.ejemplo.trace.presentation.theme.TRACETheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TRACETheme {
                val navController = rememberNavController()
                TraceNavHost(navController = navController)
            }
        }
    }
}
