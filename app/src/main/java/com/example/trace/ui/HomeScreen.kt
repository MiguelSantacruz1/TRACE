package com.example.trace.ui

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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(NeonMint.copy(alpha = dot.value))
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "SISTEMA EN LÍNEA · V1.0",
                style = MaterialTheme.typography.labelSmall,
                color = NeonMint
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "TRACE",
            style = TextStyle(
                brush = Brush.horizontalGradient(listOf(NeonLime, NeonMint, NeonCyan)),
                fontSize = 64.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp,
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

