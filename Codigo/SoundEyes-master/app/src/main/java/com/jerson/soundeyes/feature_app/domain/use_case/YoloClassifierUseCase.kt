package com.jerson.soundeyes.feature_app.domain.use_case

import android.graphics.Bitmap
import com.jerson.soundeyes.feature_app.domain.model.BoundingBox

import com.jerson.soundeyes.feature_app.domain.repository.YoloRepository

class YoloClassifierUseCase(private val yoloRepository: YoloRepository) {
    operator fun invoke(bitmap: Bitmap) : List<BoundingBox>? {
        return yoloRepository.classifyImage(bitmap)
    }
}