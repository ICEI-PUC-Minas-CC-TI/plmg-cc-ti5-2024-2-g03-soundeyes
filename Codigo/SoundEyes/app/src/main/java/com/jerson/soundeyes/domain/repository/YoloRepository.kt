package com.jerson.soundeyes.domain.repository

import android.graphics.Bitmap
import com.jerson.soundeyes.domain.model.BoundingBox

interface YoloRepository {
    fun classifyImage(bitmap: Bitmap): List<BoundingBox>?
    fun closeClassifier()
}