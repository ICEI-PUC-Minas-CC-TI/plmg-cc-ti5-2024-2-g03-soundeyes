package com.jerson.soundeyes.feature_app.presentation.yoloClassifier

import android.graphics.Bitmap

sealed class YoloEvent {
    data class ClassifyImage(val bitmap: Bitmap): YoloEvent()
    data object CloseYolo:YoloEvent()
}
