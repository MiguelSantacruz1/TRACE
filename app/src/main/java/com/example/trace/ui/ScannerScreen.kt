package com.example.trace.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview as CameraPreviewUseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.trace.ai.ProductAnalyzer
import com.example.trace.model.ProductStore
import com.example.trace.ui.components.AuroraBackground
import com.example.trace.ui.components.NeonButton
import com.example.trace.ui.components.NeonStyle
import com.example.trace.ui.components.TraceTopBar
import com.example.trace.ui.theme.NeonAmber
import com.example.trace.ui.theme.NeonCyan
import com.example.trace.ui.theme.NeonLime
import com.example.trace.ui.theme.NeonMint
import com.example.trace.ui.theme.NeonRose
import com.example.trace.ui.theme.TRACETheme
import com.example.trace.ui.theme.TextHi
import com.example.trace.ui.theme.TextMid
import com.example.trace.ui.theme.Void
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

enum class ScanState {
    IDLE, ANALYZING, IDENTIFIED, ERROR
}

private const val MAX_SIDE = 1024

private val analyzingMessages = listOf(
    "IDENTIFICANDO PRODUCTO",
    "BUSCANDO EN LA WEB",
    "REVISANDO MATERIALES Y ORIGEN",
    "CALCULANDO TU TRACE SCORE"
)

@Composable
fun ScannerScreen(
    onScanSuccess: (productId: String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var scanState by remember { mutableStateOf(ScanState.IDLE) }
    var captured by remember { mutableStateOf<Bitmap?>(null) }
    var errorText by remember { mutableStateOf("") }
    var identifiedName by remember { mutableStateOf("") }
    var messageIndex by remember { mutableStateOf(0) }
    var analysisJob by remember { mutableStateOf<Job?>(null) }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var permissionAsked by remember { mutableStateOf(false) }
    var cameraError by remember { mutableStateOf(false) }
    val imageCapture = remember {
        ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        hasPermission = it
        permissionAsked = true
    }
    LaunchedEffect(Unit) { if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA) }

    val realCamera = hasPermission && !cameraError

    // Envía la foto a la IA y navega al resultado. Vive en el scope de la pantalla:
    // si el usuario sale, se cancela y no aparece un resultado "fantasma".
    fun analyze(bitmap: Bitmap) {
        captured = bitmap
        scanState = ScanState.ANALYZING
        analysisJob = scope.launch {
            val jpeg = ByteArrayOutputStream().also { bitmap.compress(Bitmap.CompressFormat.JPEG, 82, it) }.toByteArray()
            ProductAnalyzer.analyze(jpeg)
                .onSuccess { product ->
                    ProductStore.add(product)
                    identifiedName = product.name
                    scanState = ScanState.IDENTIFIED
                    delay(900)
                    onScanSuccess(product.id)
                }
                .onFailure {
                    errorText = it.message ?: "Error desconocido."
                    scanState = ScanState.ERROR
                }
        }
    }

    fun reset() {
        captured = null
        errorText = ""
        scanState = ScanState.IDLE
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val bmp = runCatching { decodeUri(context, uri) }.getOrNull()
            if (bmp != null) analyze(bmp) else {
                errorText = "No se pudo leer esa imagen."
                scanState = ScanState.ERROR
            }
        }
    }

    fun takePhoto() {
        if (scanState != ScanState.IDLE) return
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val bmp = runCatching { image.toScaledBitmap() }.getOrNull()
                    image.close()
                    if (bmp != null) analyze(bmp) else {
                        errorText = "No se pudo procesar la foto."
                        scanState = ScanState.ERROR
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    errorText = "No se pudo tomar la foto."
                    scanState = ScanState.ERROR
                }
            }
        )
    }

    // Mensajes rotativos mientras la IA trabaja.
    LaunchedEffect(scanState) {
        if (scanState == ScanState.ANALYZING) {
            messageIndex = 0
            while (true) {
                delay(2600)
                messageIndex = (messageIndex + 1) % analyzingMessages.size
            }
        }
    }

    val frameColor by animateColorAsState(
        targetValue = when (scanState) {
            ScanState.IDLE -> NeonCyan
            ScanState.ANALYZING -> NeonAmber
            ScanState.IDENTIFIED -> NeonMint
            ScanState.ERROR -> NeonRose
        },
        animationSpec = tween(350),
        label = "frameColor"
    )

    Box(modifier.fillMaxSize()) {
        CameraFeed(Modifier.fillMaxSize())

        Column(Modifier.fillMaxSize()) {
            TraceTopBar(title = "Escáner IA", onBack = onBackClick)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val badgeColor = if (realCamera) NeonMint else NeonAmber
                Text(
                    text = if (realCamera) "● CÁMARA ACTIVA" else "● SIN CÁMARA · USA LA GALERÍA",
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeColor,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(badgeColor.copy(alpha = 0.10f))
                        .border(1.dp, badgeColor.copy(alpha = 0.35f), RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )

                Spacer(Modifier.height(20.dp))

                Viewfinder(
                    scanState = scanState,
                    frameColor = frameColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 360.dp)
                        .height(320.dp)
                ) {
                    val shot = captured
                    when {
                        shot != null -> Image(
                            bitmap = shot.asImageBitmap(),
                            contentDescription = "Foto del producto",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        realCamera -> CameraPreview(
                            imageCapture = imageCapture,
                            onError = { cameraError = true },
                            modifier = Modifier.fillMaxSize()
                        )

                        !hasPermission -> PermissionPanel(
                            denied = permissionAsked,
                            onRequest = { permissionLauncher.launch(Manifest.permission.CAMERA) }
                        )
                    }
                }

                Spacer(Modifier.height(22.dp))
                StatusHud(
                    scanState = scanState,
                    color = frameColor,
                    message = analyzingMessages[messageIndex],
                    productName = identifiedName,
                    errorText = errorText,
                    cameraProblem = !realCamera
                )
                Spacer(Modifier.height(16.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (scanState) {
                    ScanState.IDLE -> Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        SideButton("🖼", "GALERÍA") { galleryLauncher.launch("image/*") }
                        Shutter(enabled = realCamera, onClick = ::takePhoto)
                        // Marcador de simetría con el botón de galería.
                        Spacer(Modifier.size(64.dp))
                    }

                    ScanState.ERROR -> {
                        NeonButton(
                            text = "Intentar de nuevo",
                            leading = "↺",
                            onClick = ::reset,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    else -> NeonButton(
                        text = if (scanState == ScanState.ANALYZING) "Analizando…" else "¡Identificado!",
                        enabled = false,
                        onClick = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (scanState == ScanState.ANALYZING) {
                    NeonButton(
                        text = "Cancelar",
                        style = NeonStyle.Ghost,
                        height = 46.dp,
                        onClick = {
                            analysisJob?.cancel()
                            reset()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}

// ── Imágenes ─────────────────────────────────────────────────────────────────

private fun ImageProxy.toScaledBitmap(): Bitmap {
    val raw = toBitmap()
    val matrix = Matrix().apply { postRotate(imageInfo.rotationDegrees.toFloat()) }
    val rotated = Bitmap.createBitmap(raw, 0, 0, raw.width, raw.height, matrix, true)
    return rotated.scaledDown()
}

private fun Bitmap.scaledDown(): Bitmap {
    val longest = maxOf(width, height)
    if (longest <= MAX_SIDE) return this
    val ratio = MAX_SIDE / longest.toFloat()
    return Bitmap.createScaledBitmap(this, (width * ratio).toInt(), (height * ratio).toInt(), true)
}

private fun decodeUri(context: android.content.Context, uri: Uri): Bitmap {
    val source = ImageDecoder.createSource(context.contentResolver, uri)
    return ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        val longest = maxOf(info.size.width, info.size.height)
        if (longest > MAX_SIDE) {
            val ratio = MAX_SIDE / longest.toFloat()
            decoder.setTargetSize((info.size.width * ratio).toInt(), (info.size.height * ratio).toInt())
        }
    }
}

// ── Cámara real: CameraX ─────────────────────────────────────────────────────

@Composable
private fun CameraPreview(
    imageCapture: ImageCapture,
    onError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnError by rememberUpdatedState(onError)
    val previewView = remember {
        PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER }
    }

    DisposableEffect(lifecycleOwner) {
        val future = ProcessCameraProvider.getInstance(context)
        var provider: ProcessCameraProvider? = null
        future.addListener({
            try {
                val cameraProvider = future.get()
                provider = cameraProvider
                val preview = CameraPreviewUseCase.Builder().build()
                    .also { it.setSurfaceProvider(previewView.surfaceProvider) }
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture
                )
            } catch (e: Exception) {
                currentOnError()
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose { provider?.unbindAll() }
    }

    AndroidView(factory = { previewView }, modifier = modifier)
}

@Composable
private fun PermissionPanel(denied: Boolean, onRequest: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📷", fontSize = 34.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (denied) "Sin permiso de cámara" else "TRACE necesita la cámara",
            style = MaterialTheme.typography.titleSmall,
            color = TextHi,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Para fotografiar el producto. También puedes elegir una foto de la galería.",
            style = MaterialTheme.typography.bodySmall,
            color = TextMid,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        PillChip(text = "Permitir cámara", onClick = onRequest)
    }
}

// ── Controles ────────────────────────────────────────────────────────────────

@Composable
private fun Shutter(enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .border(3.dp, if (enabled) NeonLime else TextMid.copy(alpha = 0.4f), CircleShape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(7.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    if (enabled) Brush.linearGradient(listOf(NeonLime, NeonMint))
                    else Brush.linearGradient(listOf(TextMid.copy(alpha = 0.25f), TextMid.copy(alpha = 0.25f)))
                )
        )
    }
}

@Composable
private fun SideButton(icon: String, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp)
    ) {
        Text(icon, fontSize = 26.sp)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextMid)
    }
}

@Composable
private fun PillChip(text: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Text(
        text = text.uppercase(),
        color = NeonCyan,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier
            .clip(shape)
            .background(NeonCyan.copy(alpha = 0.14f))
            .border(1.dp, NeonCyan.copy(alpha = 0.7f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    )
}

// ── Fondo ────────────────────────────────────────────────────────────────────

@Composable
private fun CameraFeed(modifier: Modifier = Modifier) {
    Canvas(modifier.background(Void)) {
        val w = size.width
        val h = size.height
        drawRect(Brush.verticalGradient(listOf(Color(0xFF0B1913), Color(0xFF05080A), Color(0xFF0A0612))))

        val vp = Offset(w / 2f, h * 0.42f)
        val grid = NeonMint.copy(alpha = 0.07f)
        for (i in -8..8) {
            drawLine(grid, vp, Offset(w / 2f + i * w * 0.22f, h), 1f)
        }
        var y = vp.y
        var gap = 10.dp.toPx()
        while (y < h) {
            drawLine(grid, Offset(0f, y), Offset(w, y), 1f)
            y += gap
            gap *= 1.28f
        }

        drawRect(
            Brush.radialGradient(
                listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)),
                center = center,
                radius = size.maxDimension * 0.65f
            )
        )
    }
}

// ── Visor ────────────────────────────────────────────────────────────────────

@Composable
private fun Viewfinder(
    scanState: ScanState,
    frameColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val t = rememberInfiniteTransition(label = "scan")
    val laser = t.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser"
    )
    val breathe = t.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
