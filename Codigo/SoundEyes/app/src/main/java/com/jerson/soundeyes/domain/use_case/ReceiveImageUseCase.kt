package com.jerson.soundeyes.domain.use_case

import android.graphics.Bitmap
import com.jerson.soundeyes.domain.repository.BluetoothRepository

class ReceiveImageUseCase(private val bluetoothRepository: BluetoothRepository) {
    suspend operator fun invoke(): Bitmap?{
        return bluetoothRepository.receiveImageFromESP32()
    }
}