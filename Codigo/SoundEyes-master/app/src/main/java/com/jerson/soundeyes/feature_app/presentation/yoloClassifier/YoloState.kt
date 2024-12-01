package com.jerson.soundeyes.feature_app.presentation.yoloClassifier

import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import com.jerson.soundeyes.feature_app.domain.model.BoundingBox


data class YoloState(
    val result: List<BoundingBox>? = emptyList(),
    val image: Bitmap? = null,
    val textToSpeech: String? = null
)