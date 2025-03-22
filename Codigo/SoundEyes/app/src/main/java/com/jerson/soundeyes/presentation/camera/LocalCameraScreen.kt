package com.jerson.soundeyes.presentation.camera


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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.jerson.soundeyes.presentation.components.BottonSheetLog
import com.jerson.soundeyes.presentation.components.CameraPreview
import com.jerson.soundeyes.presentation.components.ObjectDetectionOverlay
import com.jerson.soundeyes.ui.theme.SoundEyesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalCameraScreen(
    modifier: Modifier = Modifier,
    state: LocalCameraState,
    event: (LocalCameraEvent) -> Unit,
) {


    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(

                title = {
                    Text("Câmera do Dispositivo")
                },
                actions = {
                    IconButton(onClick = {
                        event(LocalCameraEvent.ActivateModalSheet)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Estatísticas da Câmera"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { event(LocalCameraEvent.TakeImage) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Camera,
                    contentDescription = "Obter foto"
                )
            }
        }
    ) { paddingValues: PaddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CameraPreview(
                modifier = Modifier
                    .fillMaxSize(),
                onImageCaptured = { bitmap ->
                    event(LocalCameraEvent.ClassifyImage(bitmap))
                },
                quality = state.quality
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .zIndex(2f)
        ) {
            state.boundingBox?.let { ObjectDetectionOverlay(it) }
        }

        if(state.isVisible){
            BottonSheetLog(
                timeClassifier = state.timeClassify,
                sizeImage = state.sizeImage,
                onDismiss = { event(LocalCameraEvent.OnDismissModalSheet) }

            )
        }


    }


}


@Preview
@Composable
private fun LocalCameraPrev() {
    SoundEyesTheme {
        LocalCameraScreen(state = LocalCameraState(), event = {

        })
    }
}

