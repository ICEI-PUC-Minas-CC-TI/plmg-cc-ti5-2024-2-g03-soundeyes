package com.jerson.soundeyes.presentation.navGraph

sealed class Route(
    val route:String
) {
    data object HomeScreen: Route("HomeScreen")
    data object LocalCameraScreen: Route("LocalCameraScreen/{resolution}")
    data object BluetoothCameraScreen:Route("BluetoothCameraScreen")
    data object AboutScreen: Route("AboutScreen")
}