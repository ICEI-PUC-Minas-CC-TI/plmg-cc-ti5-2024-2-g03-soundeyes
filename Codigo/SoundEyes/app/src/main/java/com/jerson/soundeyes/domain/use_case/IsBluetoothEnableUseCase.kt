package com.jerson.soundeyes.domain.use_case

import com.jerson.soundeyes.domain.repository.BluetoothRepository
import com.jerson.soundeyes.domain.repository.YoloRepository

class IsBluetoothEnableUseCase(private val bluetoothRepository: BluetoothRepository) {
    suspend operator fun invoke():Boolean{
       return bluetoothRepository.isEnableBluetooth()
    }
}
