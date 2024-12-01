package com.jerson.soundeyes.feature_app.presentation.main

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

@Composable
fun BackgroundCameraCapture(
    modifier: Modifier = Modifier,
    onImageCaptured: (Bitmap) -> Unit,
    frameRate: Int = 5  // Processar 1 a cada 5 frames (exemplo)
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // Contador de frames
    var frameCount = remember { 0 }

    DisposableEffect(Unit) {
        val cameraProvider = cameraProviderFuture.get()

        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { analysisUseCase ->
                analysisUseCase.setAnalyzer(cameraExecutor) { imageProxy ->
                    frameCount++
                    if (frameCount % frameRate == 0) {
                        // Processar a imagem e convertê-la para Bitmap
                        val bitmap = imageProxy.toBitmap()
                        onImageCaptured(bitmap)
                    }
                    imageProxy.close()  // Libera o frame após o uso
                }
            }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, imageAnalyzer
        )

        onDispose {
            cameraExecutor.shutdown()
        }
    }
}

// Extensão para converter ImageProxy para Bitmap e rotacionar a imagem
fun ImageProxy.toBitmap(): Bitmap {
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer

    // Pegar tamanho dos dados
    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    // Copiar Y, U, e V dados para um array único
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
    val imageBytes = out.toByteArray()

    // Decodifica o array de bytes em um Bitmap
    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

    // Rotaciona o bitmap para a orientação correta
    return bitmap.rotate(90f) // 90 graus para a orientação vertical
}

// Função de extensão para rotacionar o Bitmap
fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = android.graphics.Matrix().apply {
        postRotate(degrees)
    }
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}

