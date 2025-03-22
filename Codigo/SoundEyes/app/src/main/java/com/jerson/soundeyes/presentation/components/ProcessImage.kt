package com.jerson.soundeyes.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.zIndex

@Composable
fun ProcessedImageScreen(processedBitmap: Bitmap?, paddingValues: PaddingValues) {
    if (processedBitmap != null) {
        Image(
            bitmap = processedBitmap.asImageBitmap(),
            contentDescription = "Imagem com as detecções",
            modifier = Modifier.fillMaxSize().padding(paddingValues).zIndex(2f)
        )
    }
}