package com.jerson.soundeyes.presentation.camera

import android.graphics.Bitmap

sealed class LocalCameraEvent {
    data class ClassifyImage(val image:Bitmap): LocalCameraEvent()
    data object CloseClassify : LocalCameraEvent()
    data object ActivateModalSheet: LocalCameraEvent()
    data object OnDismissModalSheet: LocalCameraEvent()
    data object TakeImage: LocalCameraEvent()
}