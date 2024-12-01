package com.jerson.soundeyes.feature_app.presentation.navGraph

import androidx.navigation.NamedNavArgument

sealed class Route(
    val route: String
){
    data object CameraScreen: Route(route = "cameraScreen/{destination}")
    data object GalleryScreen: Route(route = "galleryScreen/{destination}")
    data object MainScreen: Route(route = "mainScreen")
    data object YoloClassifierScreen: Route(route = "yoloClassifierScreen")
    data object MobileNetScreen: Route(route = "mobileNetScreen/{imageUri}")

}