import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Paint
import android.view.ViewGroup
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.jerson.soundeyes.feature_app.domain.model.BoundingBox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CameraPreviewView(modifier: Modifier = Modifier,
                      processImage: (Bitmap)->Unit, ) {
    val context: Context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                val imageAnalysis = ImageAnalysis.Builder()
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)// Defina a resolução desejada
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build().also {
                        it.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                            val bitmapBuffer =
                                Bitmap.createBitmap(
                                    imageProxy.width,
                                    imageProxy.height,
                                    Bitmap.Config.ARGB_8888
                                )


                            imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
                            imageProxy.close()

                            val matrix = Matrix().apply {
                                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
                            }

                            val rotatedBitmap = Bitmap.createBitmap(
                                bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
                                matrix, true
                            )
                            processImage(rotatedBitmap)

                        }
                    }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, imageAnalysis
                    )
                } catch (exc: Exception) {
                    // Handle exceptions
                }

            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}

@Composable
fun ObjectDetectionOverlay(detections: List<BoundingBox>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {

        detections.forEach { detection ->
            // Calcula as dimensões e posições das caixas de detecção
            val left = detection.x1 * size.width
            val top = detection.y1 * size.height
            val right = detection.x2 * size.width
            val bottom = detection.y2 * size.height

            // Desenha o retângulo da caixa de detecção
            drawRect(
                color = Color.Red,
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top),
                style = Stroke(width = 3f)
            )

            // Define o texto a ser desenhado (nome da classe)
            val drawableText = detection.clsName
            val textPaint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 30f // Tamanho do texto
            }

            // Calcula o tamanho do fundo do texto (caixa do fundo do texto)
            val textPadding = 8f
            val bounds = android.graphics.Rect()
            textPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
            val textWidth = bounds.width().toFloat()
            val textHeight = bounds.height().toFloat()

            // Desenha o fundo do texto (retângulo atrás do texto)
            drawRect(
                color = Color.Black,
                topLeft = Offset(left, top),
                size = Size(textWidth + textPadding, textHeight + textPadding)
            )

            // Desenha o texto da classe
            drawContext.canvas.nativeCanvas.drawText(
                drawableText,
                left,
                top + textHeight, // Posiciona o texto logo abaixo da parte superior
                textPaint
            )
        }


    }
}

