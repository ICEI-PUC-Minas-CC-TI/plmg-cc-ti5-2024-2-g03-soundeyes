package com.jerson.soundeyes.presentation.bluetooth

import android.graphics.Bitmap

sealed class BluetoothEvent {
    data class ClassifyImage(val image: Bitmap) : BluetoothEvent()
    data object ReceiveImage:BluetoothEvent()
}