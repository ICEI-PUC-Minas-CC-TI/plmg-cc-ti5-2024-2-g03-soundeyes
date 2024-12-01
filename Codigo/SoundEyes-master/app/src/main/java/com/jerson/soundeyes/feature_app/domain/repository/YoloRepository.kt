package com.jerson.soundeyes.feature_app.domain.repository

import android.graphics.Bitmap
import com.jerson.soundeyes.feature_app.domain.model.BoundingBox

interface YoloRepository {
    fun classifyImage(bitmap: Bitmap): List<BoundingBox>?
    fun closeClassifier()
}