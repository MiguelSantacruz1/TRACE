package com.example.trace.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trace.model.Product
import com.example.trace.model.sampleProducts
import com.example.trace.ui.theme.HighScoreGreen
import com.example.trace.ui.theme.LowScoreRed
import com.example.trace.ui.theme.MediumScoreAmber
import com.example.trace.ui.theme.TRACETheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val sender: MessageSender,
    val text: String
)

enum class MessageSender {
    USER, AI
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraceAiScreen(
    productId: String,
    onBackToInvestigation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val product = sampleProducts.find { it.id == productId } ?: sampleProducts.first()

    val scoreColor = when {
        product.score >= 80 -> HighScoreGreen
        product.score >= 65 -> MediumScoreAmber
        else -> LowScoreRed
    }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                id = "1",
                sender = MessageSender.AI,
                text = "¡Hola! Soy TRACE AI 🤖. He analizado el producto ${product.name} (Puntaje: ${product.score}/100). ¿Qué quieres saber sobre este producto?"
            )
        )
    }

    var isAnalyzing by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val options = listOf(
        "¿Vale la pena comprarlo?",
        "¿Hay una alternativa más económica?",
        "¿Hay una opción más sostenible?",
        "¿Cómo puedo reciclarlo?"
    )

    fun handleQuestionSelect(question: String) {
        if (isAnalyzing) return

        messages.add(
            ChatMessage(
                id = System.currentTimeMillis().toString(),
                sender = MessageSender.USER,
                text = question
            )
        )

        isAnalyzing = true

        coroutineScope.launch {
            listState.animateScrollToItem(messages.size)
            delay(1000) // Simulate local AI analysis

            val answer = generateAiResponse(question, product)

            messages.add(
                ChatMessage(
                    id = (System.currentTimeMillis() + 1).toString(),
                    sender = MessageSender.AI,
                    text = answer
                )
            )

            isAnalyzing = false
            listState.animateScrollToItem(messages.size)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🤖 TRACE AI",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackToInvestigation) {
                        Text(
                            text = "←",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Product Context & Local AI Tag Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Analizando producto:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = scoreColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${product.score}/100",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = scoreColor,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Local AI Data Badge Tag
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "⚡ IA local · Datos disponibles del producto",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Chat Conversation History List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatMessageBubble(message = message)
                }

                if (isAnalyzing) {
                    item {
                        AnalyzingIndicator()
                    }
                }
            }

            // Quick Prompt Options Section (Supports Consecutive Selection)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "¿Qué quieres saber sobre este producto?",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // 4 Preset Option Buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    options.chunked(2).forEach { rowOptions ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowOptions.forEach { option ->
                                PromptOptionChip(
                                    text = option,
                                    enabled = !isAnalyzing,
                                    onClick = { handleQuestionSelect(option) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Return to Investigation Button
                OutlinedButton(
                    onClick = onBackToInvestigation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Volver a la investigación",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
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
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .size(36.dp)
                    .padding(end = 6.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "🤖", fontSize = 18.sp)
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUser) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.padding(14.dp)
            )
        }
    }
}

@Composable
fun AnalyzingIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "🤖", fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier
                    .alpha(alpha)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "TRACE AI está analizando...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun PromptOptionChip(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun generateAiResponse(question: String, product: Product): String {
    return when (question) {
        "¿Vale la pena comprarlo?" -> {
            when {
                product.score >= 80 -> {
                    "Sí, vale la pena comprarlo 🟢. Con una puntuación TRACE de ${product.score}/100, este producto destaca por su alto compromiso de sostenibilidad (${product.impactTag}). Se recomienda por sus buenas métricas de reciclabilidad y bajo impacto ambiental."
                }
                product.score >= 65 -> {
                    "Es una opción aceptable 🟡. Su puntuación es de ${product.score}/100 (${product.impactTag}). Aunque cuenta con características positivas como ${product.repairability}, su reciclabilidad o transporte aún tienen margen de mejora. Te sugerimos evaluar si puedes reparar o alargar la vida del producto que ya posees antes de realizar la compra."
                }
                else -> {
                    "No lo recomendamos prioritariamente 🔴. Su puntuación TRACE es de ${product.score}/100. Su impacto ambiental es elevado y sus índices de reparabilidad o reciclabilidad son limitados. Te aconsejamos considerar alternativas con mayor calificación de sostenibilidad."
                }
            }
        }

        "¿Hay una alternativa más económica?" -> {
            val otherProducts = sampleProducts.filter { it.id != product.id }
            if (otherProducts.isNotEmpty()) {
                val candidate = otherProducts.minByOrNull { it.score } ?: otherProducts.first()
                "Información no disponible: En la base de datos de datos estáticos no disponemos de precios exactos. Sin embargo, dentro de nuestro registro de productos de referencia puedes evaluar **${candidate.name}** (${candidate.category}, TRACE SCORE: ${candidate.score}/100)."
            } else {
                "Información no disponible: No se registran alternativas económicas para este producto en los datos locales actuales de TRACE."
            }
        }

        "¿Hay una opción más sostenible?" -> {
            val sustainableOption = sampleProducts.filter { it.score > product.score }.maxByOrNull { it.score }
            if (sustainableOption != null) {
                "Sí, en nuestro registro local encontramos una opción más sostenible: **${sustainableOption.name}** con una puntuación TRACE de **${sustainableOption.score}/100** (${sustainableOption.impactTag}, ${sustainableOption.recyclability})."
            } else {
                "Actualmente **${product.name}** tiene la puntuación de sostenibilidad más alta registrada en la base de datos local de TRACE (${product.score}/100, ${product.impactTag})."
            }
        }

        "¿Cómo puedo reciclarlo?" -> {
            val recyclabilityText = product.recyclability
            val categoryAdvice = when (product.category) {
                "Electrónica" -> "Para componentes electrónicos, llévalo a un centro de recolección de e-waste autorizado en Colombia para evitar la contaminación por metales pesados."
                "Alimentos" -> "El empaque es biodegradable o reciclable. Separa los residuos orgánicos de los envases y deposítalo en la caneca correspondiente según el código de colores local."
                "Calzado" -> "Si aún es usable, considera donarlo. En caso de desgaste, llévalo a puntos de acopio de textil/calzado o programas de recolección de marcas."
                else -> "Consulta las guías de separación de residuos y puntos limpios locales en Colombia para materiales de tipo ${product.category}."
            }
            "Nivel de reciclabilidad registrado: **$recyclabilityText**.\n\n$categoryAdvice"
        }

        else -> {
            "He analizado los datos disponibles de **${product.name}**: TRACE SCORE ${product.score}/100, ${product.impactTag}, ${product.repairability} y ${product.recyclability}."
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TraceAiScreenPreview() {
    TRACETheme {
        TraceAiScreen(
            productId = "1",
            onBackToInvestigation = {}
        )
    }
}
