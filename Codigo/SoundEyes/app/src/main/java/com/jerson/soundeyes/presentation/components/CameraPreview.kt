package com.jerson.soundeyes.presentation.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Paint
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.jerson.soundeyes.domain.model.BoundingBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("RestrictedApi")
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onImageCaptured: (Bitmap) -> Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    quality: android.util.Size
) {
    val coroutineScope = rememberCoroutineScope()
    // Flag para evitar processar frames simultaneamente
    val isProcessing = remember { mutableStateOf(false) }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                setBackgroundColor(android.graphics.Color.BLACK)
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                // Configurando resolução menor para reduzir o custo de processamento
                val imageAnalysis = ImageAnalysis.Builder()
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setDefaultResolution(quality)
                    .build().also {
                        it.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                            if (!isProcessing.value) {
                                isProcessing.value = true
                                coroutineScope.launch(Dispatchers.Default) {
                                    try {
                                        val width = imageProxy.width
                                        val height = imageProxy.height

                                        // Cria o bitmap para o frame atual
                                        val bitmapBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

                                        imageProxy.use {
                                            bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer)
                                        }

                                        // Rotaciona a imagem conforme necessário
                                        val matrix = Matrix().apply {
                                            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
                                        }
                                        val rotatedBitmap = Bitmap.createBitmap(
                                            bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height, matrix, true
                                        )

                                        // Chama o callback com o frame processado
                                        onImageCaptured(rotatedBitmap)
                                    } catch (e: Exception) {
                                        Log.e("CameraPreview", "Erro ao processar o frame", e)
                                    } finally {
                                        // Libera a flag para processar o próximo frame
                                        isProcessing.value = false
                                    }
                                }
                            } else {
                                // Se já estiver processando, feche o imageProxy para descartar o frame
                                imageProxy.close()
                            }
                        }
                    }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, imageAnalysis
                    )
                } catch (exc: Exception) {
                    Log.e("CameraPreview", "Erro ao ligar a câmera", exc)
                }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        }
    )
}


@Composable
fun ObjectDetectionOverlay(
    detections: List<BoundingBox>,
    modifier: Modifier = Modifier
) {
    val textPaint = remember {
        Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 30f
        }
    }
    val textPadding = 8f
    val scale = 1.2f

    Canvas(modifier = modifier.fillMaxSize()) {
        detections.forEach { detection ->
            val left = detection.x1 * size.width
            val top = detection.y1 * size.height
            val right = detection.x2 * size.width
            val bottom = detection.y2 * size.height

            // Desenha a caixa
            drawRect(
                color = Color.Red,
                topLeft = Offset(left, top),
                size = Size((right - left) * scale , bottom - top),
                style = Stroke(width = 3f)
            )

            // Calcula as dimensões do fundo do texto
            val bounds = android.graphics.Rect().also {
                textPaint.getTextBounds(detection.clsName, 0, detection.clsName.length, it)
            }
            val textWidth = bounds.width().toFloat()
            val textHeight = bounds.height().toFloat()

            // Desenha o fundo do texto
            drawRect(
                color = Color.Black,
                topLeft = Offset(left, top),
                size = Size(textWidth + textPadding, textHeight + textPadding)
            )

            // Desenha o texto usando o Canvas nativo
            drawContext.canvas.nativeCanvas.drawText(
                detection.clsName,
                left,
                top + textHeight,
                textPaint
            )
        }
    }
}
