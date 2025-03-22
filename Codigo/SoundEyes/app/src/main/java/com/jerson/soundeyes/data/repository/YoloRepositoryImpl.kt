package com.jerson.soundeyes.data.repository

import android.graphics.Bitmap
import com.jerson.soundeyes.data.ml.YOLOClassifier
import com.jerson.soundeyes.domain.model.BoundingBox
import com.jerson.soundeyes.domain.repository.YoloRepository


class YoloRepositoryImpl(private val yoloClassifier: YOLOClassifier): YoloRepository {
    override fun classifyImage(bitmap: Bitmap): List<BoundingBox>? {
        return yoloClassifier.classifyImage(bitmap)
    }

    override fun closeClassifier() {
       yoloClassifier.close()
    }

}