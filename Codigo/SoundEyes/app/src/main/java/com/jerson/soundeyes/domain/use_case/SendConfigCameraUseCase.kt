package com.jerson.soundeyes.domain.use_case

import com.jerson.soundeyes.domain.repository.BluetoothRepository

class SendConfigCameraUseCase(private val bluetoothRepository: BluetoothRepository) {
    suspend operator fun invoke(value: Int,callback:(Boolean) -> Unit){
        bluetoothRepository.sendCameraConfig(value, callback)
    }
}