package com.jerson.soundeyes.presentation.home

sealed class HomeEvent {
    data object SendConfig: HomeEvent()
    data class OnSelectedResolution(val string: String): HomeEvent()
    data class OnSelectedCamera(val string: String): HomeEvent()
    data object ActivateBluetoothError :HomeEvent()
    data object OnDismissBluetoothError: HomeEvent()
    data object OnDismissDialogError: HomeEvent()
    data object ActivateDialogError: HomeEvent()
}
