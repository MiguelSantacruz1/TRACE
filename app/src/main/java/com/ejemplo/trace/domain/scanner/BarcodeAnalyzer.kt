package com.ejemplo.trace.domain.scanner

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/**
 * ImageAnalysis.Analyzer para procesar frames de cámara en tiempo real
 * utilizando Google ML Kit Barcode Scanning con prevención de lecturas repetidas.
 */
class BarcodeAnalyzer(
    private val debounceTimeMs: Long = 2000L,
    private val onBarcodeDetected: (ScannedBarcode) -> Unit,
    private val onError: (Exception) -> Unit = {}
) : ImageAnalysis.Analyzer {

    private val scanner: BarcodeScanner

    @Volatile
    private var lastScannedCode: String? = null

    @Volatile
    private var lastScannedTimestamp: Long = 0L

    init {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39,
                Barcode.FORMAT_QR_CODE
            )
            .build()
        scanner = BarcodeScanning.getClient(options)
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val rawValue = barcode.rawValue
                    if (!rawValue.isNullOrBlank() && BarcodeValidator.isValid(rawValue)) {
                        val currentTime = System.currentTimeMillis()
                        // Prevención de lecturas repetidas y rebote
                        if (rawValue != lastScannedCode || (currentTime - lastScannedTimestamp) > debounceTimeMs) {
                            lastScannedCode = rawValue
                            lastScannedTimestamp = currentTime

                            val format = mapMlKitFormat(barcode.format)
                            onBarcodeDetected(
                                ScannedBarcode(
                                    rawValue = rawValue,
                                    format = format,
                                    timestamp = currentTime
                                )
                            )
                            break
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    /**
     * Resetea el historial de detección para permitir escanear de nuevo el mismo código inmediatamente.
     */
    fun reset() {
        lastScannedCode = null
        lastScannedTimestamp = 0L
    }

    private fun mapMlKitFormat(format: Int): BarcodeFormat {
        return when (format) {
            Barcode.FORMAT_EAN_13 -> BarcodeFormat.EAN_13
            Barcode.FORMAT_EAN_8 -> BarcodeFormat.EAN_8
            Barcode.FORMAT_UPC_A -> BarcodeFormat.UPC_A
            Barcode.FORMAT_UPC_E -> BarcodeFormat.UPC_E
            Barcode.FORMAT_CODE_128 -> BarcodeFormat.CODE_128
            Barcode.FORMAT_CODE_39 -> BarcodeFormat.CODE_39
            Barcode.FORMAT_QR_CODE -> BarcodeFormat.QR_CODE
            else -> BarcodeFormat.UNKNOWN
        }
    }
}
