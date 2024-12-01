package com.jerson.soundeyes.feature_app.presentation.yoloClassifier

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerson.soundeyes.R
import com.jerson.soundeyes.feature_app.consts.Const
import com.jerson.soundeyes.feature_app.data.api.YOLOClassifier
import com.jerson.soundeyes.feature_app.domain.use_case.YoloClassifierUseCase
import com.jerson.soundeyes.feature_app.domain.use_case.CloseClassifierUseCase
import com.jerson.soundeyes.feature_app.presentation.utils.Util
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import javax.inject.Inject

@HiltViewModel
class YoloClassifierViewModel @Inject constructor(
    private val  yoloClassifierUseCase: YoloClassifierUseCase,
    private val closeClassifierUseCase: CloseClassifierUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {
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