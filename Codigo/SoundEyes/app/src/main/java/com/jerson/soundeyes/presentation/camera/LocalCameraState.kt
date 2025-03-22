package com.jerson.soundeyes.presentation.camera

import android.graphics.Bitmap
import android.util.Size
import com.jerson.soundeyes.domain.model.BoundingBox

data class LocalCameraState(
    val boundingBox: List<BoundingBox>? = emptyList(),
    val processedImage: Bitmap? = null,
    val quality: Size = Size(640,640),
    val isVisible: Boolean = false,
    val timeClassify: String = "--- ms",
    val sizeImage: String = "-- KB",

)
