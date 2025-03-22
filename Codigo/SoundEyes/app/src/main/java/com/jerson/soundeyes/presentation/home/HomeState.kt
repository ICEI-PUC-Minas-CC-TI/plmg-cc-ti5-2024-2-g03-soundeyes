package com.jerson.soundeyes.presentation.home

import com.jerson.soundeyes.consts.Const

data class HomeState(
    val optionResolution: List<String> = listOf(Const.DESEMPENHO, Const.QUALIDADE),
    val selectResolution: String = Const.DESEMPENHO,
    val optionTypeOfCamera: List<String> = listOf(Const.CAMERA_BLUETOOTH, Const.CAMERA_DISPOSITIVO),
    val selectCamera: String = Const.CAMERA_DISPOSITIVO,
    val callbackConnection: Boolean? = null,
    val errorDialog: Boolean = false,
    val errorBluetooth: Boolean = false,
    val isLoading: Boolean = false  // Novo estado para loading
)