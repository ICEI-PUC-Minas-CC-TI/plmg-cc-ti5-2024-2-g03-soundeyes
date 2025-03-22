package com.jerson.soundeyes.presentation.bluetooth


import android.graphics.Bitmap
import com.jerson.soundeyes.domain.model.BoundingBox

data class BluetoothState(
    val boundingBox: List<BoundingBox>? = emptyList(),
    val bitmapImage : Bitmap? = null,
    val timeReceive: String = "-ms",
    val timeClassifier: String = "-ms",
    val sizeImage: String = "-KB",
    val stringLogger: StringBuilder = StringBuilder()
)