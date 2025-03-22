package com.jerson.soundeyes.presentation.home

sealed class NavigationEvent {
    data object NavigateToBluetoothScreen: NavigationEvent()
    data object ShowErrorDialog : NavigationEvent()
}