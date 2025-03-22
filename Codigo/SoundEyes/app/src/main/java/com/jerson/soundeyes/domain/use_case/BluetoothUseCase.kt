package com.jerson.soundeyes.domain.use_case

import com.jerson.soundeyes.domain.repository.BluetoothRepository

data class BluetoothUseCase(
    val receiveImageUseCase: ReceiveImageUseCase,
    val sendConfigCameraUseCase: SendConfigCameraUseCase,
    val isBluetoothEnable: IsBluetoothEnableUseCase
)
