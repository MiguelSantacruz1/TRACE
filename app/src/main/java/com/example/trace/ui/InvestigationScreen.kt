package com.example.trace.ui

import com.example.trace.i18n.tr
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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
import com.example.trace.ui.theme.TextLow
import com.example.trace.ui.theme.TextMid

@Composable
fun InvestigationScreen(
    productId: String,
    onAskAiClick: () -> Unit,
    onBackClick: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val product = productById(productId)
    val color = scoreColor(product.score)

    TraceScaffold(
        modifier = modifier,
        title = tr("Investigación", "Investigation"),
        onBack = onBackClick,
        spacing = 18.dp
    ) {
        // Encabezado del producto
        Column(Modifier.fillMaxWidth().staggerIn(0)) {
            NeonChip(product.category.uppercase(), NeonCyan)
            Spacer(Modifier.height(8.dp))
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineMedium,
                color = TextHi
            )
        }

        // Score
        GlassCard(
            modifier = Modifier.fillMaxWidth().staggerIn(1),
            accent = color,
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScoreGauge(score = product.score, size = 104.dp, stroke = 10.dp, showCaption = false)
                Spacer(Modifier.width(18.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "TRACE SCORE",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMid
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = tr("Puntuación de sostenibilidad", "Sustainability score"),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextHi
                    )
                    Spacer(Modifier.height(10.dp))
                    NeonChip(scoreRating(product.score), color)
                }
            }
        }

        SectionLabel(tr("PILARES DE SOSTENIBILIDAD", "SUSTAINABILITY PILLARS"), Modifier.staggerIn(2))

        Column(
            modifier = Modifier.fillMaxWidth().staggerIn(3),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PillarTile(tr("Impacto", "Impact"), product.impactTag, NeonMint, Modifier.weight(1f).fillMaxHeight())
                PillarTile(tr("Transporte", "Transport"), product.transport ?: tr("No disponible", "Not available"), NeonViolet, Modifier.weight(1f).fillMaxHeight())
            }
            Row(
                modifier = Modifier.height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PillarTile(tr("Reparabilidad", "Repairability"), product.repairability, NeonCyan, Modifier.weight(1f).fillMaxHeight())
                PillarTile(tr("Reciclabilidad", "Recyclability"), product.recyclability, NeonLime, Modifier.weight(1f).fillMaxHeight())
            }
        }

        // Línea de tiempo
        val stages = listOf(
            TimelineStage(tr("Materia prima", "Raw material"), product.rawMaterial),
            TimelineStage(tr("Fabricación", "Manufacturing"), product.manufacturing),
            TimelineStage(tr("País de origen", "Country of origin"), product.countryOfOrigin),
            TimelineStage(tr("Transporte", "Transport"), product.transport),
            TimelineStage(tr("País de destino", "Destination country"), product.destinationCountry),
            TimelineStage(tr("Tienda", "Store"), product.store),
            TimelineStage(tr("Consumidor", "Consumer"), product.consumerStage)
        )
        val verified = stages.count { !it.value.isNullOrBlank() }

        SectionLabel(
            text = tr("RECORRIDO DEL PRODUCTO", "PRODUCT JOURNEY"),
            trailing = tr("$verified/${stages.size} VERIFICADAS", "$verified/${stages.size} VERIFIED"),
            modifier = Modifier.staggerIn(4)
        )

        GlassCard(
            modifier = Modifier.fillMaxWidth().staggerIn(5),
            accent = NeonMint,
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 22.dp)
            ) {
                stages.forEachIndexed { index, stage ->
                    TimelineItemRow(stage = stage, isLast = index == stages.lastIndex)
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().staggerIn(6),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NeonButton(
                text = tr("Preguntar a TRACE AI", "Ask TRACE AI"),
                style = NeonStyle.Secondary,
                onClick = onAskAiClick,
                modifier = Modifier.fillMaxWidth()
            )
            NeonButton(
                text = tr("Volver al inicio", "Back to home"),
                style = NeonStyle.Ghost,
                height = 50.dp,
                onClick = onGoHome,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.height(12.dp))
    }
}

data class TimelineStage(
    val title: String,
    val value: String?
)

@Composable
fun TimelineItemRow(
    stage: TimelineStage,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    val isAvailable = !stage.value.isNullOrBlank()
    val displayValue = if (isAvailable) stage.value!! else tr("Información no disponible", "Information not available")
    val nodeColor = if (isAvailable) NeonMint else TextLow

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(nodeColor.copy(alpha = if (isAvailable) 0.14f else 0.06f))
                    .border(1.5.dp, nodeColor.copy(alpha = if (isAvailable) 0.8f else 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(nodeColor)
                )
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .padding(vertical = 2.dp)
                        .background(
                            if (isAvailable) Brush.verticalGradient(listOf(NeonMint.copy(alpha = 0.7f), NeonMint.copy(alpha = 0.15f)))
                            else Brush.verticalGradient(listOf(TextLow.copy(alpha = 0.35f), TextLow.copy(alpha = 0.1f)))
                        )
                )
            }
        }

        Spacer(Modifier.width(14.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 34.dp, top = 2.dp)
        ) {
            Text(
                text = stage.title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = if (isAvailable) NeonMint else TextLow
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = displayValue,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isAvailable) FontWeight.SemiBold else FontWeight.Normal,
                    fontStyle = if (isAvailable) FontStyle.Normal else FontStyle.Italic
                ),
                color = if (isAvailable) TextHi else TextLow
            )
        }
    }
}

@Composable
fun PillarTile(
    title: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier, accent = accent, shape = RoundedCornerShape(18.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = accent
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = TextHi
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InvestigationScreenPreview() {
    TRACETheme {
        AuroraBackground {
            InvestigationScreen(
                productId = "1",
                onAskAiClick = {},
                onBackClick = {},
                onGoHome = {}
            )
        }
    }
}
