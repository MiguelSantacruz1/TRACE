package com.example.trace.ui

import com.example.trace.i18n.tr
import com.example.trace.ui.theme.Tint
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

private enum class Question {
    WORTH, GREENER, RECYCLE, COMPARE;

    val label: String
        get() = when (this) {
            WORTH -> tr("¿Vale la pena comprarlo?", "Is it worth buying?")
            GREENER -> tr("¿Hay una opción más sostenible?", "Is there a more sustainable option?")
            RECYCLE -> tr("¿Cómo puedo reciclarlo?", "How can I recycle it?")
            COMPARE -> tr("¿Cómo se compara con los demás?", "How does it compare to the others?")
        }
}

private val quickQuestions = Question.values().toList()

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
                text = tr(
                    "¡Hola! Soy **TRACE AI**. Analicé **${product.name}** (puntaje ${product.score}/100). ¿Qué quieres saber?",
                    "Hi! I'm **TRACE AI**. I analyzed **${product.name}** (score ${product.score}/100). What would you like to know?"
                )
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

    fun ask(question: Question) {
        if (isAnalyzing) return
        messages.add(ChatMessage(nextId++, MessageSender.USER, question.label))
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
                text = tr("PREGÚNTALE A TRACE AI", "ASK TRACE AI"),
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
                            text = q.label,
                            enabled = !isAnalyzing,
                            onClick = { ask(q) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            NeonButton(
                text = tr("Volver", "Back"),
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
                    text = tr("ANALIZANDO", "ANALYZING"),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMid
                )
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = TextHi
                )
                Spacer(Modifier.height(4.dp))
                NeonChip(tr("IA LOCAL · DATOS DEL PRODUCTO", "LOCAL AI · PRODUCT DATA"), NeonLime)
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val isUser = message.sender == MessageSender.USER

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            AiAvatar()
            Spacer(Modifier.width(10.dp))
        }

        val shape = RoundedCornerShape(
            topStart = 18.dp,
            topEnd = 18.dp,
            bottomStart = if (isUser) 18.dp else 4.dp,
            bottomEnd = if (isUser) 4.dp else 18.dp
        )
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .weight(1f, fill = false)
                .clip(shape)
                .then(
                    if (isUser) Modifier.background(Brush.linearGradient(listOf(NeonLime, NeonMint)))
                    else Modifier
                        .background(Tint.copy(alpha = 0.06f))
                        .border(1.dp, NeonCyan.copy(alpha = 0.35f), shape)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = markdownBold(message.text, if (isUser) Void else NeonCyan),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUser) Void else TextHi,
                fontWeight = if (isUser) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun AiAvatar() {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(NeonCyan.copy(alpha = 0.12f))
            .border(1.dp, NeonCyan.copy(alpha = 0.6f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text("AI", style = MaterialTheme.typography.labelSmall, color = NeonCyan)
    }
}

@Composable
fun AnalyzingIndicator(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "typing")
    val pulse by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "pulse"
    )

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        AiAvatar()
        Spacer(Modifier.width(10.dp))
        Text(
            text = tr("TRACE AI ESTÁ ANALIZANDO…", "TRACE AI IS ANALYZING…"),
            style = MaterialTheme.typography.labelSmall,
            color = NeonCyan,
            modifier = Modifier.alpha(pulse)
        )
    }
}

@Composable
fun PromptOptionChip(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .height(58.dp)
            .alpha(if (enabled) 1f else 0.45f)
            .clip(shape)
            .background(Tint.copy(alpha = 0.05f))
            .border(1.dp, NeonMint.copy(alpha = 0.35f), shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
            color = TextHi
        )
    }
}

/** Convierte **texto** en negrita; el resto se deja tal cual. */
private fun markdownBold(text: String, boldColor: Color): AnnotatedString = buildAnnotatedString {
    text.split("**").forEachIndexed { i, part ->
        if (i % 2 == 1) {
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = boldColor)) { append(part) }
        } else {
            append(part)
        }
    }
}

private fun generateAiResponse(question: Question, product: Product): String = when (question) {
    Question.WORTH -> when {
        product.score >= 80 -> tr(
            "Sí, vale la pena. Con **${product.score}/100** destaca por su compromiso de sostenibilidad (${product.impactTag}) y una reciclabilidad de ${product.recyclability}.",
            "Yes, it is worth it. With **${product.score}/100** it stands out for its sustainability commitment (${product.impactTag}) and a recyclability of ${product.recyclability}."
        )

        product.score >= 65 -> tr(
            "Es una opción aceptable. Su puntaje es **${product.score}/100** (${product.impactTag}). Tiene puntos a favor, pero todavía hay margen de mejora en reciclabilidad (${product.recyclability}) y transporte. Antes de comprarlo, evalúa si puedes alargar la vida de lo que ya tienes.",
            "It is an acceptable option. Its score is **${product.score}/100** (${product.impactTag}). It has strong points, but there is still room to improve in recyclability (${product.recyclability}) and transport. Before buying, consider whether you can extend the life of what you already own."
        )

        else -> tr(
            "No lo recomendamos como primera opción. Su puntaje es **${product.score}/100**: reparabilidad ${product.repairability} y reciclabilidad ${product.recyclability}. Considera alternativas mejor calificadas.",
            "We do not recommend it as a first choice. Its score is **${product.score}/100**: repairability ${product.repairability} and recyclability ${product.recyclability}. Consider better-rated alternatives."
        )
    }

    Question.GREENER -> {
        val better = ProductStore.products.filter { it.score > product.score }.maxByOrNull { it.score }
        if (better != null) {
            tr(
                "Sí. En el registro local la opción más sostenible es **${better.name}** con **${better.score}/100** (${better.impactTag}, reciclabilidad ${better.recyclability}). Ojo: es de otra categoría (${better.category}), así que sirve como referencia, no como reemplazo directo.",
                "Yes. In the local registry the most sustainable option is **${better.name}** with **${better.score}/100** (${better.impactTag}, recyclability ${better.recyclability}). Note: it is a different category (${better.category}), so use it as a reference, not a direct replacement."
            )
        } else {
            tr(
                "**${product.name}** ya tiene el puntaje más alto del registro local de TRACE (**${product.score}/100**, ${product.impactTag}).",
                "**${product.name}** already has the highest score in the local TRACE registry (**${product.score}/100**, ${product.impactTag})."
            )
        }
    }

    Question.RECYCLE -> {
        val cat = product.category.lowercase()
        val advice = when {
            cat.contains("electr") -> tr(
                "Llévalo a un punto de recolección de residuos electrónicos autorizado en Colombia, para evitar la contaminación por metales pesados.",
                "Take it to an authorized e-waste collection point to avoid heavy-metal contamination."
            )
            cat.contains("aliment") || cat.contains("food") -> tr(
                "El empaque es biodegradable o reciclable. Separa los residuos orgánicos de los envases y usa la caneca que corresponda al código de colores local.",
                "The packaging is biodegradable or recyclable. Separate organic waste from containers and use the bin that matches your local color code."
            )
            cat.contains("calzado") || cat.contains("footwear") || cat.contains("shoe") -> tr(
                "Si todavía sirve, dónalo. Si está desgastado, llévalo a puntos de acopio de calzado o textil, o a programas de recolección de marcas.",
                "If it is still usable, donate it. If it is worn out, take it to footwear or textile drop-off points, or to brand take-back programs."
            )
            else -> tr(
                "Consulta las guías de separación de residuos y los puntos limpios de tu ciudad para materiales de tipo ${product.category}.",
                "Check your city's waste-sorting guides and recycling points for ${product.category} materials."
            )
        }
        tr(
            "Reciclabilidad registrada: **${product.recyclability}**.\n\n$advice",
            "Recorded recyclability: **${product.recyclability}**.\n\n$advice"
        )
    }

    Question.COMPARE -> {
        val ranking = ProductStore.products.sortedByDescending { it.score }
        val position = ranking.indexOfFirst { it.id == product.id } + 1
        val here = tr("  ◀ este", "  ◀ this one")
        val lines = ranking.mapIndexed { i, p ->
            "${i + 1}. ${p.name} — ${p.score}/100" + if (p.id == product.id) here else ""
        }.joinToString("\n")
        tr(
            "**${product.name}** ocupa el puesto **$position de ${ranking.size}** entre los productos registrados en TRACE.\n\n$lines",
            "**${product.name}** ranks **$position of ${ranking.size}** among the products registered in TRACE.\n\n$lines"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TraceAiScreenPreview() {
    TRACETheme {
        AuroraBackground {
            TraceAiScreen(productId = "1", onBack = {})
        }
    }
}
