package com.example.trace

import com.example.trace.ui.theme.ThemeMode
import androidx.compose.runtime.SideEffect
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
        ThemeMode.init(this)
        setContent {
            // Íconos de las barras del sistema acordes al modo, transparentes sobre el fondo.
            val dark = ThemeMode.dark
            SideEffect {
                val style = if (dark) SystemBarStyle.dark(Color.TRANSPARENT)
                else SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
            }
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
