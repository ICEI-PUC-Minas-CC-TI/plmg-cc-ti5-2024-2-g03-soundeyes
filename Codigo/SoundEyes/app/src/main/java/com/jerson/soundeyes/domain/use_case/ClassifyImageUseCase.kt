package com.jerson.soundeyes.domain.use_case

import android.graphics.Bitmap
import com.jerson.soundeyes.domain.model.BoundingBox
import com.jerson.soundeyes.domain.repository.YoloRepository

class ClassifyImageUseCase(private val yoloRepository: YoloRepository) {
    operator fun invoke(bitmap: Bitmap):List<BoundingBox>?{
       return yoloRepository.classifyImage(bitmap)
    }
}