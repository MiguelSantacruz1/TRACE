package com.example.trace.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trace.model.sampleProducts
import com.example.trace.ui.theme.HighScoreGreen
import com.example.trace.ui.theme.LowScoreRed
import com.example.trace.ui.theme.MediumScoreAmber
import com.example.trace.ui.theme.TRACETheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductResultScreen(
    productId: String,
    onViewInvestigation: () -> Unit,
    onAskAiClick: () -> Unit,
    onScanAnother: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val product = sampleProducts.find { it.id == productId } ?: sampleProducts.first()

    val scoreColor = when {
        product.score >= 80 -> HighScoreGreen
        product.score >= 65 -> MediumScoreAmber
        else -> LowScoreRed
    }

    val scoreRatingText = when {
        product.score >= 80 -> "Sostenibilidad Muy Alta"
        product.score >= 65 -> "Sostenibilidad Aceptable"
        else -> "Atención: Sostenibilidad Baja"
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Producto detectado",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onGoHome) {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Product Header & Enhanced TRACE SCORE Banner Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Category Chip
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = product.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Product Name
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Enhanced Large Score Circle Badge
                        Surface(
                            shape = CircleShape,
                            color = scoreColor.copy(alpha = 0.12f),
                            modifier = Modifier.size(115.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "TRACE SCORE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = scoreColor,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "${product.score}",
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = scoreColor
                                )
                                Text(
                                    text = "de 100",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = scoreColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Descriptive Rating Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = scoreColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = scoreRatingText,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = scoreColor,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Score Progress Bar
                        LinearProgressIndicator(
                            progress = { product.score / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = scoreColor,
                            trackColor = scoreColor.copy(alpha = 0.15f)
                        )
                    }
                }
            }

            // Environmental & Impact Metrics Section
            item {
                Text(
                    text = "Métricas de Sostenibilidad",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 1. Impacto Ambiental
            item {
                MetricItemCard(
                    icon = "🌍",
                    title = "Impacto Ambiental",
                    value = product.impactTag,
                    description = "Medición de huella de carbono y emisiones durante la producción."
                )
            }

            // 2. Reparabilidad
            item {
                MetricItemCard(
                    icon = "🛠️",
                    title = "Reparabilidad",
                    value = product.repairability,
                    description = "Índice de capacidad para reparar y sustituir componentes."
                )
            }

            // 3. Reciclabilidad
            item {
                MetricItemCard(
                    icon = "♻️",
                    title = "Reciclabilidad",
                    value = product.recyclability,
                    description = "Proporción de materiales reciclables o biodegradables."
                )
            }

            // 4. Transporte
            item {
                MetricItemCard(
                    icon = "🚛",
                    title = "Transporte & Logística",
                    value = product.transport ?: "Información no disponible",
                    description = "Evaluación del método de envío e impacto logístico."
                )
            }

            // Action Buttons
            item {
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Button: "Ver investigación"
                    Button(
                        onClick = onViewInvestigation,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "🔍 Ver investigación",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Button: "Preguntar a TRACE AI"
                    Button(
                        onClick = onAskAiClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(
                            text = "🤖 Preguntar a TRACE AI",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Button: "Escanear otro producto"
                    OutlinedButton(
                        onClick = onScanAnother,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "📷 Escanear otro producto",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Button: "Volver al inicio"
                    OutlinedButton(
                        onClick = onGoHome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Volver al inicio",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricItemCard(
    icon: String,
    title: String,
    value: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = icon,
                fontSize = 28.sp,
                modifier = Modifier.padding(end = 14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductResultScreenPreview() {
    TRACETheme {
        ProductResultScreen(
            productId = "1",
            onViewInvestigation = {},
            onAskAiClick = {},
            onScanAnother = {},
            onGoHome = {}
        )
    }
}
