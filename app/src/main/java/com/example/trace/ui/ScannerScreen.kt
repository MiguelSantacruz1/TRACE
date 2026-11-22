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
