package com.example.trace.ui

import com.example.trace.ui.theme.Tint
import com.example.trace.ui.theme.ThemeMode
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trace.model.Product
import com.example.trace.model.ProductStore
import com.example.trace.ui.components.AuroraBackground
import com.example.trace.ui.components.GlassCard
import com.example.trace.ui.components.NeonButton
import com.example.trace.ui.components.NeonChip
import com.example.trace.ui.components.ScoreGauge
import com.example.trace.ui.components.SectionLabel
import com.example.trace.ui.components.TraceScaffold
import com.example.trace.ui.components.scoreColor
import com.example.trace.ui.components.staggerIn
import com.example.trace.ui.theme.NeonCyan
import com.example.trace.ui.theme.NeonLime
import com.example.trace.ui.theme.NeonMint
import com.example.trace.ui.theme.NeonViolet
import com.example.trace.ui.theme.TRACETheme
import com.example.trace.ui.theme.TextHi
import com.example.trace.ui.theme.TextMid
import com.example.trace.ui.theme.TraceMono

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    products: List<Product> = ProductStore.products,
    onScanClick: () -> Unit = {},
    onProductClick: (Product) -> Unit = {}
) {
    TraceScaffold(modifier = modifier, spacing = 18.dp) {
        HeaderSection(Modifier.staggerIn(0))
        ScanHeroCard(onScanClick, Modifier.staggerIn(1))
        StatsRow(products, Modifier.staggerIn(2))

        SectionLabel(
            text = "ÚLTIMOS ESCANEOS",
            trailing = "${products.size}",
            modifier = Modifier.staggerIn(3)
        )
        products.forEachIndexed { index, product ->
            ProductCard(
                product = product,
                onClick = { onProductClick(product) },
                modifier = Modifier.staggerIn(4 + index)
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun HeaderSection(modifier: Modifier = Modifier) {
    val blink = rememberInfiniteTransition(label = "blink")
    val dot = blink.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "dot"
    )

    Column(modifier = modifier.fillMaxWidth().padding(top = 8.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(NeonMint.copy(alpha = dot.value))
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "EN LÍNEA · V1.0",
                style = MaterialTheme.typography.labelSmall,
                color = NeonMint
            )
            Spacer(Modifier.weight(1f))
            ModeToggle()
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "TRACE",
            style = TextStyle(
                brush = Brush.horizontalGradient(listOf(NeonLime, NeonMint, NeonCyan)),
                fontSize = 96.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 8.sp,
                shadow = Shadow(NeonMint.copy(alpha = 0.55f), Offset.Zero, 28f)
            )
        )
        Text(
            text = "INVESTIGA LO QUE COMPRAS",
            style = MaterialTheme.typography.labelLarge,
            color = TextMid
        )
    }
}

@Composable
private fun ModeToggle() {
    val shape = RoundedCornerShape(50)
    Text(
        text = if (ThemeMode.dark) "MODO CLARO" else "MODO OSCURO",
        style = MaterialTheme.typography.labelSmall,
        color = TextHi,
        modifier = Modifier
            .clip(shape)
            .background(Tint.copy(alpha = 0.06f))
            .border(1.dp, NeonMint.copy(alpha = 0.5f), shape)
            .clickable { ThemeMode.toggle() }
            .padding(horizontal = 12.dp, vertical = 7.dp)
    )
}

@Composable
fun ScanHeroCard(onScanClick: () -> Unit, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.fillMaxWidth(), accent = NeonLime) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadarOrb(size = 96.dp)
                Spacer(Modifier.width(18.dp))
                Column {
                    Text(
                        text = "Descubre el rastro de cada producto",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextHi
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Origen, transporte, reciclaje y un puntaje de sostenibilidad en segundos.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMid
                    )
                }
            }
            NeonButton(
                text = "Escanear producto",
                onClick = onScanClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RadarOrb(size: Dp, modifier: Modifier = Modifier) {
    val t = rememberInfiniteTransition(label = "radar")
    val sweep = t.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing)),
        label = "sweep"
    )
    val pulse = t.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing)),
        label = "pulse"
    )

    Canvas(modifier.size(size)) {
        val c = center
        val r = this.size.minDimension / 2f
        val hair = 1.dp.toPx()

        drawCircle(NeonLime.copy(alpha = 0.06f), r)
        for (i in 1..3) drawCircle(NeonLime.copy(alpha = 0.22f), r * i / 3f, style = Stroke(hair))
        drawLine(NeonLime.copy(alpha = 0.18f), Offset(c.x - r, c.y), Offset(c.x + r, c.y), hair)
        drawLine(NeonLime.copy(alpha = 0.18f), Offset(c.x, c.y - r), Offset(c.x, c.y + r), hair)

        rotate(sweep.value, c) {
            drawCircle(
                Brush.sweepGradient(
                    0f to Color.Transparent,
                    0.7f to Color.Transparent,
                    1f to NeonLime.copy(alpha = 0.75f),
                    center = c
                ),
                radius = r,
                center = c
            )
        }

        // Ondas de pulso desde el centro
        drawCircle(
            NeonLime.copy(alpha = (1f - pulse.value) * 0.5f),
            radius = r * (0.15f + 0.85f * pulse.value),
            style = Stroke(2.dp.toPx())
        )

        // "Blips" detectados
        drawCircle(NeonMint, 3.dp.toPx(), Offset(c.x + r * 0.45f, c.y - r * 0.35f))
        drawCircle(NeonCyan, 2.5.dp.toPx(), Offset(c.x - r * 0.5f, c.y + r * 0.25f))
        drawCircle(NeonLime.copy(alpha = 0.3f), 9.dp.toPx(), c)
        drawCircle(NeonLime, 4.5.dp.toPx(), c)
    }
}

@Composable
private fun StatsRow(products: List<Product>, modifier: Modifier = Modifier) {
    val average = if (products.isEmpty()) 0 else products.sumOf { it.score } / products.size
    val best = products.maxOfOrNull { it.score } ?: 0

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatTile("${products.size}", "ANALIZADOS", NeonCyan, Modifier.weight(1f))
        StatTile("$average", "PROMEDIO", NeonLime, Modifier.weight(1f))
        StatTile("$best", "MEJOR SCORE", NeonMint, Modifier.weight(1f))
    }
}

@Composable
private fun StatTile(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier, accent = color) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = color,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = label,
                color = TextMid,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontFamily = TraceMono)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = scoreColor(product.score)

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        accent = color,
        shape = RoundedCornerShape(22.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScoreGauge(score = product.score, size = 84.dp, stroke = 8.dp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = product.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = color
                )
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = TextHi
                )
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    NeonChip(product.impactTag, NeonCyan)
                }
            }
            Text(
                text = "›",
                color = color,
                fontSize = 28.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
fun HomeScreenPhonePreview() {
    TRACETheme {
        AuroraBackground { HomeScreen() }
    }
}
