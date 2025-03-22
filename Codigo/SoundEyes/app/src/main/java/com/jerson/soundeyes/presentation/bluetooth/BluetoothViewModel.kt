package com.jerson.soundeyes.presentation.bluetooth

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerson.soundeyes.consts.Const
import com.jerson.soundeyes.domain.use_case.BluetoothUseCase
import com.jerson.soundeyes.domain.use_case.YoloUseCases
import com.jerson.soundeyes.utils.FileLogger.saveDataToPublicDirectoryUnified
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val yoloUseCases: YoloUseCases,
    private val bluetoothUseCase: BluetoothUseCase
) : ViewModel() {

    private val _state = mutableStateOf(BluetoothState())
    val state: State<BluetoothState> = _state

    init {
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                val startTime = System.currentTimeMillis()
                val image = bluetoothUseCase.receiveImageUseCase()
                image?.let {
                    val sizeImageKB = (image.byteCount / 1024)
                    _state.value = _state.value.copy(
                        sizeImage = "$sizeImageKB KB",
                        bitmapImage = it
                    )
                    val finishTime = System.currentTimeMillis()
                    val timeTotal = (startTime - finishTime)
                    _state.value = _state.value.copy(
                        timeReceive = "$timeTotal ms"
                    )
                }

                delay(10)
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            snapshotFlow { state.value.bitmapImage }
                .collect { newImage ->
                    newImage?.let {
                        val startTime = System.currentTimeMillis()
                        val boxes = yoloUseCases.classifyImageUseCase(it)
                        val finishTime = System.currentTimeMillis()
                        val timeTotal = (startTime - finishTime)
                        _state.value = _state.value.copy(
                            timeClassifier = "$timeTotal ms",
                            boundingBox = boxes
                        )
                    }

                    _state.value = _state.value.copy(
                        stringLogger = _state.value.stringLogger.append("Classificação: ${_state.value.timeClassifier},  Tamanho da imagem: ${_state.value.sizeImage}, Recebimento: ${_state.value.timeReceive}\n")
                    )


                }
        }

    }

    override fun onCleared() {
        super.onCleared()
       saveDataToPublicDirectoryUnified(
            context = context,
            data = _state.value.stringLogger.toString(),
            nameFile = Const.FILE_CAMERA_BLUETOOTH + "${System.currentTimeMillis()}\n"
        )

    }


}