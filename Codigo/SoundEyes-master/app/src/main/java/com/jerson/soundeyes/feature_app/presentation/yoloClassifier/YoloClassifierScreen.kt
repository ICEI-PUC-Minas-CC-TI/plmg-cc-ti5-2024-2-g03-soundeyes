package com.jerson.soundeyes.feature_app.presentation.yoloClassifier

import ConectionBluetooth.receiveImageFromESP32
import ConectionBluetooth.simulateReceiveImageFromDrawable
import android.content.Context
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jerson.soundeyes.feature_app.presentation.utils.FileLogger.logToFile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

@Composable
fun YoloClassifierScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: YoloClassifierViewModel = hiltViewModel(),
) {
    val state by viewModel.state
    val context: Context = LocalContext.current
    var bitmapState by remember { mutableStateOf<Bitmap?>(null) }
    var classificationTime by remember { mutableStateOf("") }
    var sendingImage by remember { mutableStateOf("") }
    var isPaused by remember { mutableStateOf(false) }

    // LaunchedEffect controlado por isPaused
    LaunchedEffect(isPaused) {
        if (!isPaused) {
            flow {
                while (true) {
                    emit(Unit)
                    delay(10) // Evita sobrecarga
                }
            }.collect {
                try {
                    val startTime = System.currentTimeMillis()
                    val receivedImage = receiveImageFromESP32(context)
                    val endTime = System.currentTimeMillis()
                    receivedImage?.let {
                        bitmapState = it
                    }
                    sendingImage = "${endTime - startTime} ms"
                    logToFile(context,"Recebimento de Imagem: ", sendingImage)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Classificação da imagem quando bitmapState muda
    LaunchedEffect(bitmapState) {
        if (!isPaused) {
            bitmapState?.let {
                 val startTime = System.currentTimeMillis()
                viewModel.onEvent(YoloEvent.ClassifyImage(it))
                    val endTime = System.currentTimeMillis()
                classificationTime = "${endTime - startTime} ms"
                logToFile(context,"Tempo de Classificação: ",classificationTime)
            }
        }
    }

    Surface(modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(vertical = 30.dp, horizontal = 20.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            bitmapState?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .width(500.dp)
                        .height(500.dp)

                )
            }
            Button(onClick = {
                isPaused = true
                navController.popBackStack()
            }) {
                Text(text = "Voltar")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { isPaused = !isPaused }) {
                Text(text = if (isPaused) "Retomar" else "Pausar")
            }
        }
    }
    BackHandler {
        navController.popBackStack()
    }
}


