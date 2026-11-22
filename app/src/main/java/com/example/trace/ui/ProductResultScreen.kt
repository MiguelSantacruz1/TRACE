package com.example.trace.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trace.model.productById
import com.example.trace.ui.components.AuroraBackground
import com.example.trace.ui.components.GlassCard
import com.example.trace.ui.components.NeonButton
import com.example.trace.ui.components.NeonChip
import com.example.trace.ui.components.NeonStyle
import com.example.trace.ui.components.ScoreGauge
import com.example.trace.ui.components.SectionLabel
import com.example.trace.ui.components.TraceScaffold
import com.example.trace.ui.components.scoreColor
import com.example.trace.ui.components.scoreRating
import com.example.trace.ui.components.staggerIn
import com.example.trace.ui.theme.NeonCyan
import com.example.trace.ui.theme.NeonLime
import com.example.trace.ui.theme.NeonMint
import com.example.trace.ui.theme.NeonViolet
import com.example.trace.ui.theme.TRACETheme
import com.example.trace.ui.theme.TextHi
import com.example.trace.ui.theme.TextMid

@Composable
fun ProductResultScreen(
    productId: String,
    onViewInvestigation: () -> Unit,
    onAskAiClick: () -> Unit,
    onScanAnother: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val product = productById(productId)
    val color = scoreColor(product.score)

    TraceScaffold(
        modifier = modifier,
        title = "Producto detectado",
        onBack = onGoHome
    ) {
        // Tarjeta héroe con el score
        GlassCard(
            modifier = Modifier.fillMaxWidth().staggerIn(0),
            accent = color,
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NeonChip(product.category.uppercase(), NeonCyan)
                Spacer(Modifier.height(10.dp))
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextHi,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "TRACE SCORE",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMid
                )
                Spacer(Modifier.height(14.dp))
                ScoreGauge(score = product.score, size = 200.dp, stroke = 15.dp)
                Spacer(Modifier.height(8.dp))
                NeonChip(scoreRating(product.score), color)
            }
        }

        if (product.aiGenerated) {
            val uriHandler = LocalUriHandler.current
            GlassCard(
                modifier = Modifier.fillMaxWidth().staggerIn(1),
                accent = NeonViolet,
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(Modifier.fillMaxWidth().padding(18.dp)) {
                    SectionLabel("ANÁLISIS DE LA IA")
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = product.summary ?: "La IA identificó el producto pero no dejó un resumen.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextHi
                    )
                    if (product.sources.isNotEmpty()) {
                        Spacer(Modifier.height(14.dp))
                        Text("FUENTES WEB", style = MaterialTheme.typography.labelSmall, color = TextMid)
                        Spacer(Modifier.height(6.dp))
                        product.sources.forEach { src ->
                            Text(
                                text = "↗ ${src.title}",
                                style = MaterialTheme.typography.bodySmall,
                                color = NeonCyan,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { runCatching { uriHandler.openUri(src.url) } }
                                    .padding(vertical = 5.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Calificación estimada por IA a partir de información pública. Puede contener errores.",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMid
                    )
                }
            }
        }

        SectionLabel("MÉTRICAS DE SOSTENIBILIDAD", Modifier.staggerIn(1))

        MetricItemCard(
            icon = "🌍",
            title = "Impacto ambiental",
            value = product.impactTag,
            description = "Huella de carbono y emisiones durante la producción.",
            accent = NeonMint,
            modifier = Modifier.staggerIn(2)
        )
        MetricItemCard(
            icon = "🛠️",
            title = "Reparabilidad",
