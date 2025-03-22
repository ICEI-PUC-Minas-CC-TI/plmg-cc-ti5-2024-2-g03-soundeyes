package com.jerson.soundeyes.presentation.bluetooth


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jerson.soundeyes.presentation.components.CardLog
import com.jerson.soundeyes.ui.theme.SoundEyesTheme


@Composable
fun BluetoothScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    state: BluetoothState,
    //event: (BluetoothEvent) -> Unit
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
    )

    { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues).padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            state.bitmapImage?.let { image ->
                Image(
                    bitmap = image.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .width(500.dp)
                        .height(500.dp)
                )
            }
            HorizontalDivider()
            Spacer(Modifier.height(20.dp))
            Column(
                Modifier.fillMaxWidth()
            ) {
               CardLog(
                   timeClassifier = state.timeClassifier,
                   timeReceive = state.timeReceive,
                   sizeImage = state.sizeImage
               )
            }
            Spacer(Modifier.height(20.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(6.dp, RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                onClick = {
                onBack()
            }) {
                Text(text = "Voltar")
            }
            Spacer(modifier = Modifier.height(10.dp))

        }

    }
}

@Preview
@Composable
private fun BluetoothScreenPrev() {
    SoundEyesTheme {
        BluetoothScreen(
            onBack = {

            },
            state = BluetoothState()
        )
    }
}
