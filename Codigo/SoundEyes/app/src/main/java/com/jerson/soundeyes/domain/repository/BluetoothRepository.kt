package com.jerson.soundeyes.domain.repository

import android.graphics.Bitmap

interface BluetoothRepository {
    suspend fun receiveImageFromESP32(): Bitmap?
    suspend fun sendCameraConfig(value: Int, callback : (Boolean) -> Unit)
    fun isEnableBluetooth(): Boolean
}