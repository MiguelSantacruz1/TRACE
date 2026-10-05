package com.example.trace.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trace.model.sampleProducts
import com.example.trace.ui.theme.TRACETheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ScanState {
    IDLE, ANALYZING, IDENTIFIED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    onScanSuccess: (productId: String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var scanState by remember { mutableStateOf(ScanState.IDLE) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Escanear producto",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text(
                            text = "←",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF121413)) // Dark camera viewfinder background
        ) {
            val scanAreaSize = (maxWidth * 0.75f).coerceAtMost(300.dp)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header badge inside camera view
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "📷 Cámara simulada (Demostración)",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                // Central Camera Simulation Area with Viewfinder Frame
                Box(
                    modifier = Modifier
                        .size(scanAreaSize)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF1E2320))
                        .border(
                            width = 3.dp,
                            color = when (scanState) {
                                ScanState.IDENTIFIED -> Color(0xFF2E7D32)
                                ScanState.ANALYZING -> Color(0xFFE65100)
                                ScanState.IDLE -> MaterialTheme.colorScheme.primary
                            },
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Animated Laser Scanning Line
                    val infiniteTransition = rememberInfiniteTransition(label = "laser")
                    val laserYPercentage by infiniteTransition.animateFloat(
                        initialValue = 0.1f,
                        targetValue = 0.9f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = if (scanState == ScanState.ANALYZING) 800 else 1800,
                                easing = FastOutSlowInEasing
                            ),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "laserPosition"
                    )

                    // Simulated Barcode Pattern in background
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "█ ▌ █ █ ▌ █ ▌ █ █",
                            color = Color.White.copy(alpha = 0.35f),
                            fontSize = 32.sp,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "|||||||||||||||||||||||||||",
                            color = Color.White.copy(alpha = 0.35f),
                            fontSize = 20.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    // Laser Line Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(4.dp)
                            .align(Alignment.TopCenter)
                            .padding(top = (scanAreaSize.value * laserYPercentage).dp)
                            .background(
                                color = when (scanState) {
                                    ScanState.IDENTIFIED -> Color(0xFF4CAF50)
                                    ScanState.ANALYZING -> Color(0xFFFF9800)
                                    ScanState.IDLE -> Color(0xFFFF5252)
                                },
                                shape = CircleShape
                            )
                    )
                }

                // Status & Instruction Text
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedVisibility(
                        visible = scanState == ScanState.ANALYZING,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFFFF9800),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Analizando producto...",
                                color = Color(0xFFFF9800),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = scanState == ScanState.IDENTIFIED,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "✅ Producto identificado",
                                color = Color(0xFF81C784),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = scanState == ScanState.IDLE,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Apunta al código de barras del producto",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "El análisis de sostenibilidad se generará automáticamente",
                                color = Color.White.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Bottom Buttons: "Escanear producto" & "Volver a inicio"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Simulated Scan Button
                    Button(
                        onClick = {
                            if (scanState == ScanState.IDLE) {
                                coroutineScope.launch {
                                    scanState = ScanState.ANALYZING
                                    delay(1300) // "Analizando producto..."
                                    scanState = ScanState.IDENTIFIED
                                    delay(800)  // "Producto identificado"
                                    val scannedProduct = sampleProducts.first()
                                    onScanSuccess(scannedProduct.id)
                                }
                            }
                        },
                        enabled = scanState == ScanState.IDLE,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = when (scanState) {
                                ScanState.ANALYZING -> "Analizando..."
                                ScanState.IDENTIFIED -> "¡Identificado!"
                                ScanState.IDLE -> "📷 Escanear producto"
                            },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Return Button
                    OutlinedButton(
                        onClick = onBackClick,
                        enabled = scanState == ScanState.IDLE,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Volver al inicio",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScannerScreenPreview() {
    TRACETheme {
        ScannerScreen(
            onScanSuccess = {},
            onBackClick = {}
        )
    }
}
