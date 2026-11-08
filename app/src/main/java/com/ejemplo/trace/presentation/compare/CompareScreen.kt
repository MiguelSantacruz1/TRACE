package com.ejemplo.trace.presentation.compare

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ejemplo.trace.R
import com.ejemplo.trace.domain.model.ComparisonAdvantage
import com.ejemplo.trace.domain.model.FactorComparisonItem
import com.ejemplo.trace.domain.model.HistoryProduct
import com.ejemplo.trace.domain.model.ProductComparison
import com.ejemplo.trace.presentation.common.EmptyState
import com.ejemplo.trace.presentation.common.ErrorState
import com.ejemplo.trace.presentation.common.LoadingState
import com.ejemplo.trace.presentation.common.ScoreBadge

/**
 * Pantalla de Comparación de Productos investigados.
 * Cumple con la regla ética y metodológica (Prompt Maestro - Sección 10):
 * si no hay datos suficientes o la cobertura de factores no es comparable,
 * muestra la comparativa analítica sin emitir un veredicto subjetivo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CompareViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.home_btn_compare),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is CompareUiState.Loading -> {
                LoadingState(
                    message = "Preparando comparativa de sostenibilidad...",
                    modifier = Modifier.padding(innerPadding)
                )
            }
            is CompareUiState.EmptyHistory -> {
                EmptyState(
                    title = "Requiere al menos 2 productos",
                    message = "Para comparar el impacto y trazabilidad, escanea al menos dos productos en la aplicación.",
                    icon = Icons.AutoMirrored.Filled.CompareArrows,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            is CompareUiState.Error -> {
                ErrorState(
                    message = state.message,
                    onRetry = { },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            is CompareUiState.Ready -> {
                CompareContent(
                    comparison = state.comparison,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun CompareContent(
    comparison: ProductComparison,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Comparativa de Sostenibilidad",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Análisis lado a lado basado en evidencia verificada y cálculo determinista.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Tarjetas lado a lado de los 2 productos comparados
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProductCompareCard(
                product = comparison.productA,
                modifier = Modifier.weight(1f)
            )

            ProductCompareCard(
                product = comparison.productB,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tabla detallada por dimensiones
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Comparación por Dimensiones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))

                comparison.factorComparisons.forEach { factor ->
                    DimensionComparisonRow(factor = factor)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Aviso de Comparación Responsable (Regla de la Sección 10)
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (comparison.isComparable) {
                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
                } else {
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                }
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Balance,
                    contentDescription = null,
                    tint = if (comparison.isComparable) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = comparison.analyticalVerdict,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (comparison.isComparable) {
                        MaterialTheme.colorScheme.onTertiaryContainer
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProductCompareCard(
    product: HistoryProduct,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            Text(
                text = product.brand.ifBlank { "Marca sin verificar" },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            ScoreBadge(score = product.lastScore, size = 70.dp)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Código: ${product.barcode}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DimensionComparisonRow(
    factor: FactorComparisonItem
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = factor.factorName,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "• ${factor.detailsA}",
                style = MaterialTheme.typography.bodySmall,
                color = if (factor.advantage == ComparisonAdvantage.PRODUCT_A) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "• ${factor.detailsB}",
                style = MaterialTheme.typography.bodySmall,
                color = if (factor.advantage == ComparisonAdvantage.PRODUCT_B) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
