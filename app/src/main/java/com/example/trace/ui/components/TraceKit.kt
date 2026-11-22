package com.example.trace.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trace.ui.theme.HighScoreGreen
import com.example.trace.ui.theme.LowScoreRed
import com.example.trace.ui.theme.MediumScoreAmber
import com.example.trace.ui.theme.NeonCyan
import com.example.trace.ui.theme.NeonLime
import com.example.trace.ui.theme.NeonMint
import com.example.trace.ui.theme.NeonViolet
import com.example.trace.ui.theme.TextHi
import com.example.trace.ui.theme.TextMid
import com.example.trace.ui.theme.TraceMono
import com.example.trace.ui.theme.Void
import com.example.trace.ui.theme.Ink
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

// ── Lógica de score compartida ───────────────────────────────────────────────

fun scoreColor(score: Int): Color = when {
    score >= 80 -> HighScoreGreen
    score >= 65 -> MediumScoreAmber
    else -> LowScoreRed
}

fun scoreRating(score: Int): String = when {
    score >= 80 -> "SOSTENIBILIDAD MUY ALTA"
    score >= 65 -> "SOSTENIBILIDAD ACEPTABLE"
    else -> "SOSTENIBILIDAD BAJA"
}

// ── Fondo ────────────────────────────────────────────────────────────────────

/** Fondo vivo: gradientes de aurora que derivan despacio sobre una rejilla tenue. */
@Composable
fun AuroraBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val transition = rememberInfiniteTransition(label = "aurora")
    val drift = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(16000, easing = LinearEasing), RepeatMode.Reverse),
        label = "drift"
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Void)
            .drawBehind {
                val w = size.width
                val h = size.height
                val d = drift.value

                val c1 = Offset(w * (0.05f + 0.45f * d), h * 0.08f)
                drawCircle(
                    Brush.radialGradient(listOf(NeonMint.copy(alpha = 0.20f), Color.Transparent), center = c1, radius = w),
                    radius = w, center = c1
                )
                val c2 = Offset(w * (0.95f - 0.40f * d), h * 0.88f)
                drawCircle(
                    Brush.radialGradient(listOf(NeonViolet.copy(alpha = 0.20f), Color.Transparent), center = c2, radius = w * 0.9f),
                    radius = w * 0.9f, center = c2
                )
                val c3 = Offset(w * 0.9f, h * (0.35f + 0.1f * d))
                drawCircle(
                    Brush.radialGradient(listOf(NeonCyan.copy(alpha = 0.08f), Color.Transparent), center = c3, radius = w * 0.7f),
                    radius = w * 0.7f, center = c3
                )

                // Rejilla tenue
                val step = 36.dp.toPx()
                val lineColor = Color.White.copy(alpha = 0.03f)
                var x = 0f
                while (x < w) {
                    drawLine(lineColor, Offset(x, 0f), Offset(x, h), 1f)
                    x += step
                }
                var y = 0f
                while (y < h) {
                    drawLine(lineColor, Offset(0f, y), Offset(w, y), 1f)
                    y += step
                }
            },
        content = content
    )
}

// ── Tarjeta de cristal ───────────────────────────────────────────────────────

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    accent: Color = NeonMint,
    shape: Shape = RoundedCornerShape(24.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(shape)
            .background(Ink.copy(alpha = 0.72f))
            .background(Brush.linearGradient(listOf(accent.copy(alpha = 0.10f), Color.White.copy(alpha = 0.02f))))
            .border(
                1.dp,
                Brush.linearGradient(listOf(accent.copy(alpha = 0.65f), Color.White.copy(alpha = 0.06f), accent.copy(alpha = 0.18f))),
                shape
            )
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier),
        content = content
    )
}

// ── Botones ──────────────────────────────────────────────────────────────────

enum class NeonStyle { Primary, Secondary, Ghost }

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: NeonStyle = NeonStyle.Primary,
    enabled: Boolean = true,
    leading: String? = null,
    height: Dp = 56.dp
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.96f else 1f, spring(dampingRatio = 0.6f), label = "press")
    val shape = RoundedCornerShape(18.dp)
    val glow = if (style == NeonStyle.Secondary) NeonCyan else NeonLime
    val fill: Brush = when (style) {
        NeonStyle.Primary -> Brush.horizontalGradient(listOf(NeonLime, NeonMint))
        NeonStyle.Secondary -> Brush.horizontalGradient(listOf(NeonCyan, NeonViolet))
        NeonStyle.Ghost -> SolidColor(Color.White.copy(alpha = 0.05f))
    }
    val fg = if (style == NeonStyle.Ghost) TextHi else Void

    Box(
        modifier = modifier
            .height(height)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = if (enabled) 1f else 0.4f
            }
            .then(
                if (style != NeonStyle.Ghost && enabled)
                    Modifier.shadow(16.dp, shape, ambientColor = glow, spotColor = glow)
                else Modifier
            )
            .clip(shape)
            .background(fill)
            .then(if (style == NeonStyle.Ghost) Modifier.border(1.dp, NeonMint.copy(alpha = 0.45f), shape) else Modifier)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            if (leading != null) {
                Text(leading, fontSize = 18.sp, modifier = Modifier.padding(end = 10.dp))
            }
            Text(
                text = text.uppercase(),
                color = fg,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 1.8.sp)
            )
        }
    }
}

// ── Etiquetas ────────────────────────────────────────────────────────────────

@Composable
fun NeonChip(text: String, color: Color, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(50)
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier
            .clip(shape)
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.35f), shape)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    )
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier, trailing: String? = null) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .width(4.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Brush.verticalGradient(listOf(NeonLime, NeonMint)))
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = TextHi,
            modifier = Modifier.weight(1f)
        )
        if (trailing != null) NeonChip(trailing, NeonMint)
    }
}

// ── Medidor de score ─────────────────────────────────────────────────────────

/** Arco de 270° con brillo, cabeza luminosa y conteo animado hasta [score]. */
@Composable
fun ScoreGauge(
    score: Int,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    stroke: Dp = 14.dp,
    showCaption: Boolean = true
) {
    val color = scoreColor(score)
    val progress = remember { Animatable(0f) }
    LaunchedEffect(score) {
        progress.snapTo(0f)
        progress.animateTo(score / 100f, tween(1400, easing = FastOutSlowInEasing))
    }
    val shown = (progress.value * 100f).roundToInt()

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = stroke.toPx()
            val inset = sw * 1.2f
            val arcSize = Size(this.size.width - inset * 2, this.size.height - inset * 2)
            val topLeft = Offset(inset, inset)
            val sweep = 270f * progress.value

            drawArc(Color.White.copy(alpha = 0.07f), 135f, 270f, false, topLeft, arcSize, style = Stroke(sw, cap = StrokeCap.Round))
            if (sweep > 0.5f) {
                drawArc(color.copy(alpha = 0.22f), 135f, sweep, false, topLeft, arcSize, style = Stroke(sw * 2.4f, cap = StrokeCap.Round))
                drawArc(color, 135f, sweep, false, topLeft, arcSize, style = Stroke(sw, cap = StrokeCap.Round))
                val r = arcSize.width / 2f
                val ang = (135f + sweep) * (PI.toFloat() / 180f)
                val head = Offset(center.x + r * cos(ang), center.y + r * sin(ang))
                drawCircle(color.copy(alpha = 0.35f), sw * 1.1f, head)
                drawCircle(Color.White, sw * 0.38f, head)
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$shown",
                color = color,
                fontSize = (size.value * 0.32f).sp,
                fontWeight = FontWeight.Black,
                lineHeight = (size.value * 0.34f).sp
            )
            if (showCaption && size >= 120.dp) {
                Text(
                    text = "/ 100",
                    color = TextMid,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

// ── Barra superior + scaffold ────────────────────────────────────────────────

@Composable
fun TraceTopBar(title: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
                .border(1.dp, NeonMint.copy(alpha = 0.4f), CircleShape)
                .semantics { contentDescription = "Volver" }
                .clickable(role = Role.Button, onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Canvas(Modifier.size(16.dp)) {
                val w = size.width
                val h = size.height
                val sw = 2.6.dp.toPx()
                drawLine(NeonMint, Offset(w * 0.7f, h * 0.05f), Offset(w * 0.25f, h * 0.5f), sw, StrokeCap.Round)
                drawLine(NeonMint, Offset(w * 0.25f, h * 0.5f), Offset(w * 0.7f, h * 0.95f), sw, StrokeCap.Round)
            }
        }
        Spacer(Modifier.width(14.dp))
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp, letterSpacing = 2.2.sp, fontFamily = TraceMono),
            color = TextHi
        )
    }
}

