package com.jerson.soundeyes.feature_app.presentation.main

import ConectionBluetooth.envioConfigCamera
import ConectionBluetooth.receiveImageFromESP32
import ConectionBluetooth.simulateReceiveImageFromDrawable
import PermissionsRequest
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.jerson.soundeyes.R
import com.jerson.soundeyes.feature_app.presentation.navGraph.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher


@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {

    var onPermissionGranted by remember{ mutableStateOf( false) }
    val listConfig =  listOf("Velocidade", "Qualidade")
    var selectOption by remember{ mutableStateOf("Velocidade")}
  /*  var selectSound by remember{ mutableStateOf("Desempenho")}
    val listSound  =  listOf("Desempenho", "Qualidade")*/
    val context: Context = LocalContext.current
    var bitmapState by remember { mutableStateOf<Bitmap?>(null) }

    PermissionsRequest {
        onPermissionGranted = true
    }
    var showErrorPopup by remember { mutableStateOf(false) }

    if (showErrorPopup) {
        AlertDialog(
            onDismissRequest = { showErrorPopup = false },
            title = { Text(text = "Erro de Conexão") },
            text = { Text("Erro ao conectar com o dispositivo. Certifique-se de que ele está pareado e com o Bluetooth ligado.") },
            confirmButton = {
                Button(onClick = { showErrorPopup = false }) {
                    Text("OK")
                }
            }
        )
    }

    Surface(modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(vertical = 30.dp, horizontal = 20.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {

            Image(painter = painterResource(id = R.drawable.logo_preview), contentDescription = "Imagem de uma pessoa com deficiencia visual usando fones de ouvido")
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "SoundEyes",
                style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(20.dp))
            Card(modifier = Modifier
                .fillMaxWidth()) {
                Text(text = "Imagem",
                    Modifier.padding(top = 20.dp, start = 20.dp, bottom = 10.dp),
                    style = MaterialTheme.typography.headlineSmall)
                Row (horizontalArrangement = Arrangement.SpaceAround
                    ,modifier = Modifier.fillMaxWidth()){
                    listConfig.forEach {option ->
                        Row(modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectOption == option,
                                onClick = { selectOption = option } // Atualiza o estado ao clicar
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = option, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
           /* Spacer(modifier = Modifier.height(20.dp))
            Card(modifier = Modifier
                .fillMaxWidth()) {
                Text(text = "Voz",
                    Modifier.padding(top = 20.dp, start = 20.dp, bottom = 10.dp),
                    style = MaterialTheme.typography.headlineSmall)
                Row (horizontalArrangement = Arrangement.SpaceAround
                    ,modifier = Modifier.fillMaxWidth()){
                    listSound.forEach {option ->
                        Row(modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectOption == option,
                                onClick = { selectOption = option } // Atualiza o estado ao clicar
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = option, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }*/
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        when(selectOption) {
                            "Velocidade" -> {
                                envioConfigCamera(context, 2) { it: Boolean ->
                                    if (it) {
                                        goToScreenView(navController)
                                    } /*else {
                                        showErrorPopup = true
                                    }*/
                                }
                            }

                            "Qualidade" -> envioConfigCamera(context, 3) { it: Boolean ->
                                if (it) {
                                    goToScreenView(navController)
                                } /*else {
                                    showErrorPopup = true
                                }*/
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showErrorPopup = true
                    }
                }

            }) {
                Text(text = "Iniciar")
            }
        }
    }
}

fun goToScreenView(navController : NavController){
    navController.navigate(Route.YoloClassifierScreen.route){
        popUpTo(navController.graph.startDestinationId)
        launchSingleTop = true
    }
}