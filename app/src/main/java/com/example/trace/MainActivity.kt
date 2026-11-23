package com.example.trace

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.trace.model.ProductStore
import com.example.trace.ui.TRACEApp
import com.example.trace.ui.components.AuroraBackground
import com.example.trace.ui.theme.TRACETheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProductStore.init(this)
        // App oscura: íconos claros en las barras del sistema, transparentes sobre el fondo.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            TRACETheme {
                AuroraBackground {
                    TRACEApp()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainAppPreview() {
    TRACETheme {
        AuroraBackground {
            TRACEApp()
        }
    }
}
