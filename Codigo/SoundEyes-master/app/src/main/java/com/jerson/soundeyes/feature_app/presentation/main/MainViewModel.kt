package com.jerson.soundeyes.feature_app.presentation.main

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerson.soundeyes.feature_app.domain.use_case.CloseClassifierUseCase
import com.jerson.soundeyes.feature_app.domain.use_case.YoloClassifierUseCase
import com.jerson.soundeyes.feature_app.presentation.yoloClassifier.YoloEvent
import com.jerson.soundeyes.feature_app.presentation.yoloClassifier.YoloState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val  yoloClassifierUseCase: YoloClassifierUseCase,
    private val closeClassifierUseCase: CloseClassifierUseCase,
    @ApplicationContext private val context: Context
): ViewModel(){
    private val _state = mutableStateOf(YoloState())
    val state: State<YoloState> = _state



    fun onEvent(event: YoloEvent) {
        when (event) {
            is YoloEvent.ClassifyImage -> { // Modifique para aceitar um Bitmap
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        result = yoloClassifierUseCase.invoke(event.bitmap) // Usa o bitmap do evento
                    )
                }
            }
            is YoloEvent.CloseYolo -> {
                viewModelScope.launch {
                    closeClassifierUseCase.invoke()
                }
            }

        }
    }

}
