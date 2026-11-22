package com.example.trace.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import com.example.trace.model.Product
import com.example.trace.model.productById
import com.example.trace.model.ProductStore
import com.example.trace.ui.components.AuroraBackground
import com.example.trace.ui.components.GlassCard
import com.example.trace.ui.components.NeonButton
import com.example.trace.ui.components.NeonChip
import com.example.trace.ui.components.NeonStyle
import com.example.trace.ui.components.ScoreGauge
import com.example.trace.ui.components.TraceScaffold
import com.example.trace.ui.theme.NeonCyan
import com.example.trace.ui.theme.NeonLime
import com.example.trace.ui.theme.NeonMint
import com.example.trace.ui.theme.TRACETheme
import com.example.trace.ui.theme.TextHi
import com.example.trace.ui.theme.TextMid
import com.example.trace.ui.theme.Void
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: Int,
    val sender: MessageSender,
    val text: String
)

enum class MessageSender {
    USER, AI
}

private const val Q_WORTH = "¿Vale la pena comprarlo?"
private const val Q_GREENER = "¿Hay una opción más sostenible?"
private const val Q_RECYCLE = "¿Cómo puedo reciclarlo?"
private const val Q_COMPARE = "¿Cómo se compara con los demás?"
private val quickQuestions = listOf(Q_WORTH, Q_GREENER, Q_RECYCLE, Q_COMPARE)

@Composable
fun TraceAiScreen(
    productId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val product = productById(productId)

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                id = 0,
                sender = MessageSender.AI,
                text = "¡Hola! Soy **TRACE AI**. Analicé **${product.name}** (puntaje ${product.score}/100). ¿Qué quieres saber?"
            )
        )
    }
    var nextId by remember { mutableIntStateOf(1) }
    var isAnalyzing by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Siempre muestra lo último (mensaje nuevo o indicador "analizando")
    LaunchedEffect(messages.size, isAnalyzing) {
        val last = messages.size - 1 + if (isAnalyzing) 1 else 0
        if (last >= 0) listState.animateScrollToItem(last)
    }

    fun ask(question: String) {
        if (isAnalyzing) return
        messages.add(ChatMessage(nextId++, MessageSender.USER, question))
        isAnalyzing = true
        scope.launch {
            delay(900) // simula el análisis local
            messages.add(ChatMessage(nextId++, MessageSender.AI, generateAiResponse(question, product)))
            isAnalyzing = false
        }
    }

    TraceScaffold(
        modifier = modifier,
        title = "TRACE AI",
        onBack = onBack,
        scroll = false,
        contentPadding = PaddingValues(0.dp),
        spacing = 0.dp
    ) {
        ProductBanner(product, Modifier.padding(horizontal = 20.dp, vertical = 6.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                ChatMessageBubble(message)
            }
            if (isAnalyzing) {
                item(key = "typing") { AnalyzingIndicator() }
            }
        }

        // Preguntas rápidas
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Void.copy(alpha = 0.85f))))
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "PREGÚNTALE A TRACE AI",
                style = MaterialTheme.typography.labelSmall,
                color = TextMid,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            quickQuestions.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { q ->
                        PromptOptionChip(
                            text = q,
                            enabled = !isAnalyzing,
                            onClick = { ask(q) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            NeonButton(
                text = "Volver",
                style = NeonStyle.Ghost,
                height = 46.dp,
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ProductBanner(product: Product, modifier: Modifier = Modifier) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        accent = NeonCyan,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScoreGauge(score = product.score, size = 60.dp, stroke = 6.dp, showCaption = false)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "ANALIZANDO",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMid
                )
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = TextHi
                )
                Spacer(Modifier.height(4.dp))
                NeonChip("⚡ IA LOCAL · DATOS DEL PRODUCTO", NeonLime)
            }
        }
    }
}

