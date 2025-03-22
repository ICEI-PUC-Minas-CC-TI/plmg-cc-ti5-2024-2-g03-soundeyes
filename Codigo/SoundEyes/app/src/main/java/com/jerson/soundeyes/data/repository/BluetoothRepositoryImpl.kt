package com.jerson.soundeyes.data.repository

import android.graphics.Bitmap
import com.jerson.soundeyes.data.bluetooth.BluetoothDataSource
import com.jerson.soundeyes.domain.repository.BluetoothRepository

class BluetoothRepositoryImpl(private val bluetoothDataSource: BluetoothDataSource): BluetoothRepository {
    override suspend fun receiveImageFromESP32(): Bitmap? {
        return bluetoothDataSource.receiveImageFromESP32()
    }

    override suspend fun sendCameraConfig(value: Int, callback: (Boolean) -> Unit) {
        bluetoothDataSource.sendCameraConfig(value,callback)
    }

    override fun isEnableBluetooth(): Boolean {
       return bluetoothDataSource.isBluetoothEnabled()
    }
}