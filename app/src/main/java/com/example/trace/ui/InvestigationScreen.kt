package com.example.trace.ui

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
        title = "Investigación",
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
                        text = "Puntuación de sostenibilidad",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextHi
                    )
                    Spacer(Modifier.height(10.dp))
                    NeonChip(scoreRating(product.score), color)
                }
            }
        }

        SectionLabel("PILARES DE SOSTENIBILIDAD", Modifier.staggerIn(2))

        Column(
            modifier = Modifier.fillMaxWidth().staggerIn(3),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PillarTile("🌍", "Impacto", product.impactTag, NeonMint, Modifier.weight(1f).fillMaxHeight())
                PillarTile("🚛", "Transporte", product.transport ?: "No disponible", NeonViolet, Modifier.weight(1f).fillMaxHeight())
            }
            Row(
                modifier = Modifier.height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PillarTile("🛠️", "Reparabilidad", product.repairability, NeonCyan, Modifier.weight(1f).fillMaxHeight())
                PillarTile("♻️", "Reciclabilidad", product.recyclability, NeonLime, Modifier.weight(1f).fillMaxHeight())
            }
        }

        // Línea de tiempo
        val stages = listOf(
            TimelineStage("Materia prima", "🌾", product.rawMaterial),
            TimelineStage("Fabricación", "🏭", product.manufacturing),
            TimelineStage("País de origen", "🗺️", product.countryOfOrigin),
            TimelineStage("Transporte", "🚛", product.transport),
            TimelineStage("País de destino", "🇨🇴", product.destinationCountry),
            TimelineStage("Tienda", "🏪", product.store),
            TimelineStage("Consumidor", "👤", product.consumerStage)
        )
        val verified = stages.count { !it.value.isNullOrBlank() }

        SectionLabel(
            text = "RECORRIDO DEL PRODUCTO",
            trailing = "$verified/${stages.size} VERIFICADAS",
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
