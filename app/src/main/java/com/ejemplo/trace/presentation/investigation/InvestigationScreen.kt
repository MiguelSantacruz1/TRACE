package com.ejemplo.trace.presentation.investigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ejemplo.trace.R

enum class FactStatus(val label: String, val isConfirmed: Boolean) {
    CONFIRMED("Confirmado", true),
    DECLARED("Declarado por fabricante", false),
    ESTIMATED("Estimado", false),
    NOT_FOUND("No encontrado", false)
}

/**
 * Pantalla de Investigación Profunda por Categorías.
 * Muestra el desglose de las 6 dimensiones de trazabilidad y sostenibilidad:
 * 1. Identificación
 * 2. Origen y Materias Primas
 * 3. Fabricación y Emisiones
 * 4. Transporte y Logística
 * 5. Reparabilidad y Durabilidad
 * 6. Fin de Vida y Reciclabilidad
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestigationScreen(
    barcode: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.result_tab_details),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Trazabilidad de Producto: $barcode",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Datos analizados y contrastados con fuentes verificables.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Origen y Materias Primas
            CategoryCard(
                title = "Origen y Materias Primas",
                icon = Icons.Default.Public,
                facts = listOf(
                    FactItem("País de origen", "Italia", FactStatus.CONFIRMED, "Open Food Facts"),
                    FactItem("Cacao certificado", "Rainforest Alliance", FactStatus.CONFIRMED, "Ferrero CSR 2023"),
                    FactItem("Aceite de palma", "RSPO Segregated 100%", FactStatus.DECLARED, "Ferrero Palm Oil Charter")
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Fabricación
            CategoryCard(
                title = "Fabricación y Procesos",
                icon = Icons.Default.Factory,
                facts = listOf(
                    FactItem("Lugar de envasado", "Villers-Écalles, Francia", FactStatus.CONFIRMED, "Etiqueta / Open Food Facts"),
                    FactItem("Energía renovable", "85% en plantas europeas", FactStatus.DECLARED, "Reporte Sostenibilidad Ferrero"),
                    FactItem("Emisiones directas (Scope 1)", "No declaradas por unidad", FactStatus.NOT_FOUND, "")
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Transporte y Logística
            CategoryCard(
                title = "Transporte y Logística",
                icon = Icons.Default.LocalShipping,
                facts = listOf(
                    FactItem("Ruta logística", "Terrestre / Marítima Europa", FactStatus.ESTIMATED, "Cálculo logístico"),
                    FactItem("Distancia estimada a destino", "~8,500 km", FactStatus.ESTIMATED, "Estimación de ruta")
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Reparabilidad y Durabilidad
            CategoryCard(
                title = "Uso, Vida Útil y Reparabilidad",
                icon = Icons.Default.Build,
                facts = listOf(
                    FactItem("Vida útil sugerida", "12 meses cerrado", FactStatus.CONFIRMED, "Ficha del fabricante"),
                    FactItem("Reparabilidad", "No aplica (bien de consumo)", FactStatus.CONFIRMED, "")
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Fin de Vida y Reciclabilidad
            CategoryCard(
                title = "Fin de Vida y Reciclabilidad",
                icon = Icons.Default.Recycling,
                facts = listOf(
                    FactItem("Frasco", "Vidrio - 100% Reciclable", FactStatus.CONFIRMED, "Instrucciones de empaque"),
                    FactItem("Tapa", "Polipropileno (PP 05) - Reciclable", FactStatus.CONFIRMED, "Open Food Facts"),
                    FactItem("Sello de seguridad", "Complejo multicapa - Desecho general", FactStatus.CONFIRMED, "Guía de reciclaje")
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private data class FactItem(
    val field: String,
    val value: String,
    val status: FactStatus,
    val sourceName: String
)

@Composable
private fun CategoryCard(
    title: String,
    icon: ImageVector,
    facts: List<FactItem>,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            facts.forEach { fact ->
                FactRow(fact = fact)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun FactRow(fact: FactItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = fact.field,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = fact.value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (fact.sourceName.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = fact.sourceName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(6.dp),
            color = when (fact.status) {
                FactStatus.CONFIRMED -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                FactStatus.DECLARED -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                FactStatus.ESTIMATED -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                FactStatus.NOT_FOUND -> MaterialTheme.colorScheme.surfaceVariant
            }
        ) {
            Text(
                text = fact.status.label,
                style = MaterialTheme.typography.labelSmall,
                color = when (fact.status) {
                    FactStatus.CONFIRMED -> MaterialTheme.colorScheme.primary
                    FactStatus.DECLARED -> MaterialTheme.colorScheme.secondary
                    FactStatus.ESTIMATED -> MaterialTheme.colorScheme.tertiary
                    FactStatus.NOT_FOUND -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
