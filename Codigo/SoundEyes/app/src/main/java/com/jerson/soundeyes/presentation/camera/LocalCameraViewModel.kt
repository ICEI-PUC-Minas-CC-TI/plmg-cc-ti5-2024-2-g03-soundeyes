package com.jerson.soundeyes.presentation.camera

import android.content.Context
import android.util.Log
import android.util.Size
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerson.soundeyes.consts.Const
import com.jerson.soundeyes.domain.use_case.YoloUseCases
import com.jerson.soundeyes.utils.FileLogger
import com.jerson.soundeyes.utils.Util.drawBoundingBoxesOnBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LocalCameraViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val yoloUseCases: YoloUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(LocalCameraState())
    val state: State<LocalCameraState> = _state

    init {
        val resolution = savedStateHandle.get<String>("resolution")
        resolution?.let {
            getSizeImage(it)
        }

    }


    private fun getSizeImage(str: String) {
        val list = str.replace("{", "").replace("}", "").split("x")
        Log.d("lista", list.toString())
        if (list.isNotEmpty()) {
            val width = list[0].toInt()
            val heigth = list[1].toInt()
            _state.value = _state.value.copy(
                quality = Size(width, heigth)
            )
        }
    }

    fun onEvent(event: LocalCameraEvent) {
        when (event) {
            is LocalCameraEvent.ClassifyImage -> {
                viewModelScope.launch {
                    val startTime = System.currentTimeMillis()
                    val boxes = yoloUseCases.classifyImageUseCase(event.image)
                    // Desenha os boxes direto na imagem
                    val processedBitmap = boxes?.let { drawBoundingBoxesOnBitmap(event.image, it) }
                    val finishTime = System.currentTimeMillis()
                    val timeTotal = (startTime - finishTime)
                    val sizeImageKB = (event.image.byteCount / 1024)
                    _state.value = _state.value.copy(
                        boundingBox = boxes,
                        processedImage = processedBitmap,
                        timeClassify = "$timeTotal ms",
                        sizeImage = "$sizeImageKB KB"
                    )
                    delay(100)
                }

            }

            is LocalCameraEvent.CloseClassify -> {
                viewModelScope.launch {
                    yoloUseCases.closeYoloUseCase()
                }
            }

            is LocalCameraEvent.ActivateModalSheet -> {
                _state.value = _state.value.copy(
                    isVisible = true
                )
            }

            is LocalCameraEvent.OnDismissModalSheet -> {
                _state.value = _state.value.copy(
                    isVisible = false
                )
            }

            is LocalCameraEvent.TakeImage -> {
                viewModelScope.launch {
                    _state.value.processedImage?.let {
                        FileLogger.saveBitmapToPublicDirectory(
                            context = context,
                            bitmap = it,
                            nameFile = Const.FILE_CAMERA_CLASSIFIER
                        )
                    }
                }
            }
        }
    }


}