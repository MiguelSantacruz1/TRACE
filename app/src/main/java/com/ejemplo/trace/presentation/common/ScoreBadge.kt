package com.ejemplo.trace.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ejemplo.trace.presentation.theme.ScoreCritical
import com.ejemplo.trace.presentation.theme.ScoreExcellent
import com.ejemplo.trace.presentation.theme.ScoreGood
import com.ejemplo.trace.presentation.theme.ScoreModerate
import com.ejemplo.trace.presentation.theme.ScorePoor
import com.ejemplo.trace.presentation.theme.ScoreUnknown

/**
 * Nivel semántico de sostenibilidad del TRACE SCORE.
 * Cumple con accesibilidad: NO comunica el estado solo por color,
 * incluye etiquetas textuales legibles e icono identificador.
 */
enum class ScoreLevel(
    val labelRes: String,
    val color: Color
) {
    EXCELLENT("Excelente", ScoreExcellent),
    GOOD("Bueno", ScoreGood),
    MODERATE("Moderado", ScoreModerate),
    POOR("Desfavorable", ScorePoor),
    CRITICAL("Crítico", ScoreCritical),
    UNKNOWN("Sin calcular", ScoreUnknown)
}

fun scoreToLevel(score: Int?): ScoreLevel {
    return when {
        score == null -> ScoreLevel.UNKNOWN
        score in 81..100 -> ScoreLevel.EXCELLENT
        score in 61..80 -> ScoreLevel.GOOD
        score in 41..60 -> ScoreLevel.MODERATE
        score in 21..40 -> ScoreLevel.POOR
        score in 0..20 -> ScoreLevel.CRITICAL
        else -> ScoreLevel.UNKNOWN
    }
}

@Composable
fun ScoreBadge(
    score: Int?,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    showLabel: Boolean = true
) {
    val level = scoreToLevel(score)
    val scoreText = score?.toString() ?: "--"
    val accessibilityLabel = "TRACE SCORE: $scoreText sobre 100. Nivel: ${level.labelRes}."

    Column(
        modifier = modifier.semantics { contentDescription = accessibilityLabel },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(size),
            shape = CircleShape,
            color = level.color.copy(alpha = 0.15f),
            shadowElevation = 0.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = scoreText,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = (size.value * 0.35f).sp
                        ),
                        color = level.color
                    )
                    Text(
                        text = "/100",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = (size.value * 0.15f).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.size(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = level.color.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = if (score != null) Icons.Default.Eco else Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = null,
                    tint = level.color,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = level.labelRes,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = level.color
                )
            }
        }
    }
}
